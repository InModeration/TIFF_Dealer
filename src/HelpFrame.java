import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HelpFrame extends JFrame {
    //布局设置
    JPanel panel = new JPanel(new GridLayout(8, 1));
    public HelpFrame(final Frame father)
    {
        setIconImage(MainFrame.image.getImage());
        setBounds(525, 255, 600, 350);
        add(panel);
        panel.setBackground(Color.white);
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();
        JLabel label5 = new JLabel();
        JLabel label6 = new JLabel();
        JLabel label7 = new JLabel();
        JLabel label8 = new JLabel();
        label1.setText("使用说明：");
        label2.setText("选择图片(File - Open):弹出文件选择对话框，选择需要处理的tiff图片");
        label3.setText("处理后图片保存位置(File - Save_as):弹出文件保存对话框，保存处理后的图像");
        label4.setText("清空图像 -- 清除界面上当前包括处理前处理后的图像");
        label5.setText("模糊处理 -- 对已保存的图片进行模糊处理(输入模糊次数)");
        label6.setText("边缘检测 -- 对已保存的图片进行一次边缘检测");
        label7.setText("一键处理 -- 对已保存的图片默认进行五次模糊处理及一次边缘检测");
        label8.setText("*每次处理图像都需另存为； 若要对原图进行修改，请将保存路径设为原图路径");
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);
        panel.add(label5);
        panel.add(label6);
        panel.add(label7);
        panel.add(label8);

        setDefaultCloseOperation(2);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                father.setEnabled(true);
            }
        });
        setVisible(true);
        setResizable(false);
    }
}
