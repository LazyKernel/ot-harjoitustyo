package asteroids.core.networking;

import java.util.List;

public interface INetworked {
    void netSerialize(List<Object> objects, boolean isServer);
    void netDeserialize(List<Object> objects, float deltaTime, boolean isServer);
}
