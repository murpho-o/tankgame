import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author murpho-o 2022/8/17
 * @version 11.0.15
 */
public class GamePanel extends JPanel implements KeyListener {
    public static final int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = 600;
    public static final Color BACKGROUND_COLOR = new Color(0x77969A);
    public static final long GAME_TIME = 120000;
    public static final int ENEMY_SIZE = 6;

    public Tank hero = null;
    public LinkedList<Tank> enemies = new LinkedList<>();
    public LinkedList<Shell> shells = new LinkedList<>();
    public LinkedList<Bomb> bombs = new LinkedList<>();
    public LinkedList<Wall> walls = new LinkedList<>();
    public LinkedList<Water> waters = new LinkedList<>();

    public long remainingTime; // total game time in millisecond
    private final long repaintPeriod = 50;

    private final Recorder recorder = new Recorder(this);
    private Image offScreenImage = null;

    /**
     * initialize the war game
     */
    public GamePanel() {
        setBackground(BACKGROUND_COLOR); // set background color

        generateBarriers();
        recoverGame();

        if (remainingTime != GAME_TIME) {
            int selection = JOptionPane.showConfirmDialog(null, "请问是否继续上局游戏",
                    "继续上局", JOptionPane.YES_NO_OPTION);
            if (selection == JOptionPane.NO_OPTION) {
                initializeGame();
            }
        }

        // timer 会比 Thread 类的 sleep() 更准确，定义为守护线程
        // schedule使用系统时间计算下一次，即System.currentTimeMillis()+period
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainingTime -= repaintPeriod;
                repaint();
                if (isGameOver()) {
                    // when game over, pop up dialog to choose to restart or exit
                    String message = isWin() ? "你赢了！是否重新开始？"
                            : "你输了，是否重新开始？";
                    String title = "游戏结束";
                    int selection = JOptionPane.showConfirmDialog(null, message, title,
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (selection == JOptionPane.OK_OPTION) {
                        initializeGame();
                    } else {
                       System.exit(0);
                    }
                }
            }
        }, 0, repaintPeriod);// 每隔50毫秒刷新一次
    }


    /**
     * initialize or reset game
     */
    public void initializeGame() {

        enemies.clear();
        shells.clear();
        bombs.clear();

        remainingTime = GAME_TIME;

        generateHero();
        generateEnemies(ENEMY_SIZE);

    }

    public void storeGame() {
        recorder.save();
    }

    public void recoverGame() {
        recorder.recover();
    }


    /**
     * generate my hero tank
     */
    public void generateHero() {
        int heroX = 0;
        int heroY = getPreferredSize().height - Tank.HEIGHT;
        hero = new Tank(true, heroX, heroY, Direction.UP, this);
    }

    /**
     * generate enemy tanks
     */
    public void generateEnemies(int enemySize) {
        for (int i = 0; i < enemySize; i++) {
            enemies.add(new Tank(false, getPreferredSize().width - Tank.WIDTH * i, 0, Direction.DOWN, this));
        }
    }

    /**
     * generate barriers
     */
    public void generateBarriers() {
        walls.add(new Wall(200, 200, 10, 300));
        walls.add(new Wall(350, 490, 300, 10));
        waters.add(new Water(400, 300, 300, 50));
    }

    /**
     * check if game is over
     */
    public boolean isGameOver() {
        return remainingTime <= 0 || enemies.isEmpty() || !hero.isAlive();
    }

    public boolean isWin() {
        return hero.isAlive() && enemies.isEmpty();
    }

    /**
     * define the role color there
     *
     * @param role true is my tank, false is enemy
     * @return the role color
     */
    public static Color getRoleColor(boolean role) {
        return role ? new Color(0xF9BF45) : new Color(0xEEA9A9);
    }

    /**
     * define the shell's color there
     *
     * @param role true is my tank, false is enemy
     * @return the shell's color
     */
    public static Color getShellColor(boolean role) {
        return role ? new Color(0xFCB218) : new Color(0xBF6766);
    }


    @Override
    // 只要某个部分需要重新绘制，就会自动调用这个组件的 paintComponent()，如果重新绘制，需调用 repaint()
    public void paintComponent(Graphics g) { // paintComponent() 比 paint() 更轻量级
        super.paintComponent(g); // 必须保留super.paintComponent() 才能调用 update()


        // draw water
        for (int i = 0; i < waters.size(); i++) {
            waters.get(i).draw(g);
        }

        // draw wall
        for (int i = 0; i < walls.size(); i++) {
            walls.get(i).draw(g);
        }

        // draw enemies
        for (int i = 0; i < enemies.size(); i++) {
            Tank e = enemies.get(i);
            e.draw(g);
        }

        // draw my tank
        hero.draw(g);

        // draw shell (注意使用普通for循环避免空指针)
        for (int i = 0; i < shells.size(); i++) {
            Shell s = shells.get(i);
            s.hitTank(enemies);
            s.hitTank(hero);
            s.hitWall();
            s.draw(g);
        }

        // draw bombs
        for (int i = 0; i < bombs.size(); i++) {
            bombs.get(i).draw(g);
        }

        // show game information
        recorder.showInfo(g);
    }

    /**
     * double-buffer applied to avert screen flicker and flash
     * 消除闪烁，使用双缓冲, 线程重画更加均匀，更能控制重化的速度。按键重画不能解决子弹自动飞行的问题；
     */
    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(getPreferredSize().width, getPreferredSize().height);
        }

        Graphics gOffScreen = offScreenImage.getGraphics(); // 获取图片内的所有图形，形成虚拟窗口
        Color color = gOffScreen.getColor();
        gOffScreen.setColor(BACKGROUND_COLOR);
        gOffScreen.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
        gOffScreen.setColor(color);
        paintComponent(gOffScreen); // 画在背后图片上
        g.drawImage(offScreenImage, 0, 0, null); // 画在屏幕上
    }

    @Override
    // 覆盖这个方法，以返回这个组件的首选大小
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        hero.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        hero.keyReleased(e);
    }
}
