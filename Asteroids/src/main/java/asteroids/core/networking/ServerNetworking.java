package asteroids.core.networking;

import asteroids.core.containers.Entity;
import asteroids.game.components.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Networking for server side
 */
public class ServerNetworking extends INetworking {

    private int numPlayers = 0;
    private int maxPlayers = 255;

    public ServerNetworking(int maxPlayers) {
        isServer = true;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Starts a server
     */
    @Override
    public void init() {
        System.out.println("Starting server.");
        server.start();
        try {
            server.bind(portTCP, portUDP);
        } catch (IOException e) {
            System.out.println("Error while trying to bind to a port.\n" + e.getMessage());
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetPacket) {
                    NetPacket packet = (NetPacket) object;
                    handlePacket(packet, connection);
                }
            }

            public void disconnected(Connection connection) {
                NetConnection c = (NetConnection) connection;

                if (c.name.isEmpty()) {
                    System.out.println("Spectator disconnected.");
                } else {
                    if (!c.disconnectedByServer) {
                        System.out.println("Player " + c.name + " disconnected.");
                        numPlayers--;
                    }
                }

                for (int i : c.ownedIds) {
                    queueForRemoval(networkeds.getWithNetId(i));
                }
            }
        });
    }

    @Override
    public void preUpdate(float deltaTime) {
        lastDelta = deltaTime;
    }

    /**
     * Sends the current state to all connected clients
     * @param deltaTime seconds since last update
     */
    @Override
    public void postUpdate(float deltaTime) {
        for (INetworked n : networkeds) {
            if (n == null) {
                continue;
            }

            List<Object> list = new ArrayList<>();
            n.netSerialize(list, true);

            for (Object o : list) {
                NetPacket packet = new NetPacket();
                packet.netID = n.getNetId();
                packet.entityId = n.getEntity().getEntityId();
                packet.data = o;
                server.sendToAllUDP(packet);
            }
        }

        removeQueuedComponents();
    }

    /**
     * Disconnects the server
     */
    @Override
    public void destroy() {
        server.stop();
    }

    @Override
    public int getNewNetId() {
        return networkedComponentCounter++;
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    private void handlePacket(NetPacket packet, Connection connection) {
        if (packet.isNetRequest) {
            if (packet.data instanceof String) {
                handleStringPacket((String) packet.data, connection);
            }
            return;
        }

        if (packet.netID < 0 || packet.entityId < 0) {
            return;
        }

        NetConnection c = (NetConnection) connection;

        if (!c.ownedIds.contains(packet.netID)) {
            return;
        }

        Entity e = getRenderer().getEntity(packet.entityId);

        if (e != null) {
            List<INetworked> networks = e.getComponentsOfType(INetworked.class);

            for (INetworked n : networks) {
                if (n.getNetId() == packet.netID) {
                    List<Object> list = new ArrayList<>();
                    list.add(packet.data);
                    n.netDeserialize(list, lastDelta, true);
                    break;
                }
            }
        }
    }

    private void handleStringPacket(String data, Connection connection) {
        String[] split = data.split(";");
        switch (split[0]) {
            case "connect":
                if (split.length < 2 || split[1].isEmpty()) {
                    System.out.println("Spectator connected.");
                    sendStateToConnectedClient(connection);
                } else {
                    System.out.println("Player " + split[1] + " connected.");

                    if (numPlayers >= maxPlayers) {
                        NetConnection conn = (NetConnection) connection;
                        conn.disconnectedByServer = true;
                        conn.sendTCP("d;Server full.");
                        conn.close();
                        return;
                    }

                    numPlayers++;
                    sendStateToConnectedClient(connection);
                    INetworked n = createPlayer(split[1]);
                    NetConnection c = (NetConnection) connection;
                    c.ownedIds.add(n.getNetId());
                    c.name = split[1];
                }
                break;
        }
    }

    private INetworked createPlayer(String owner) {
        Entity player = new Entity();
        Player playerComponent = new Player();
        playerComponent.setOwner(owner);
        getRenderer().addEntity(player);
        player.addComponent(playerComponent);
        return playerComponent;
    }

    private void sendStateToConnectedClient(Connection connection) {
        for (INetworked n : networkeds) {
            if (n == null) {
                continue;
            }

            sendNewEntity(n.getEntity(), connection);
        }
    }

    private void sendNewEntity(Entity entity, Connection connection) {
        sendNewEntity(encodeEntity(entity), connection);
    }

    private void sendNewEntity(String entity, Connection connection) {
        NetPacket packet = new NetPacket();
        packet.isNetRequest = true;
        packet.data = "e;" + entity;
        connection.sendTCP(packet);
    }

    /**
     * Add a new networked component, initialize it and set its net id. Sends the new entity containing the component to all clients.
     * @param component component to add
     */
    @Override
    public void addNetworkedComponent(INetworked component) {
        component.setNetId(getNewNetId());
        networkeds.add(component);

        String entity = encodeEntity(component.getEntity());
        for (Connection conn : server.getConnections()) {
            sendNewEntity(entity, conn);
        }
    }

    /**
     * Remove a networked component. Send a message to all clients to remove the entity containing the component
     * @param component component to remove
     */
    @Override
    public void removeNetworkedComponent(INetworked component) {
        if (component == null) {
            return;
        }

        NetPacket packet = new NetPacket();
        packet.isNetRequest = true;
        packet.data = "r;" + component.getEntity().getEntityId();
        server.sendToAllTCP(packet);

        networkeds.remove(component);
    }
}
