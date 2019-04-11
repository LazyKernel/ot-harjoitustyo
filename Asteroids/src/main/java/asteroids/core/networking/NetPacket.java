package asteroids.core.networking;

public class NetPacket {
    public int senderId = -1;
    public int receiverId = -1;
    public int entityId = -1;
    public int netID = -1;
    public Object data = null;
}
