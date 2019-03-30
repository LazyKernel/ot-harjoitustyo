package asteroids.core.graphics.shaders;

import asteroids.core.FileLoader;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader
{
    private int pShaderVert = 0;
    private int pShaderFrag = 0;
    private int pProgram = 0;

    protected void createShader(String fileNameVert, String fileNameFrag)
    {
        String source = FileLoader.loadFileAsString(fileNameVert);
        pShaderVert = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(pShaderVert, source);
        glCompileShader(pShaderVert);

        source = FileLoader.loadFileAsString(fileNameFrag);
        pShaderFrag = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(pShaderFrag, source);
        glCompileShader(pShaderFrag);

        if (!checkCompile())
            return;

        pProgram = glCreateProgram();

        glAttachShader(pProgram, pShaderVert);
        glAttachShader(pProgram, pShaderFrag);

        glLinkProgram(pProgram);

        checkLink();
        validateProgram();
    }

    public void destroy()
    {
        if (pShaderVert != 0)
        {
            glDeleteShader(pShaderVert);
            pShaderVert = 0;
        }

        if (pShaderFrag != 0)
        {
            glDeleteShader(pShaderFrag);
            pShaderFrag = 0;
        }

        if (pProgram != 0)
        {
            glDeleteProgram(pProgram);
            pProgram = 0;
        }
    }

    private boolean checkCompile()
    {
        if (glGetShaderi(pShaderVert, GL_COMPILE_STATUS) != GL_TRUE)
        {
            System.err.println("Couldn't compile vertex shader.\n" + glGetShaderInfoLog(pShaderVert));
            return false;
        }

        if (glGetShaderi(pShaderFrag, GL_COMPILE_STATUS) != GL_TRUE)
        {
            System.err.println("Couldn't compile fragment shader.\n" + glGetShaderInfoLog(pShaderFrag));
            return false;
        }

        return true;
    }

    private boolean checkLink()
    {
        if (glGetProgrami(pProgram, GL_LINK_STATUS) != GL_TRUE)
        {
            System.err.println("Couldn't link shaders.");
            return false;
        }

        return true;
    }

    private boolean validateProgram()
    {
        glValidateProgram(pProgram);

        if (glGetProgrami(pProgram, GL_VALIDATE_STATUS) != GL_TRUE)
        {
            System.err.println("Couldn't validate program.");
            return false;
        }

        return true;
    }

    public void bind()
    {
        glUseProgram(pProgram);
    }

    public void unbind()
    {
        glUseProgram(0);
    }
}
