import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * @author murpho-o 2022/8/16
 * @version 11.0.15
 */
public class GameFrame extends JFrame {

    public GameFrame() {

        // Game panel
        var panel = new GamePanel();
        add(panel, FlowLayout.LEFT); // add panel to frame
        addKeyListener(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.storeGame();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        // 所有的Swing组件必需由时间分派线程(event dispatch thread)配置
        EventQueue.invokeLater(() -> {
            var frame = new GameFrame();
            frame.setTitle("Tank Game"); // 设置菜单栏显示
            frame.pack(); // 使用窗体中组件的首选尺寸
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 定义用户关闭窗体是的响应动作
            frame.setLocationRelativeTo(null); // null为在屏幕居中位置显示
            frame.setVisible(true); // 设置窗体可见
            frame.setResizable(false); // 设置窗体不可缩放
        });
    }
}
