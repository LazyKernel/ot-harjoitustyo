package asteroids.core.networking;

import asteroids.core.containers.Entity;
import asteroids.core.graphics.Renderer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class INetworking {
    final int portTCP = 55555;
    final int portUDP = 55556;

    protected Server server = new Server();
    protected Client client = new Client();

    protected boolean isServer = false;
    protected List<INetworked> networkeds = new ArrayList<>();
    protected HashMap<Integer, List<Object>> waitingForSerialization = new HashMap<>();

    protected List<INetworked> queuedForRemoval = new ArrayList<>();

    protected int networkedComponentCounter = 0;

    private Renderer renderer;

    protected float lastDelta = 0.0f;

    public INetworking() {
        registerClass(NetPacket.class);
    }

    public void addNetworkedComponent(INetworked component) {
        if (isServer) {
            component.setNetId(getNewNetId());
            // TODO: send net id over net
        }

        networkeds.add(component);
    }

    // server cant remove components before sending the message to clients
    public void queueForRemoval(INetworked component) {
        queuedForRemoval.add(component);
    }

    protected void removeQueuedComponents() {
        for (INetworked n : queuedForRemoval) {
            n.getEntity().getRenderer().removeEntity(n.getEntity());
        }
        queuedForRemoval.clear();
    }

    public void removeNetworkedComponent(INetworked component) {
        networkeds.remove(component);
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    public boolean getIsServer() {
        return isServer;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract boolean isOffline();

    public abstract void init();

    public abstract void preUpdate(float deltaTime);
    public abstract void postUpdate(float deltaTime);

    public abstract int getNewNetId();

    protected String encodeEntity(Entity e) {
        StringBuilder builder = new StringBuilder();
        builder.append(e.getEntityId());

        List<INetworked> list = e.getComponentsOfType(INetworked.class);
        for (INetworked n : list) {
            builder.append(";").append(n.getClass().getSuperclass().getTypeName()).append(";").append(n.getNetId());
        }

        return builder.toString();
    }

    protected Entity decodeEntity(String data) {
        String[] split = data.split(";");
        Entity e = new Entity();
        e.setEntityId(Integer.parseInt(split[0]));

        for (int i = 1; i < split.length; i += 2) {
            try {
                INetworked n = (INetworked) Class.forName(split[i]).newInstance();
                n.setNetId(Integer.parseInt(split[i+1]));
                e.addComponent(n);
            } catch (ClassNotFoundException ex) {
                System.out.println("Class not found.\n" + ex.getMessage());
            } catch (InstantiationException ex) {
                System.out.println("Couldn't instantiate class.\n" + ex.getMessage());
            } catch (IllegalAccessException ex) {
                System.out.println("Couldn't access class.\n" + ex.getMessage());
            }
        }

        return e;
    }

    // game has to do this
    public void registerClass(Class type) {
        server.getKryo().register(type);
        client.getKryo().register(type);
    }
}
