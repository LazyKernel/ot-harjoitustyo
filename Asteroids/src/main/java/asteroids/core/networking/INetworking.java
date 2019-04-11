package asteroids.core.networking;

import asteroids.core.graphics.Renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class INetworking {
    protected boolean isServer = false;
    protected List<INetworked> networkeds = new ArrayList<>();
    protected HashMap<Integer, List<Object>> waitingForSerialization = new HashMap<>();

    protected List<INetworked> queuedForRemoval = new ArrayList<>();

    protected int networkedComponentCounter = 0;

    private Renderer renderer;

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
}
