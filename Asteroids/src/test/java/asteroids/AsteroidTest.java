package asteroids;

import asteroids.core.containers.Entity;
import asteroids.core.graphics.Renderer;
import asteroids.core.networking.ServerNetworking;
import asteroids.game.AsteroidsGame;
import asteroids.game.components.Asteroid;
import org.joml.Vector2f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsteroidTest {
    Entity entity;
    Asteroid asteroid;
    Renderer renderer;
    ServerNetworking networking;

    @Before
    public void setUp() {
        networking = new ServerNetworking(4);
        networking.setUsername("test");
        renderer = new Renderer(new AsteroidsGame(), networking);
        entity = new Entity();
        renderer.addEntity(entity);
        asteroid = new Asteroid();
        asteroid.setOwner("test");
        entity.addComponent(asteroid);
    }

    @Test
    public void testUpdate() {
        asteroid.getTransform().setPosition(new Vector2f(0, 0));
        asteroid.update(1.0f);
        assertEquals(0, asteroid.getTransform().getPosition().x, 0.01f);
        assertEquals(50.0f, asteroid.getTransform().getPosition().y, 0.01f);
    }
}
