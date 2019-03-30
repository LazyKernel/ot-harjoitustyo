package asteroids.core.game.components;

import asteroids.core.game.EntityComponent;
import asteroids.core.game.input.KeyboardHandler;
import asteroids.core.graphics.Camera;
import asteroids.core.graphics.shaders.PlayerShader;
import com.sun.scenario.effect.Merge;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class PlayerMesh extends EntityComponent
{
    private static final Vector3f[] POINTS = new Vector3f[]{
        new Vector3f(-0.75f, -1.0f, 0.0f), new Vector3f(0.75f, -1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f)
    };

    private static final int VERTEX_SIZE = 3;

    private int pVBO = 0;
    private int pVAO = 0;

    private PlayerShader shader;

    private Vector3f color = new Vector3f(1.0f, 0.0f, 1.0f);

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

            pVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, pVBO);
            glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            pVAO = glGenVertexArrays();
            glBindVertexArray(pVAO);
            glEnableVertexAttribArray(0);

            glBindBuffer(GL_ARRAY_BUFFER, pVBO);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        shader = new PlayerShader();
        shader.init();

        getTransform().setScale(new Vector2f(200, 200));
        //getTransform().setPosition(new Vector2f(400, 300));
    }

    @Override
    public void render()
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glBindVertexArray(pVAO);
            glEnableVertexAttribArray(0);
            shader.bind();

            glUniform3f(1, color.x, color.y, color.z);
            glUniformMatrix4fv(2, false, getTransform().getTransformMatrix().get(stack.mallocFloat(16)));
            glUniformMatrix4fv(3, false, Camera.getProjectionMatrix().get(stack.mallocFloat(16)));

            glDrawArrays(GL_LINE_STRIP, 0, POINTS.length + 1);

            shader.unbind();
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
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
