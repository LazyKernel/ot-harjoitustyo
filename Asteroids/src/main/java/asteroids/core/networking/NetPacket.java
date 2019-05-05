package asteroids.core.networking;

/**
 * NetPacket sent over the net
 */
public class NetPacket {
    public int entityId = -1;
    public int netID = -1;
    public boolean isNetRequest = false;
    public Object data = null;

    @Override
    public String toString() {
        return "eid: " + entityId + "\nnetid: " + netID + "\nisNetReq: " + isNetRequest + "\ndata:\n" + data;
    }
}
