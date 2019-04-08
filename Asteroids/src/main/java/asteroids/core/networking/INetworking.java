package asteroids.core.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class INetworking {
    protected boolean isServer = false;
    protected List<INetworked> networkeds = new ArrayList<>();
    protected HashMap<Integer, List<Object>> waitingForSerialization = new HashMap<>();

    protected int networkedComponentCounter = 0;

    public void addNetworkedComponent(INetworked component) {
        if (isServer) {
            component.setNetId(getNewNetId());
            // TODO: send net id over net
        }

        networkeds.add(component);
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

    public abstract void preUpdate(float deltaTime);
    public abstract void postUpdate(float deltaTime);

    public abstract int getNewNetId();
}
