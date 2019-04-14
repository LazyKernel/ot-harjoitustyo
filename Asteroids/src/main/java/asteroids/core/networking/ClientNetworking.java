package asteroids.core.networking;

import asteroids.core.containers.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientNetworking extends INetworking {

    private float deltaCounter = 0.0f;
    private String ip;

    public ClientNetworking(String ip, String username) {
        this.ip = ip;
        this.setUsername(username);
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void preUpdate(float deltaTime) {
        lastDelta = deltaTime;
        deltaCounter += deltaTime;
    }

    @Override
    public void postUpdate(float deltaTime) {
        if (deltaCounter < 0.016f) {
            return;
        }

        deltaCounter = 0.0f;
        for (INetworked n : networkeds) {
            if (n == null) {
                continue;
            }

            List<Object> list = new ArrayList<>();
            n.netSerialize(list, false);

            for (Object o : list) {
                NetPacket packet = new NetPacket();
                packet.netID = n.getNetId();
                packet.entityId = n.getEntity().getEntityId();
                packet.data = o;
                client.sendUDP(packet);
            }
        }

        removeQueuedComponents();
    }

    @Override
    public void destroy() {
        client.stop();
    }

    @Override
    public int getNewNetId() {
        return -1;
    }

    public void connect() {
        client.start();
        try {
            client.connect(5000, ip, portTCP, portUDP);
        } catch (IOException e) {
            System.out.println("Error while trying to connect.\n" + e.getMessage());
        }

        NetPacket packet = new NetPacket();
        packet.isNetRequest = true;
        packet.data = "connect;" + getUsername();
        client.sendTCP(packet);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetPacket) {
                    NetPacket packet = (NetPacket) object;
                    handlePacket(packet);
                }
            }

            public void disconnected(Connection connection) {
                // TODO: disconnected
                System.out.println("Disconnected from server.");
            }
        });
    }

    private void handlePacket(NetPacket packet) {
        if (packet.isNetRequest) {
            handleStringPacket((String) packet.data);
            return;
        }

        if (packet.netID < 0 || packet.entityId < 0) {
            return;
        }

        Entity e = getRenderer().getEntity(packet.entityId);
        if (e != null) {
            List<INetworked> networks = e.getComponentsOfType(INetworked.class);

            for (INetworked n : networks) {
                if (n.getNetId() == packet.netID) {
                    List<Object> list = new ArrayList<>();
                    list.add(packet.data);
                    n.netDeserialize(list, lastDelta, false);
                    break;
                }
            }
        }
    }

    private void handleStringPacket(String data) {
        String[] split = data.split(";", 2);

        if (split[0].equals("e")) {
            getRenderer().addEntity(decodeEntity(split[1]));
        } else if (split[0].equals("r")) {
            Entity e = getRenderer().getEntity(Integer.parseInt(split[1]));

            if (e != null) {
                List<INetworked> list = e.getComponentsOfType(INetworked.class);

                if (!list.isEmpty()) {
                    queuedForRemoval.add(list.get(0));
                }
            }
        }
    }
}
