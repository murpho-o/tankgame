import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * @author murpho-o 2022/8/19
 * @version 11.0.15
 */
public class Recorder {
    private final String path = "assets/data.txt";
    private BufferedWriter bw = null;
    private BufferedReader br = null;

    private final GamePanel panel;


    public Recorder(GamePanel panel) {
        this.panel = panel;
    }

    public void showInfo(Graphics g) {

        g.setColor(Color.WHITE);
        g.drawString("玩家坦克剩余血量: " + panel.hero.getHP() + "/" + Tank.HERO_BLOOD, 10, 20);
        g.drawString("敌方坦克剩余数量: " + panel.enemies.size() + "/" + GamePanel.ENEMY_SIZE, 10, 40);
        g.drawString("杀死敌方数量: " + Tank.getKill(), 10, 60);

        // when remaining time reach 10s, the time showing line becomes red
        if (panel.remainingTime <= 10000) {
            g.setColor(Color.RED);
        }
        long min = panel.remainingTime / 60000;
        long sec = (panel.remainingTime - min * 60000) / 1000;
        g.drawString(String.format("剩余游戏时间：%02d:%02d", min, sec), 10, 80);

    }


    /**
     * save game record to disk
     */
    public void save() {

        try {
            bw = new BufferedWriter(new FileWriter(path));

            if (panel.isGameOver()) {

                bw.write("");

            } else {

                bw.write(panel.remainingTime + " ");
                bw.write(tankToString(panel.hero));
                for (int i = 0; i < panel.enemies.size(); i++) {
                    bw.write(tankToString(panel.enemies.get(i)));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * recover game record from disk
     */
    public void recover() {
        try {
            if (!Files.exists(Path.of(path)) || Files.size(Path.of(path)) == 0) {
                panel.initializeGame();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            br = new BufferedReader(new FileReader(path));

            String content = br.readLine();
            String[] data = content.split(" ");

            panel.remainingTime = Long.parseLong(data[0]);
            panel.hero = stringToTank(data[1], data[2], data[3], data[4], data[5]);

            for (int i = 1; i <= (data.length - 6) / 5; i++) {
                panel.enemies.add(stringToTank(data[5 * i + 1], data[5 * i + 2], data[5 * i + 3], data[5 * i + 4], data[5 * i + 5]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * change tank data into String
     *
     * @param tank tank which need to save
     * @return strings save into disk
     */
    public String tankToString(Tank tank) {
        return tank.getRole() + " " + tank.getX() + " " + tank.getY() + " "
                + tank.getDirection().ordinal() + " " + tank.getHP() + " ";
    }

    /**
     * recover tank data from strings
     */
    public Tank stringToTank(String s1, String s2, String s3, String s4, String s5) {
        boolean role = Boolean.parseBoolean(s1);
        int x = Integer.parseInt(s2);
        int y = Integer.parseInt(s3);
        Direction d = Direction.values()[Integer.parseInt(s4)];
        int hP = Integer.parseInt(s5);
        Tank tank = new Tank(role, x, y, d, panel);
        tank.setHp(hP);

        return tank;
    }
}
