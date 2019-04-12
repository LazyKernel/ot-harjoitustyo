package asteroids.core.networking;

import asteroids.core.containers.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerNetworking extends INetworking {

    private HashMap<Integer, Connection> ownerMap = new HashMap<>(); // netId -> owner connection

    public ServerNetworking() {
        isServer = true;
    }

    @Override
    public void init() {
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
        });
    }

    @Override
    public void preUpdate(float deltaTime) {
        lastDelta = deltaTime;
    }

    @Override
    public void postUpdate(float deltaTime) {
        for (INetworked n : networkeds) {
            List<Object> list = new ArrayList<>();
            n.netSerialize(list, true);

            for (Object o : list) {
                Connection receiver = ownerMap.get(n.getNetId());

                if (receiver == null) {
                    continue;
                }

                NetPacket packet = new NetPacket();
                packet.netID = n.getNetId();
                packet.entityId = n.getEntity().getEntityId();
                packet.data = o;
                receiver.sendUDP(packet);
            }
        }
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
        if (packet.netID < 0 || packet.entityId < 0) {
            return;
        }

        // TODO: check if is actually the owner

        if (packet.isNetRequest) {
            if (packet.data instanceof String) {
                handleStringPacket((String) packet.data, connection);
            }
        }

        Entity e = getRenderer().getEntity(packet.entityId);

        if (e != null) {
            List<INetworked> networkeds = e.getComponentsOfType(INetworked.class);

            for (INetworked n : networkeds) {
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
        switch (data) {
            case "connect":
                sendStateToConnectedClient(connection);
                break;
        }
    }

    private void sendStateToConnectedClient(Connection connection) {
        for (INetworked n : networkeds) {
            sendNewEntity(n.getEntity(), connection);
        }
    }

    private void sendNewEntity(Entity entity, Connection connection) {
        NetPacket packet = new NetPacket();
        packet.isNetRequest = true;
        packet.data = "e;" + encodeEntity(entity);
        connection.sendTCP(packet);
    }
}
