package asteroids.core.networking;

import asteroids.core.containers.EntityComponent;

import java.util.List;

public abstract class INetworked extends EntityComponent {
    private int netId = -1;

    public abstract void netSerialize(List<Object> objects, boolean isServer);
    public abstract void netDeserialize(List<Object> objects, float deltaTime, boolean isServer);

    public void setNetId(int id) {
        netId = id;
    }

    public int getNetId() {
        return netId;
    }
}
