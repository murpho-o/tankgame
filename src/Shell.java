import java.awt.*;
import java.util.LinkedList;

/**
 * @author murpho-o 2022/8/18
 * @version 11.0.15
 */
public class Shell {
    public static final int WIDTH = 10, HEIGHT = 10;
    public static final int SPEED = 15;
    public static final int KILL = 10;

    private int x;
    private int y;
    private final Direction direction;
    private boolean isAlive = true;
    private final boolean role;
    private final Color color;
    private final Rectangle rect; // 尽可能的不要在高频率执行的代码里使用new来创建对象，如果能用成员变量就用成员变量。
    private final GamePanel panel;

    /**
     * @param role      true in my tank's, false is enemy's
     * @param x         coordinate x
     * @param y         coordinate y
     * @param direction the move direction of this shell
     * @param panel     which GamePanel it's in
     */
    public Shell(boolean role, int x, int y, Direction direction, GamePanel panel) {
        this.role = role;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.rect = new Rectangle(x, y, WIDTH, HEIGHT);
        this.panel = panel;
        this.color = GamePanel.getShellColor(role);
    }

    /**
     * method to draw shell
     *
     * @param g Graphics class
     */
    public void draw(Graphics g) {
        if (!isAlive) {
            panel.shells.remove(this);
            return;
        }

        g.setColor(color);
        g.fillOval(x, y, WIDTH, HEIGHT);

        move();
    }

    /**
     * cross boundary check
     */
    public void crossBoundaryCheck() {
        if (x < 0 || y < 0 || x > panel.getPreferredSize().width || y > panel.getPreferredSize().height) {
            isAlive = false;
        }
    }

    /**
     * shell moves
     */
    public void move() {

        switch (direction) {
            case UP:
                y -= SPEED;
                break;
            case UP_RIGHT:
                y -= SPEED;
                x += SPEED;
                break;
            case RIGHT:
                x += SPEED;
                break;
            case DOWN_RIGHT:
                x += SPEED;
                y += SPEED;
                break;
            case DOWN:
                y += SPEED;
                break;
            case DOWN_LEFT:
                y += SPEED;
                x -= SPEED;
                break;
            case LEFT:
                x -= SPEED;
                break;
            case UP_LEFT:
                x -= SPEED;
                y -= SPEED;
                break;
        }

        crossBoundaryCheck();
    }

    /**
     * judge if the shell hits tank
     *
     * @param tank tank
     * @return if it hits, return true
     */
    public boolean hitTank(Tank tank) {
        if (!isAlive || !tank.isAlive() || !getRect().intersects(tank.getRect())
                || (role == tank.getRole())) {
            return false;
        }

        // if shell hits
        isAlive = false; // shell destroyed

        // reset HP of the tank
        tank.killHP(KILL);

        return true;
    }

    /**
     * judge if the shell hits tank among a list of tanks
     *
     * @param tanks a list of tanks
     * @return if it hits, return true
     */
    public boolean hitTank(LinkedList<Tank> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            if (hitTank(tanks.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * if hit wall, shell destroys
     */
    public void hitWall() {
        for (int i = 0; i < panel.walls.size(); i++) {
            if (!isAlive || !getRect().intersects(panel.walls.get(i).getRect())) continue;
            isAlive = false;
        }
    }

    /**
     * @return the rectangular scope of the shell
     */
    public Rectangle getRect() {
        rect.setLocation(x, y);
        return rect;
    }
}
