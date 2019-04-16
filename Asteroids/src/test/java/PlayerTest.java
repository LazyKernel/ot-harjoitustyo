import asteroids.core.containers.Entity;
import asteroids.core.graphics.Renderer;
import asteroids.core.input.KeyboardHandler;
import asteroids.core.networking.OfflineNetworking;
import asteroids.core.networking.ServerNetworking;
import asteroids.game.Game;
import asteroids.game.components.Player;
import org.joml.Vector2f;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.JTextComponent;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerTest {
    Entity entity;
    Player player;
    KeyboardHandler handler;
    Renderer renderer;
    ServerNetworking networking;

    @Before
    public void setUp() {
        networking = new ServerNetworking();
        networking.setUsername("test");
        renderer = new Renderer(new Game(), networking);
        entity = new Entity();
        renderer.addEntity(entity);
        player = new Player();
        player.setOwner("test");
        entity.addComponent(player);
        handler = new KeyboardHandler();
    }

    @Test
    public void testRotateRight() {
        handler.invoke(0, GLFW_KEY_D, 0, GLFW_PRESS, 0);
        player.update(1);
        handler.invoke(0, GLFW_KEY_D, 0, GLFW_RELEASE, 0);
        assertEquals(-Math.PI * 2, player.getTransform().getRotation(), 0.01f);
    }

    @Test
    public void testRotateLeft() {
        handler.invoke(0, GLFW_KEY_A, 0, GLFW_PRESS, 0);
        player.update(1);
        handler.invoke(0, GLFW_KEY_A, 0, GLFW_RELEASE, 0);
        assertEquals(Math.PI * 2, player.getTransform().getRotation(), 0.01f);
    }

    @Test
    public void testForward() {
        player.getTransform().setPosition(new Vector2f(0, 0));
        handler.invoke(0, GLFW_KEY_W, 0, GLFW_PRESS, 0);
        player.update(1);
        handler.invoke(0, GLFW_KEY_W, 0, GLFW_RELEASE, 0);
        assertEquals(0, player.getTransform().getPosition().x, 0.01f);
        assertEquals(500, player.getTransform().getPosition().y, 0.01f);
    }

    @Test
    public void testBack() {
        player.getTransform().setPosition(new Vector2f(0, 0));
        handler.invoke(0, GLFW_KEY_S, 0, GLFW_PRESS, 0);
        player.update(1);
        handler.invoke(0, GLFW_KEY_S, 0, GLFW_RELEASE, 0);
        assertEquals(0, player.getTransform().getPosition().x, 0.01f);
        assertEquals(-500, player.getTransform().getPosition().y, 0.01f);
    }
}
