import asteroids.core.containers.Entity;
import asteroids.core.graphics.Renderer;
import asteroids.core.networking.ServerNetworking;
import asteroids.game.AsteroidsGame;
import asteroids.game.components.Bullet;
import org.joml.Vector2f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BulletTest {

    Entity entity;
    Bullet bullet;
    Renderer renderer;
    ServerNetworking networking;

    @Before
    public void setUp() {
        networking = new ServerNetworking();
        networking.setUsername("test");
        renderer = new Renderer(new AsteroidsGame(), networking);
        entity = new Entity();
        renderer.addEntity(entity);
        bullet = new Bullet();
        bullet.setOwner("test");
        entity.addComponent(bullet);
    }

    @Test
    public void testUpdate() {
        bullet.getTransform().setPosition(new Vector2f(0, 0));
        bullet.update(0.25f);
        assertEquals(0, bullet.getTransform().getPosition().x, 0.01f);
        assertEquals(300, bullet.getTransform().getPosition().y, 0.01f);
    }
}
