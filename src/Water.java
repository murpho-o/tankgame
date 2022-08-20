import java.awt.*;

/**
 * @author murpho-o 2022/8/20
 * @version 11.0.15
 */
public class Water {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Rectangle rect;

    public Water(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rect = new Rectangle(x, y, width, height);
    }

    public Rectangle getRect() {
        return rect;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0x7DB9DE));
        g.fillRect(x, y, width, height);
    }
}
