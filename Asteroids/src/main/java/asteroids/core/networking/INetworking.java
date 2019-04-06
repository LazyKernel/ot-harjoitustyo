package asteroids.core.networking;

import java.util.ArrayList;
import java.util.List;

public abstract class INetworking {
    private boolean isServer = false;
    private List<INetworked> networkeds = new ArrayList<>();

    public void addNetworkedComponent(INetworked component) {
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

    public abstract void update();
}
