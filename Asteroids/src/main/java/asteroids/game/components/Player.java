package asteroids.game.components;

import asteroids.core.containers.Entity;
import asteroids.core.containers.Transform;
import asteroids.core.graphics.Mesh;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.input.KeyboardHandler;
import asteroids.core.networking.INetworked;
import asteroids.core.physics.ICollider;
import asteroids.game.AsteroidsGame;
import asteroids.game.ui.ScoreWindow;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;

/**
 * Player component
 */
public class Player extends INetworked implements ICollider {
    private static final Vector3f[] POINTS = new Vector3f[]{ new Vector3f(-0.75f, -1.0f, 0.0f), new Vector3f(0.75f, -1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(-0.75f, -1.0f, 0.0f) };
    private static final Vector2f[] RESPAWN_POINTS = new Vector2f[] {
        new Vector2f(-400.0f, -300.0f), new Vector2f(400.0f, 300.0f),
        new Vector2f(-400.0f, 300.0f), new Vector2f(400.0f, -300.0f)
    };
    private static final Vector3f NORMAL_COLOR = new Vector3f(1.0f, 0.0f, 1.0f);
    private static final Vector3f IMMUNE_COLOR = new Vector3f(0.7f, 0.7f, 0.7f);

    private Mesh playerMesh;
    private int inputFlags = 0;

    private int score = 0;
    private boolean scoreUpdated = false;

    private float largestRadius = 0.0f;

    private float immuneTime = 0.0f;

    private Random rand = new Random();

    /**
     * If this is not a headless server creates the player mesh and updates score display
     */
    @Override
    public void init() {
        if (!getEntity().getRenderer().getIsHeadlessServer() || getEntity().getRenderer().getIsServerVisualDebug()) {
            playerMesh = new Mesh();
            playerMesh.setPoints(POINTS, GL_LINE_STRIP);
            getEntity().addComponent(playerMesh);

            if (getUi() != null) {
                getUi().addPlayer(this);
                getUi().updatePlayer(this);
            }
        }

        getTransform().setScale(new Vector2f(25, 25));
    }

    /**
     * Controls whether to render the mesh and mesh color
     */
    @Override
    public void render() {
        if (immuneTime > 3.0f) {
            playerMesh.setRender(false);
        } else if (immuneTime > 0.0f) {
            playerMesh.setRender(true);
            playerMesh.setColor(IMMUNE_COLOR);
        } else {
            playerMesh.setRender(true);
            playerMesh.setColor(NORMAL_COLOR);
        }
    }

    /**
     * Updates keyboard input on clients if they own the player and clamps the player to the play area
     * @param deltaTime seconds since last update
     */
    @Override
    public void update(float deltaTime) {
        if (immuneTime > 0.0f) {
            immuneTime -= deltaTime;
        }

        if (getOwner().equals(getEntity().getRenderer().getNetworking().getUsername())) {
            if (KeyboardHandler.isKeyDown(GLFW_KEY_A)) {
                handleInput(0x1, deltaTime, false);
                inputFlags |= 0x1;
            }

            if (KeyboardHandler.isKeyDown(GLFW_KEY_D)) {
                handleInput(0x2, deltaTime, false);
                inputFlags |= 0x2;
            }

            if (KeyboardHandler.isKeyDown(GLFW_KEY_W)) {
                handleInput(0x4, deltaTime, false);
                inputFlags |= 0x4;
            }

            if (KeyboardHandler.isKeyDown(GLFW_KEY_S)) {
                handleInput(0x8, deltaTime, false);
                inputFlags |= 0x8;
            }

            if (KeyboardHandler.isKeyPressed(GLFW_KEY_SPACE)) {
                shoot(false);
                inputFlags |= 0x10;
            }
        }

        Vector2f pos = getTransform().getPosition();
        Vector2f scale = getTransform().getScale();
        if (pos.x > 800 - scale.x) {
            pos.x = 800 - scale.x;
        } else if (pos.x < -800 + scale.x) {
            pos.x = -800 + scale.x;
        }

        if (pos.y > 600 - scale.y) {
            pos.y = 600 - scale.y;
        } else if (pos.y < -600 + scale.y) {
            pos.y = -600 + scale.y;
        }

        getTransform().setPosition(pos);
    }

    /**
     * Removes the player from the ui
     */
    @Override
    public void destroy() {
        if (getUi() != null) {
            getUi().removePlayer(this);
        }
    }

    /**
     * If this is a server, updates the transform, immuneTime and score. If this is a client, sends input flags to server.
     * @param objects list to add serialized objects to
     * @param isServer is this a server
     */
    @Override
    public void netSerialize(List<Object> objects, boolean isServer) {
        if (isServer) {
            objects.add(getTransform());

            if (scoreUpdated) {
                objects.add(score);
                scoreUpdated = false;
            }

            objects.add(immuneTime);
        } else {
            objects.add(inputFlags);
            inputFlags = 0;
        }
    }

    /**
     * If this is a client sets transform, immune time and score from the server and updates ui accordingly.
     * If this is a server updates player position from input flags.
     * @param objects objects to deserialize
     * @param deltaTime time since last update
     * @param isServer is this a server
     */
    @Override
    public void netDeserialize(List<Object> objects, float deltaTime, boolean isServer) {
        for (Object o : objects) {
            if (o instanceof Transform) {
                setTransform((Transform) o);
            }

            if (!isServer && o instanceof Float) {
                immuneTime = (float) o;
            }

            if (!isServer && o instanceof Integer) {
                score = (int) o;

                if (getUi() != null) {
                    getUi().updatePlayer(this);
                }
            }

            if (isServer && o instanceof Integer) {
                handleInput((int) o, deltaTime, true);
            }
        }
    }

    /**
     * Change player position and orientation from input flags.
     * @param inputFlags input flags
     * @param deltaTime seconds since last update
     * @param isServer is this a server
     */
    public void handleInput(int inputFlags, float deltaTime, boolean isServer) {
        if (immuneTime > 3.0f) {
            return;
        }

        if ((inputFlags & 0x1) != 0) {
            getTransform().rotate((float) Math.PI * deltaTime * 2);
        }

        if ((inputFlags & 0x2) != 0) {
            getTransform().rotate(-(float) Math.PI * deltaTime * 2);
        }

        if ((inputFlags & 0x4) != 0) {
            Vector3f dir = new Vector3f(0.0f, 1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0, 0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 500));
        }

        if ((inputFlags & 0x8) != 0) {
            Vector3f dir = new Vector3f(0.0f, -1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0, 0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 500));
        }

        if ((inputFlags & 0x10) != 0) {
            shoot(isServer);
        }
    }

    private void shoot(boolean isServer) {
        if (!isServer) {
            return;
        }

        Vector2f pos = getTransform().getPosition();
        Vector2f scale = getTransform().getScale();
        float rotation = getTransform().getRotation();

        Vector3f spawnPos = new Vector3f(0.0f, scale.y, 1.0f);
        spawnPos.rotateZ(rotation);
        spawnPos.add(pos.x, pos.y, 0.0f);

        Entity bulletEntity = new Entity();
        Bullet bullet = new Bullet(this);
        bullet.setOwner(getOwner());
        getEntity().getRenderer().addEntity(bulletEntity);
        bulletEntity.addComponent(bullet);

        bulletEntity.getTransform().setPosition(spawnPos);
        bulletEntity.getTransform().setRotation(getTransform().getRotation());
        immuneTime = 0.0f;
    }

    @Override
    public Vector3f[] getPoints() {
        return POINTS;
    }

    /**
     * Ignored if the player is currently immune.
     * If this is a server and another player's bullet hit this player, adds 50 score to other player and respawns this.
     * If this is a server and this player hit another player, respawns this player.
     * If this is a server and this player hit an asteroid, deducts 5 points from this player's score and respawns this player.
     * @param other collided with other collider
     * @param isServer is this a server
     */
    @Override
    public void hit(ICollider other, boolean isServer) {
        if (immuneTime > 0.0f) {
            return;
        }

        if (isServer) {
            if (other instanceof Bullet) {
                Bullet b = (Bullet) other;

                if (b.getOwnerPlayer() == null) {
                    return;
                }

                if (b.getOwnerPlayer().equals(this)) {
                    return;
                }

                b.getOwnerPlayer().addScore(50);
                respawn();
            } else if (other instanceof Player) {
                respawn();
            } else if (other instanceof Asteroid) {
                addScore(-5);
                respawn();
            }
        }
    }

    /**
     * Calculates the value or returns a precalculated value
     * @see ICollider#calculateLargestRadius()
     * @return largest radius calculated by calculateLargestRadius()
     */
    @Override
    public float getLargestRadius() {
        if (largestRadius == 0.0f) {
            largestRadius = calculateLargestRadius();
        }

        return largestRadius;
    }

    /**
     * Add score to this player's score
     * @param score score to add to current player's score
     */
    public void addScore(int score) {
        this.score += score;
        scoreUpdated = true;
    }

    public int getScore() {
        return score;
    }

    private void respawn() {
        immuneTime = 5.0f;
        getTransform().setPosition(RESPAWN_POINTS[rand.nextInt(4)]);
    }

    public ScoreWindow getUi() {
        AsteroidsGame game = (AsteroidsGame) getEntity().getRenderer().getGame();
        return game.getScoreWindow();
    }
}
