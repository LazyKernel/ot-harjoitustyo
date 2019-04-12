package asteroids.core.networking;

import asteroids.core.containers.Entity;
import asteroids.core.containers.EntityComponent;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientNetworking extends INetworking {

    Client client = new Client();

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
    }

    @Override
    public void postUpdate(float deltaTime) {
        for (INetworked n : networkeds) {
            List<Object> list = new ArrayList<>();
            n.netSerialize(list, true);

            for (Object o : list) {
                NetPacket packet = new NetPacket();
                packet.netID = n.getNetId();
                packet.entityId = n.getEntity().getEntityId();
                packet.data = o;
                client.sendUDP(packet);
            }
        }
    }

    @Override
    public int getNewNetId() {
        return -1;
    }

    public void connect(String ip) {
        client.start();
        try {
            client.connect(5000, ip, portTCP, portUDP);
        } catch (IOException e) {
            System.out.println("Error while trying to connect.\n" + e.getMessage());
        }

        NetPacket packet = new NetPacket();
        packet.isNetRequest = true;
        packet.data = "connect";
        client.sendTCP(packet);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetPacket) {
                    NetPacket packet = (NetPacket) object;
                    handlePacket(packet);
                }
            }
        });
    }

    private void handlePacket(NetPacket packet) {
        if (packet.netID < 0 || packet.entityId < 0) {
            return;
        }

        if (packet.isNetRequest) {
            handleStringPacket((String) packet.data);
            return;
        }

        Entity e = getRenderer().getEntity(packet.entityId);
        if (e != null) {
            List<INetworked> networkeds = e.getComponentsOfType(INetworked.class);

            for (INetworked n : networkeds) {
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
        }
    }
}
