import asteroids.Main;
import asteroids.core.networking.ClientNetworking;
import asteroids.core.networking.OfflineNetworking;
import asteroids.core.networking.ServerNetworking;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    Main main;

    @Before
    public void setUp() {
        main = new Main();
    }

    @Test
    public void testServer() {
        main.setArgs(new String[]{ "-s" });
        assertTrue(main.getNetworking() instanceof ServerNetworking);
    }

    @Test
    public void testOffline() {
        main.setArgs(new String[] {"-o"});
        assertTrue(main.getNetworking() instanceof OfflineNetworking);
        main = new Main();
        main.setArgs(new String[]{});
        assertTrue(main.getNetworking() instanceof OfflineNetworking);
    }

    @Test
    public void testVisualDebug() {
        main.setArgs(new String[] {"-v"});
        assertTrue(main.isVisualDebug());
    }

    @Test
    public void testClient() {
        main.setArgs(new String[] {"-c", "okay.works", "lojoj"});
        assertTrue(main.getNetworking() instanceof ClientNetworking);
    }
}
