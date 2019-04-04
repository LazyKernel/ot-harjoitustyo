package asteroids.core.game.components.rendering;

import asteroids.core.game.EntityComponent;
import asteroids.core.graphics.shaders.MeshShader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh extends EntityComponent
{
    private static final int VERTEX_SIZE = 3;

    private int pVBO = 0;
    private int pVAO = 0;

    private MeshShader shader;

    private Vector3f color = new Vector3f(1.0f, 0.0f, 1.0f);
    private int drawType = GL_LINE_STRIP;
    private Vector3f[] points = new Vector3f[0];
    private int vertCount = 0;

    @Override
    public void init()
    {

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

            glDrawArrays(drawType, 0, vertCount);

            shader.unbind();
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
    }

    @Override
    public void update(float deltaTime)
    {

    }

    @Override
    public void destroy()
    {
        if (pVBO != 0)
        {
            glDeleteBuffers(pVBO);
            pVBO = 0;
        }


        if (pVAO != 0)
        {
            glDeleteVertexArrays(pVAO);
            pVAO = 0;
        }
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public int getDrawType() {
        return drawType;
    }

    public void setDrawType(int drawType) {
        this.drawType = drawType;
    }

    public Vector3f[] getPoints() {
        return points;
    }

    public void setPoints(Vector3f[] points) {
        this.points = points;
        vertCount = this.points.length;

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer vertexData = BufferUtils.createFloatBuffer((points.length) * VERTEX_SIZE);

            for (int i = 0; i < vertCount; i++)
            {
                vertexData.put(this.points[i].get(stack.mallocFloat(VERTEX_SIZE)));
            }
            vertexData.flip();

            pVBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, pVBO);
            glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            pVAO = glGenVertexArrays();
            glBindVertexArray(pVAO);
            glEnableVertexAttribArray(0);

            glBindBuffer(GL_ARRAY_BUFFER, pVBO);
            glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        shader = new MeshShader();
        shader.init();
    }

    public void setPoints(Vector3f[] points, int drawType) {
        setDrawType(drawType);
        setPoints(points);
    }
}
