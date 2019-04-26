package asteroids.core.graphics.ui;

import asteroids.core.containers.ModifiableList;
import asteroids.core.file.FileLoader;
import org.joml.Matrix4f;
import org.lwjgl.nuklear.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.nmemFree;

public class UIManager {
    private ByteBuffer ttf;

    private ModifiableList<UIElement> elements = new ModifiableList<>(20);

    private NkContext ctx;
    private NkUserFont defaultFont;
    private NkBuffer cmds;
    private NkDrawNullTexture nullTexture;
    private NkAllocator allocator;
    private UIShader shader;

    private NkDrawVertexLayoutElement.Buffer vertexLayout;

    private int vbo = 0;
    private int ebo = 0;
    private int vao = 0;

    private Matrix4f projMatrix = new Matrix4f();

    private final int matrixLoc = 0;
    private final int posLoc = 1;
    private final int uvLoc = 2;
    private final int colorLoc = 3;
    private final int texLoc = 4;

    private final int maxBufferSize = 65536;

    private int windowWidth;
    private int windowHeight;

    private boolean captureMouse = false;

    public UIManager() {
        ctx = NkContext.create();
        defaultFont = NkUserFont.create();
        cmds = NkBuffer.create();
        nullTexture = NkDrawNullTexture.create();
        allocator = NkAllocator.create();
        allocator.alloc((handle, old, size) -> {
            long mem = MemoryUtil.nmemAlloc(size);
            if (mem == NULL) {
                throw new OutOfMemoryError();
            }

            return mem;
        });

        allocator.mfree((handle, ptr) -> nmemFree(ptr));

        shader = new UIShader();

        vertexLayout = NkDrawVertexLayoutElement.create(4);
        vertexLayout.position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0);
        vertexLayout.position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8);
        vertexLayout.position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16);
        vertexLayout.position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0);
        vertexLayout.flip();

    }

    public void init(long window) {
        shader.init();

        setUpCallbacks(window);
        nk_init(ctx, allocator, null);
        ctx.clip(it -> it
            .copy((handle, text, len) -> {
                if (len == 0) {
                    return;
                }

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    ByteBuffer str = stack.malloc(len + 1);
                    MemoryUtil.memCopy(text, MemoryUtil.memAddress(str), len);
                    str.put(len, (byte) 0);
                    glfwSetClipboardString(window, str);
                } catch (Exception e) {
                    System.out.println("init(): Exception caught while pushing into a stack.\n" + e.getMessage());
                }
            })
            .paste((handle, edit) -> {
                long text = nglfwGetClipboardString(window);
                if (text != NULL) {
                    nnk_textedit_paste(edit, text, nnk_strlen(text));
                }
            }));

        nk_buffer_init(cmds, allocator, maxBufferSize);
        setupBuffers();
        setupFont();
    }

    public void update(float deltaTime) {
        for (UIElement e : elements) {
            if (e == null) {
                continue;
            }

            e.update(deltaTime);
        }
    }

    public void render() {
        for (UIElement e : elements) {
            if (e == null) {
                continue;
            }

            e.render();
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {

            glEnable(GL_BLEND);
            glBlendEquation(GL_FUNC_ADD);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_SCISSOR_TEST);
            glActiveTexture(GL_TEXTURE0);

            shader.bind();
            glUniform1i(texLoc, 0);
            glUniformMatrix4fv(matrixLoc, false, projMatrix.get(stack.mallocFloat(16)));

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glBufferData(GL_ARRAY_BUFFER, maxBufferSize, GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, maxBufferSize, GL_STREAM_DRAW);

            // load draw vertices & elements directly into vertex + element buffer
            ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, maxBufferSize, null));
            ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, maxBufferSize, null));

            // fill convert configuration
            NkConvertConfig config = NkConvertConfig.callocStack(stack);
            config.vertex_layout(vertexLayout);
            config.vertex_size(20);
            config.vertex_alignment(4);
            config.null_texture(nullTexture);
            config.circle_segment_count(22);
            config.curve_segment_count(22);
            config.arc_segment_count(22);
            config.global_alpha(1.0f);
            config.shape_AA(NK_ANTI_ALIASING_ON);
            config.line_AA(NK_ANTI_ALIASING_ON);

            // setup buffers to load vertices and elements
            NkBuffer vbuf = NkBuffer.mallocStack(stack);
            NkBuffer ebuf = NkBuffer.mallocStack(stack);

            nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
            nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
            nk_convert(ctx, cmds, vbuf, ebuf, config);

            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            glUnmapBuffer(GL_ARRAY_BUFFER);

            long offset = NULL;
            for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx)) {
                if (cmd.elem_count() == 0) {
                    continue;
                }
                glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
                glScissor(
                        (int) (cmd.clip_rect().x()),
                        (int) ((windowHeight - (int) (cmd.clip_rect().y() + cmd.clip_rect().h()))),
                        (int) (cmd.clip_rect().w()),
                        (int) (cmd.clip_rect().h())
                );
                glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
                offset += cmd.elem_count() * 2;
            }
            nk_clear(ctx);

            shader.unbind();
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            glDisable(GL_BLEND);
            glDisable(GL_SCISSOR_TEST);

        } catch (Exception e) {
            System.out.println("render(): Cannot push into a stack.\n" + e);
        }
    }

    public void updateScreenDimensions(int width, int height) {
        projMatrix = new Matrix4f(2.0f / width, 0.0f, 0.0f, 0.0f,
                0.0f, -2.0f / height, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f, 1.0f);
        glViewport(0, 0, width, height);
        windowWidth = width;
        windowHeight = height;
    }

    public void beginInput() {
        nk_input_begin(ctx);
    }

    public void endInput(long window) {
        NkMouse mouse = ctx.input().mouse();
        if (mouse.grab()) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if (mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            glfwSetCursorPos(window, prevX, prevY);
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if (mouse.ungrab()) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        nk_input_end(ctx);
    }

    private void setupBuffers() {
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        vao = glGenVertexArrays();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(posLoc, 2, GL_FLOAT, false, 20, 0);
        glVertexAttribPointer(uvLoc, 2, GL_FLOAT, false, 20, 8);
        glVertexAttribPointer(colorLoc, 4, GL_UNSIGNED_BYTE, true, 20, 16);

        int nullTexID = glGenTextures();

        nullTexture.texture().id(nullTexID);
        nullTexture.uv().set(0.5f, 0.5f);

        glBindTexture(GL_TEXTURE_2D, nullTexID);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
        } catch (Exception e) {
            System.out.println("setupBuffers(): Cannot push into a stack.\n" + e.getMessage());
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void setupFont() {
        int bitmapW = 1024;
        int bitmapH = 1024;

        int fontHeight = 18;
        int fontTexID = glGenTextures();

        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

        float scale;
        float descent;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            stbtt_InitFont(fontInfo, ttf); // access violation here
            scale = stbtt_ScaleForPixelHeight(fontInfo, fontHeight);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapW * bitmapH);

            STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
            stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, ttf, 0, fontHeight, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            ByteBuffer texture = MemoryUtil.memAlloc(bitmapW * bitmapH * 4);
            for (int i = 0; i < bitmap.capacity(); i++) {
                texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            }
            texture.flip();

            glBindTexture(GL_TEXTURE_2D, fontTexID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, bitmapW, bitmapH, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            MemoryUtil.memFree(texture);
            MemoryUtil.memFree(bitmap);
        }

        defaultFont
                .width((handle, h, text, len) -> {
                    float textWidth = 0;
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer unicode = stack.mallocInt(1);

                        int glyphLen = nnk_utf_decode(text, MemoryUtil.memAddress(unicode), len);
                        int textLen  = glyphLen;

                        if (glyphLen == 0) {
                            return 0;
                        }

                        IntBuffer advance = stack.mallocInt(1);
                        while (textLen <= len && glyphLen != 0) {
                            if (unicode.get(0) == NK_UTF_INVALID) {
                                break;
                            }

                            /* query currently drawn glyph information */
                            stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                            textWidth += advance.get(0) * scale;

                            /* offset next glyph */
                            glyphLen = nnk_utf_decode(text + textLen, MemoryUtil.memAddress(unicode), len - textLen);
                            textLen += glyphLen;
                        }
                    }
                    return textWidth;
                })
                .height(fontHeight)
                .query((handle, height, glyph, codepoint, nextCodepoint) -> {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        FloatBuffer x = stack.floats(0.0f);
                        FloatBuffer y = stack.floats(0.0f);

                        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
                        IntBuffer advance = stack.mallocInt(1);

                        stbtt_GetPackedQuad(cdata, bitmapW, bitmapH, codepoint - 32, x, y, q, false);
                        stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                        NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                        ufg.width(q.x1() - q.x0());
                        ufg.height(q.y1() - q.y0());
                        ufg.offset().set(q.x0(), q.y0() + (height + descent));
                        ufg.xadvance(advance.get(0) * scale);
                        ufg.uv(0).set(q.s0(), q.t0());
                        ufg.uv(1).set(q.s1(), q.t1());
                    }
                })
                .texture(it -> it.id(fontTexID));

        nk_style_set_font(ctx, defaultFont);
    }

    private void setUpCallbacks(long win) {
        glfwSetScrollCallback(win, (window, xOffset, yOffset) -> scrollCallback((float) xOffset, (float) yOffset));
        glfwSetCharCallback(win, (window, codepoint) -> unicodeCallback(codepoint));
        glfwSetCursorPosCallback(win, (window, xpos, ypos) -> cursorPosCallback((int) xpos, (int) ypos));
        glfwSetMouseButtonCallback(win, (window, button, action, mods) -> mouseButtonCallback(window, button, action));
        glfwSetWindowSizeCallback(win, (window, width, height) -> updateScreenDimensions(width, height));
    }

    private void scrollCallback(float xOffset, float yOffset) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NkVec2 scroll = NkVec2.mallocStack(stack).x(xOffset).y(yOffset);
            nk_input_scroll(ctx, scroll);
        } catch (Exception e) {
            System.out.println("Exception caught while pushing into a stack.\n" + e.getMessage());
        }
    }

    private void unicodeCallback(int keyCode) {
        nk_input_unicode(ctx, keyCode);
    }

    public void keyCallback(long window, int key, int action) {
        boolean press = action == GLFW_PRESS;
        switch (key) {
            case GLFW_KEY_DELETE:
                nk_input_key(ctx, NK_KEY_DEL, press);
                break;
            case GLFW_KEY_ENTER:
                nk_input_key(ctx, NK_KEY_ENTER, press);
                break;
            case GLFW_KEY_TAB:
                nk_input_key(ctx, NK_KEY_TAB, press);
                break;
            case GLFW_KEY_BACKSPACE:
                nk_input_key(ctx, NK_KEY_BACKSPACE, press);
                break;
            case GLFW_KEY_UP:
                nk_input_key(ctx, NK_KEY_UP, press);
                break;
            case GLFW_KEY_DOWN:
                nk_input_key(ctx, NK_KEY_DOWN, press);
                break;
            case GLFW_KEY_HOME:
                nk_input_key(ctx, NK_KEY_TEXT_START, press);
                nk_input_key(ctx, NK_KEY_SCROLL_START, press);
                break;
            case GLFW_KEY_END:
                nk_input_key(ctx, NK_KEY_TEXT_END, press);
                nk_input_key(ctx, NK_KEY_SCROLL_END, press);
                break;
            case GLFW_KEY_PAGE_DOWN:
                nk_input_key(ctx, NK_KEY_SCROLL_DOWN, press);
                break;
            case GLFW_KEY_PAGE_UP:
                nk_input_key(ctx, NK_KEY_SCROLL_UP, press);
                break;
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT:
                nk_input_key(ctx, NK_KEY_SHIFT, press);
                break;
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL:
                if (press) {
                    nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS);
                } else {
                    nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_COPY, false);
                    nk_input_key(ctx, NK_KEY_PASTE, false);
                    nk_input_key(ctx, NK_KEY_CUT, false);
                    nk_input_key(ctx, NK_KEY_SHIFT, false);
                }
                break;
        }
    }

    private void cursorPosCallback(int xPos, int yPos) {
        nk_input_motion(ctx, xPos, yPos);
    }

    private void mouseButtonCallback(long window, int button, int action) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer cx = stack.mallocDouble(1);
            DoubleBuffer cy = stack.mallocDouble(1);

            glfwGetCursorPos(window, cx, cy);

            int x = (int) cx.get(0);
            int y = (int) cy.get(0);

            int nkButton;
            switch (button) {
                case GLFW_MOUSE_BUTTON_RIGHT:
                    nkButton = NK_BUTTON_RIGHT;
                    break;

                case GLFW_MOUSE_BUTTON_MIDDLE:
                    nkButton = NK_BUTTON_MIDDLE;
                    break;

                default:
                    nkButton = NK_BUTTON_LEFT;
            }

            nk_input_button(ctx, nkButton, x, y, action == GLFW_PRESS);

        } catch (Exception e) {
            System.out.println("Exception caught while pushing into a stack.\n" + e.getMessage());
        }
    }

    public void loadFont(String fileName) {
        try {
            ttf = FileLoader.loadFileAsByteBuffer(fileName);
        } catch (IOException e) {
            System.out.println("Font file " + fileName + " not found.\n" + e.getMessage());
        }
    }

    public boolean isCaptureMouse() {
        return captureMouse;
    }

    public void setCaptureMouse(boolean captureMouse) {
        this.captureMouse = captureMouse;
    }

    public void addElement(UIElement element) {
        element.setCtx(ctx);
        elements.add(element);
        element.init();
    }

    public void removeElement(UIElement element) {
        element.destroy();
        elements.remove(element);
    }
}
