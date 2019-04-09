package asteroids.core.networking;

import java.util.ArrayList;
import java.util.List;

public class OfflineNetworking extends INetworking {

    public OfflineNetworking() {
        isServer = true;
    }

    @Override
    public void preUpdate(float deltaTime) {
        waitingForSerialization.clear();

        for (INetworked n : networkeds) {
            List<Object> list = new ArrayList<>();
            n.netSerialize(list, false);

            if (!list.isEmpty()) {
                waitingForSerialization.put(n.getNetId(), list);
            }
        }

        for (INetworked n : networkeds) {
            List<Object> list = waitingForSerialization.get(n.getNetId());

            if (list != null) {
                n.netDeserialize(list, deltaTime, true);
            }
        }
    }

    @Override
    public void postUpdate(float deltaTime) {
        waitingForSerialization.clear();

        for (INetworked n : networkeds) {
            List<Object> list = new ArrayList<>();
            n.netSerialize(list, true);

            if (!list.isEmpty()) {
                waitingForSerialization.put(n.getNetId(), list);
            }
        }

        for (INetworked n : networkeds) {
            List<Object> list = waitingForSerialization.get(n.getNetId());

            if (list != null) {
                n.netDeserialize(list, deltaTime, false);
            }
        }

        removeQueuedComponents();
    }

    @Override
    public int getNewNetId() {
        return networkedComponentCounter++;
    }
}
