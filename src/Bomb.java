import javax.swing.*;
import java.awt.*;

/**
 * @author murpho-o 2022/8/18
 * @version 11.0.15
 */
public class Bomb {
    public static final Image IMG_L = new ImageIcon("assets/img/bomb_1.gif").getImage();
    public static final Image IMG_M = new ImageIcon("assets/img/bomb_2.gif").getImage();
    public static final Image IMG_S = new ImageIcon("assets/img/bomb_3.gif").getImage();

    private int x;
    private int y;
    private int life;
    private final GamePanel panel;

    public Bomb(int x, int y, GamePanel panel) {
        this.x = x;
        this.y = y;
        this.life = 9;
        this.panel = panel;
    }

    public void draw(Graphics g) {
        if (life < 0) {
            panel.bombs.remove(this);
        }

        if (life > 6) {
            g.drawImage(IMG_L, x, y, Tank.WIDTH, Tank.HEIGHT, null);
        } else if (life > 3) {
            g.drawImage(IMG_M, x, y, Tank.WIDTH, Tank.HEIGHT, null);
        } else {
            g.drawImage(IMG_S, x, y, Tank.WIDTH, Tank.HEIGHT, null);
        }

        life--;
    }
}
