package asteroids.core.game.components;

import asteroids.core.game.EntityComponent;
import asteroids.core.game.input.KeyboardHandler;
import com.sun.scenario.effect.Merge;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

public class PlayerMesh extends EntityComponent
{
    private static final Vector3f[] POINTS = new Vector3f[]{
        new Vector3f(-0.75f, -1.0f, 0.0f), new Vector3f(0.75f, -1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f)
    };

    private static final int VERTEX_SIZE = 3;

    private int pVBO = 0;
    private int pColorVBO = 0;

    @Override
    public void init()
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer vertexData = BufferUtils.createFloatBuffer((POINTS.length + 1) * VERTEX_SIZE); // drawing with GL_LINE_STRIP
            vertexData.put(POINTS[0].get(stack.mallocFloat(3)));
            vertexData.put(POINTS[1].get(stack.mallocFloat(3)));
            vertexData.put(POINTS[2].get(stack.mallocFloat(3)));
            vertexData.put(POINTS[0].get(stack.mallocFloat(3)));
            vertexData.flip();

            FloatBuffer colorData = BufferUtils.createFloatBuffer((POINTS.length + 1) * VERTEX_SIZE);
            colorData.put(new float[] { 1f, 0f, 0f, });
            colorData.put(new float[] { 0f, 1f, 0f, });
            colorData.put(new float[] { 0f, 0f, 1f, });
            colorData.put(new float[] { 1f, 0f, 0f, });
            colorData.flip();

            pVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, pVBO);
            glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            pColorVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, pColorVBO);
            glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void render()
    {
        glBindBuffer(GL_ARRAY_BUFFER, pVBO);
        glVertexPointer(VERTEX_SIZE, GL_FLOAT, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, pColorVBO);
        glColorPointer(VERTEX_SIZE, GL_FLOAT, 0, 0L);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glDrawArrays(GL_LINE_STRIP, 0, POINTS.length + 1);

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    @Override
    public void update(float deltaTime)
    {
        if (KeyboardHandler.isKeyDown(GLFW_KEY_A))
        {
            getTransform().rotate(-(float)Math.PI * deltaTime);
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_D))
        {
            getTransform().rotate((float)Math.PI * deltaTime);
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_W))
        {
            Vector3f dir = new Vector3f(0.0f, 1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0,0, 1).transform(dir);
            getTransform().translate(dir);
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_S))
        {
            Vector3f dir = new Vector3f(0.0f, -1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0,0, 1).transform(dir);
            getTransform().translate(dir);
        }
    }

    @Override
    public void destroy()
    {
        if (pVBO != 0)
            glDeleteBuffers(pVBO);
    }
}
