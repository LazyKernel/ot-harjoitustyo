package asteroids.core.graphics.ui.elements;

import asteroids.core.graphics.ui.UIElement;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.NkPluginFilterI;
import org.lwjgl.nuklear.Nuklear;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Basic text field ui element
 */
public class TextField extends UIElement {

    private int flags;
    private int maxLength;
    private NkPluginFilterI filter;
    private Charset charset;

    private ByteBuffer content;
    private IntBuffer length;

    /**
     * Creates a text field with max length of 12 and ascii charset
     */
    public TextField() {
        maxLength = 12;
        flags = NK_EDIT_FIELD | NK_EDIT_SELECTABLE;
        content = BufferUtils.createByteBuffer(maxLength + 1);
        length = BufferUtils.createIntBuffer(1);
        filter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
        charset = Charset.forName("US-ASCII");
    }

    /**
     * Creates an empty textfield
     * @param maxLength desired max length
     * @param multiline can the text field span multiple lines
     * @param filter charset filter
     * @param charset charset used in the filter
     */
    public TextField(int maxLength, boolean multiline, NkPluginFilterI filter, Charset charset) {
        this.maxLength = maxLength;
        flags = (multiline) ? NK_EDIT_MULTILINE : NK_EDIT_FIELD;
        flags |= NK_EDIT_SELECTABLE;
        content = BufferUtils.createByteBuffer(maxLength + 1);
        length = BufferUtils.createIntBuffer(1);
        this.filter = NkPluginFilter.create(filter);
        this.charset = charset;
    }

    /**
     * Creates a text field with pre filled text
     * @param text pre filled text
     * @param maxLength desired max length
     * @param multiline can the text field span multiple lines
     * @param filter charset filter
     * @param charset charset used in the filter
     */
    public TextField(String text, int maxLength, boolean multiline, NkPluginFilterI filter, Charset charset) {
        this(maxLength, multiline, filter, charset);
        byte[] arr = text.getBytes(charset);
        int len = text.length();
        content.put(arr);
        length.put(len);
        content.clear();
        length.clear();
    }

    public void init() {

    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        nk_edit_string(getCtx(), flags, content, length, maxLength + 1, filter);
    }

    public void update(float deltaTime) {

    }

    public void destroy() {

    }

    /**
     * Text field cannot have children
     * @param element not used
     */
    @Override
    public void addElement(UIElement element) {
        System.err.println("Text field cannot have children.");
    }

    /**
     * Text field cannot have children
     * @param element not used
     */
    @Override
    public void removeElement(UIElement element) {
        System.err.println("Text field cannot have children.");
    }

    /**
     * Get the text contained in this text field
     * @return text in the text field
     */
    public String getValue() {
        content.mark();
        byte[] bytes = new byte[length.get(0)];
        content.get(bytes, 0, length.get(0));
        content.reset();
        return new String(bytes, charset);
    }
}
