package asteroids.game.components;

import asteroids.core.Entity;
import asteroids.core.EntityComponent;
import asteroids.core.Transform;
import asteroids.core.graphics.Mesh;
import asteroids.core.input.KeyboardHandler;
import asteroids.core.networking.INetworked;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;

public class Player extends EntityComponent implements INetworked {
    private static final Vector3f[] POINTS = new Vector3f[]{ new Vector3f(-0.75f, -1.0f, 0.0f), new Vector3f(0.75f, -1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(-0.75f, -1.0f, 0.0f) };
    private Mesh playerMesh;
    private int inputFlags = 0;

    @Override
    public void init() {
        playerMesh = new Mesh();
        playerMesh.setPoints(POINTS, GL_LINE_STRIP);
        getTransform().setScale(new Vector2f(25, 25));
        getEntity().addComponent(playerMesh);
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float deltaTime) {
        if (KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
            getTransform().rotate((float) Math.PI * deltaTime * 2);
            inputFlags |= 0x1;
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
            getTransform().rotate(-(float) Math.PI * deltaTime * 2);
            inputFlags |= 0x2;
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_W)) {
            Vector3f dir = new Vector3f(0.0f, 1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0, 0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 500));
            inputFlags |= 0x4;
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_S)) {
            Vector3f dir = new Vector3f(0.0f, -1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0, 0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 500));
            inputFlags |= 0x8;
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)) {
            Vector2f pos = getTransform().getPosition();
            Vector2f scale = getTransform().getScale();
            float rotation = getTransform().getRotation();

            Vector3f spawnPos = new Vector3f(0.0f, scale.y, 1.0f);
            spawnPos.rotateZ(rotation);
            spawnPos.add(pos.x, pos.y, 0.0f);

            Entity bulletEntity = new Entity();
            bulletEntity.addComponent(new Bullet());

            bulletEntity.getTransform().setPosition(spawnPos);
            bulletEntity.getTransform().setRotation(getTransform().getRotation());
            getEntity().getRenderer().addEntity(bulletEntity);
            inputFlags |= 0xF;
    }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void netSerialize(List<Object> objects, boolean isServer) {
        objects.add(inputFlags);
        inputFlags = 0;
    }

    @Override
    public void netDeserialize(List<Object> objects, float deltaTime, boolean isServer) {
        for (Object o : objects) {
            if (o.getClass() == Transform.class) {
                setTransform((Transform)o);
            }

            if (isServer && o.getClass() == int.class) {

            }
        }
    }
}
