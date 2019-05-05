package asteroids.core.networking;

import com.esotericsoftware.kryonet.Connection;

import java.util.HashSet;

/**
 * Extends base Kryonet connection
 */
public class NetConnection extends Connection {
    public String name = "";
    public boolean disconnectedByServer = false;
    public HashSet<Integer> ownedIds = new HashSet<>();
}
