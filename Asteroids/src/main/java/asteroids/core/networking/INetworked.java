package asteroids.core.networking;

import asteroids.core.containers.EntityComponent;

import java.util.List;

/**
 * Extension of normal EntityComponent. This component is replicated over the net with server authorization.
 * @see EntityComponent
 */
public abstract class INetworked extends EntityComponent {
    private int netId = -1;
    private String owner = "";

    /**
     * Serialize the state of the component
     * @param objects list to add serialized objects to
     * @param isServer is this a server
     */
    public abstract void netSerialize(List<Object> objects, boolean isServer);

    /**
     * Deserialize a state received from the net
     * @param objects objects to deserialize
     * @param deltaTime time since last update
     * @param isServer is this a server
     */
    public abstract void netDeserialize(List<Object> objects, float deltaTime, boolean isServer);

    public void setNetId(int id) {
        netId = id;
    }

    public int getNetId() {
        return netId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
