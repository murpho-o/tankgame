import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author murpho-o 2022/8/16
 * @version 11.0.15
 */
public class Tank {
    public static final int WIDTH = 30, HEIGHT = 30;
    public static final int SPEED = 5; // set the speed of tank
    public static final int HERO_BLOOD = 200;
    public static final int ENEMY_BLOOD = 20;
    public static final int KILL = 2;
    public static final Random r = new Random();

    private static int kill = 0; // count how many tanks my hero kill
    private int x;
    private int y;
    private int oldX, oldY;
    private Direction direction;
    private final boolean role;
    private final Color color;
    private boolean isAlive = true;
    private int hP;
    private boolean fire = false, superFire = false;
    private boolean bL = false, bU = false, bR = false, bD = false; // to control direction of move
    private int step = r.nextInt(10) + 3; // to generate random steps to move
    private final BloodBar bBar = new BloodBar();
    private final Rectangle rect; // 尽可能的不要在高频率执行的代码里使用new来创建对象，如果能用成员变量就用成员变量。
    private final GamePanel panel;

    /**
     *
     * @param role true is my tank, false is enemy
     * @param x coordinate x
     * @param y coordinate y
     * @param direction direction of tank
     * @param panel instance of game controller
     */
    public Tank(boolean role, int x, int y, Direction direction, GamePanel panel) {
        this.role = role;
        this.x = x;
        this.y = y;
        this.direction = direction;
        color = GamePanel.getRoleColor(role);
        hP = role ? HERO_BLOOD : ENEMY_BLOOD;
        rect = new Rectangle(x, y, WIDTH, HEIGHT);
        this.panel = panel;
    }

    /**
     * method to draw tank
     * @param g Graphics class
     */
    public void draw(Graphics g) {
        if (!isAlive) {
            if (!role) {
                panel.enemies.remove(this);
            } else {
                g.setColor(Color.MAGENTA);
                g.drawString("游戏结束，大侠请重新来过！", 100, 100);
            }
            return;
        }

        bBar.draw(g);

        g.setColor(color);
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(new Color(0x405B55)); // 炮筒颜色

        switch (direction) {
            case UP:
                g.drawLine(this.x + WIDTH / 2, this.y - HEIGHT / 2, this.x + WIDTH / 2, this.y
                        + HEIGHT / 2);
                break;
            case UP_RIGHT:
                g.drawLine(this.x + WIDTH + WIDTH / 2, this.y - HEIGHT / 2, this.x + WIDTH / 2,
                        this.y + HEIGHT / 2);
                break;
            case RIGHT:
                g.drawLine(this.x + WIDTH + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH / 2,
                        this.y + HEIGHT / 2);
                break;
            case DOWN_RIGHT:
                g.drawLine(this.x + WIDTH, this.y + HEIGHT, this.x + WIDTH / 2, this.y + HEIGHT / 2);
                break;
            case DOWN:
                g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT + HEIGHT / 2, this.x + WIDTH / 2,
                        this.y + HEIGHT / 2);
                break;
            case DOWN_LEFT:
                g.drawLine(this.x, this.y + HEIGHT + HEIGHT / 2, this.x + WIDTH / 2, this.y
                        + HEIGHT / 2);
                break;
            case LEFT:
                g.drawLine(this.x - WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH / 2, this.y
                        + HEIGHT / 2);
                break;
            case UP_LEFT:
                g.drawLine(this.x - WIDTH / 2, this.y - HEIGHT / 2, this.x + WIDTH / 2, this.y
                        + HEIGHT / 2);
                break;
        }

        move();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public static int getKill() {
        return kill;
    }

    public int getHP() {
        return hP;
    }

    public void setHp(int hP) {
        this.hP = hP;
    }

    /**
     * when tank is hit, reset the HP, and check if the tank still alive
     * @param killValue  HP = HP - killValue
     */
    public void killHP(int killValue) {
        this.hP -= killValue;
        setAlive();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive() {
        isAlive = hP > 0;
        if (isAlive) return;
        if (!role) kill++;
        panel.bombs.add(new Bomb(x, y, panel)); // exploded
    }

    public boolean getRole() {
        return role;
    }

    /**
     * set random direction but not the original
     */
    public void setRandomDirect() {
        Direction original = direction;
        Direction[] dir = Direction.values();
        int k;

        do {
            k = r.nextInt(dir.length);
            direction = dir[k];
        } while (direction == original);
    }

    /**
     * tank move
     */
    public void move() {

        oldX = x;
        oldY = y;

        if (step == 0) {
            step = r.nextInt(10) + 3;
            setRandomDirect();
        }

        if (!role) {
            step--;
        } else {
            // if key not pressed, my tank doesn't move
            if (!(bU || bD || bR || bL)) {
                return;
            }
        }

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
            default:
                break;
        }

        avoidCrossOver();
        avoidCrossBarriers();
        avoidCrush();

        if (!role && enemyAttack()) {
            fire();
        }
    }

    /**
     * judge the direction that we control on keyboard
     */
    public void moveDirection() {
        int a, b, c, d, total;

        a = bU ? 1 : 0;
        b = bR ? 2 : 0;
        c = bD ? 4 : 0;
        d = bL ? 7 : 0;

        total = a + b + c + d;

        switch (total) {
            case 1:
                direction = Direction.UP;
                break;
            case 2:
                direction = Direction.RIGHT;
                break;
            case 3:
                direction = Direction.UP_RIGHT;
                break;
            case 4:
                direction = Direction.DOWN;
                break;
            case 6:
                direction = Direction.DOWN_RIGHT;
                break;
            case 7:
                direction = Direction.LEFT;
                break;
            case 8:
                direction = Direction.UP_LEFT;
                break;
            case 11:
                direction = Direction.DOWN_LEFT;
                break;
        }
    }


    /**
     * judge if two tanks are crushing
     */
    public void avoidCrush() {
        ArrayList<Tank> tanks = new ArrayList<>();
        tanks.add(panel.hero);
        tanks.addAll(panel.enemies);
        for (int i = 0; i < tanks.size(); i++) {
            if (!isAlive) return;
            if (this == tanks.get(i)) continue;
            if (!tanks.get(i).isAlive || !getRect().intersects(tanks.get(i).getRect())) continue;

            recoverLoc();

            if (!role) {
                step = 0;
                setRandomDirect();
            } else {
                killHP(KILL);
            }
        }
    }


    /**
     * avoid tank cross over boundaries.
     */
    public void avoidCrossOver() {
        if (x < 0) {
            x = 0;
        }

        if (x + WIDTH > GamePanel.DEFAULT_WIDTH) {
            x = GamePanel.DEFAULT_WIDTH - WIDTH;
        }

        if (y < 0) {
            y = 0;
        }

        if (y + HEIGHT > GamePanel.DEFAULT_HEIGHT) {
            y = GamePanel.DEFAULT_HEIGHT - HEIGHT;
        }
    }

    public void avoidCrossBarriers() {
        for (int i = 0; i < panel.walls.size(); i++) {
            if (!isAlive || !getRect().intersects(panel.walls.get(i).getRect())) continue;

            recoverLoc();

        }

        for (int i = 0; i < panel.waters.size(); i++) {
            if (!isAlive || !getRect().intersects(panel.waters.get(i).getRect())) continue;

            recoverLoc();

        }
    }


    /**
     * recover the original tank location
     */
    private void recoverLoc() {
        this.x = this.oldX;
        this.y = this.oldY;
    }

    public Rectangle getRect() {
        rect.setLocation(x, y);
        return rect;
    }

    /**
     * tank shoots
     */
    public void fire() {
        fire(direction);
    }

    /**
     * overload fire, fire with direction
     */
    public void fire(Direction dir) {
        if (!isAlive) {
            return;
        }

        int shellX = x + WIDTH / 2 - Shell.WIDTH / 2;
        int shellY = y + WIDTH / 2 - Shell.HEIGHT / 2;
        panel.shells.add(new Shell(role, shellX, shellY, dir, panel));
    }

    /**
     * super fire with press control
     */
    public void superFire() {
        Direction[] dirs = Direction.values();

        for (Direction dir : dirs) {
            fire(dir);
        }
    }


    /**
     * control the enemy tank shooting frequency
     * @return boolean
     */
    public boolean enemyAttack() {
        return r.nextInt(1000) > 920;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_SPACE:
                fire = true;
                break;
            case KeyEvent.VK_ALT:
                superFire = true;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                bU = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                bR = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                bD = true;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                bL = true;
                break;
            default:
                return;
        }

        moveDirection();
        if (fire) {
            if (superFire) {
                superFire();
            } else {
                fire();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_SPACE:
                fire = false;
                break;
            case KeyEvent.VK_ALT:
                superFire = false;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                bU = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                bR = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                bD = false;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                bL = false;
                break;
            case KeyEvent.VK_F2:
                panel.initializeGame();
            default:
                return;
        }

        moveDirection();
    }

    class BloodBar {
        public static final int Height = 10;

        public void draw(Graphics g) {
            int width;
            if (role) {
                width = (int) ((double) hP / HERO_BLOOD * WIDTH);
            } else {
                width = (int) ((double) hP / ENEMY_BLOOD * WIDTH);
            }

            g.setColor(Color.red);
            g.fillRect(x, y - BloodBar.Height, width, BloodBar.Height);

            g.setColor(Color.WHITE);
            g.drawRect(x, y - BloodBar.Height, WIDTH, BloodBar.Height);
        }
    }
}
