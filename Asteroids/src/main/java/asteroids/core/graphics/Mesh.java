package asteroids.core.graphics;

import asteroids.core.containers.EntityComponent;
import asteroids.core.graphics.shaders.MeshShader;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh extends EntityComponent {
    private static final int VERTEX_SIZE = 3;

    private int pVBO = 0;
    private int pVAO = 0;

    private MeshShader shader = null;

    private Vector3f color = new Vector3f(1.0f, 0.0f, 1.0f);
    private int drawType = GL_LINE_STRIP;
    private Vector3f[] points = new Vector3f[0];
    private int vertCount = 0;

    private int colorLoc = 0;
    private int transformLoc = 1;
    private int posLoc = 2;

    private boolean pendingUpdate = false;
    private boolean render = true;

    @Override
    public void init() {

    }

    @Override
    public void render() {
        if (!render) {
            return;
        }

        if (points.length <= 0 || shader == null) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            glBindVertexArray(pVAO);
            glEnableVertexAttribArray(posLoc);
            glEnable(GL_MULTISAMPLE);
            shader.bind();

            glUniform3f(colorLoc, color.x, color.y, color.z);
            glUniformMatrix4fv(transformLoc, false, getTransform().getTransformMatrix().get(stack.mallocFloat(16)));

            glDrawArrays(drawType, 0, vertCount);

            shader.unbind();
            glDisable(GL_MULTISAMPLE);
            glDisableVertexAttribArray(posLoc);
            glBindVertexArray(0);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (pendingUpdate) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer vertexData = BufferUtils.createFloatBuffer(vertCount * VERTEX_SIZE);

                for (int i = 0; i < vertCount; i++) {
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
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            shader = new MeshShader();
            shader.init();

            colorLoc = glGetUniformLocation(shader.getProgram(), "customColor");
            transformLoc = glGetUniformLocation(shader.getProgram(), "transform");
            posLoc = glGetAttribLocation(shader.getProgram(), "vertPos");

            pendingUpdate = false;
        }
    }

    @Override
    public void destroy() {
        if (pVBO != 0) {
            glDeleteBuffers(pVBO);
            pVBO = 0;
        }


        if (pVAO != 0) {
            glDeleteVertexArrays(pVAO);
            pVAO = 0;
        }

        if (shader != null) {
            shader.destroy();
            shader = null;
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
        pendingUpdate = true;
    }

    public Vector3f[] getPoints() {
        return points;
    }

    /**
     * Set new points to render. Sets points to be pending update
     * @param points new points
     */
    public void setPoints(Vector3f[] points) {
        this.points = points;
        vertCount = this.points.length;
        pendingUpdate = true;
    }

    /**
     * Set points and change draw type
     * @see Mesh#setPoints(Vector3f[])
     * @see Mesh#setDrawType(int)
     * @param points points to add
     * @param drawType new draw type
     */
    public void setPoints(Vector3f[] points, int drawType) {
        setDrawType(drawType);
        setPoints(points);
    }

    /**
     * Is this mesh currently rendered
     * @return true if it is
     */
    public boolean isRendered() {
        return render;
    }

    /**
     * Should this mesh be rendered
     * @param render true if it should
     */
    public void setRender(boolean render) {
        this.render = render;
    }
}
