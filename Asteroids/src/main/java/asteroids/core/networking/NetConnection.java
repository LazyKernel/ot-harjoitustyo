package asteroids.core.networking;

import com.esotericsoftware.kryonet.Connection;

import java.util.HashSet;

public class NetConnection extends Connection {
    public String name = "";
    public HashSet<Integer> ownedIds = new HashSet<>();
}
