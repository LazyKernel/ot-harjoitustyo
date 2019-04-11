package asteroids.core.networking;

import asteroids.core.containers.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerNetworking extends INetworking {

    private static final int serverId = 0; // server is always 0

    private float lastDelta = 0.0f;

    private Server server = new Server();

    private HashMap<Integer, Integer> ownerMap = new HashMap<>(); // netId -> owner connection id

    public ServerNetworking() {
        isServer = true;
    }

    @Override
    public void init() {
        server.start();
        try {
            server.bind(55555, 55556);
        } catch (IOException e) {
            System.out.println("Error while trying to bind to a port.\n" + e.getMessage());
        }

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetPacket) {
                    NetPacket packet = (NetPacket) object;
                    handlePacket(packet);
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
                Integer receiver = ownerMap.get(n.getNetId());

                if (receiver == null) {
                    continue;
                }

                NetPacket packet = new NetPacket();
                packet.senderId = serverId;
                packet.netID = n.getNetId();
                packet.entityId = n.getEntity().getEntityId();
                packet.receiverId = receiver;
                packet.data = o;
                sendPacket(packet);
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

    private void handlePacket(NetPacket packet) {
        // TODO: verify that the owner is actually the owner of the entity
        if (packet.netID < 0) {
            return;
        }

        Entity e = getRenderer().getEntity(packet.entityId);

        if (e != null) {
            List<INetworked> networkeds = e.getComponentsOfType(INetworked.class);

            for (INetworked n : networkeds) {
                if (n.getNetId() == packet.netID) {
                    List<Object> list = new ArrayList<>();
                    list.add(packet.data);
                    n.netDeserialize(list, lastDelta, true);
                }
            }
        }
    }

    private void sendPacket(NetPacket packet) {
        Connection connection = server.getConnections()[packet.receiverId];
        connection.sendUDP(packet);
    }
}
