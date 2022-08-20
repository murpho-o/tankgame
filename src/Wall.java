import java.awt.*;

/**
 * @author murpho-o 2022/8/19
 * @version 11.0.15
 */
public class Wall {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Rectangle rect;

    public Wall(int x, int y, int width, int height) {
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
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
    }
}
