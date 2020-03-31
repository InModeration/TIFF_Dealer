import com.sun.media.jai.codec.*;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
/*
 *  1.open -- 选择要处理的图片并显示
 *  2.sava_as -- 选择另存为的位置 -- 若未open -- 提示
 *  3.boxblur -- 对另存为的图片进行模糊处理 -- 若未open -- 提示 -- 若未save_as -- 提示
 *  4.sobel -- 对另存为的图片进行模糊处理 -- 若未open -- 提示 -- 若未save_as -- 提示
 */
public class MainFrame extends JFrame {

    //定义boolean变量确定是否可操作图片，即是否open了tiff文件
    private static boolean ifOpen = false;
    //定义boolean变量确定是否另存为图像
    private static boolean ifSave = false;
    //选择文件的绝对路径
    private static String filename = "default";
    //另存为文件的绝对路径
    private static String save_filename = "default";
    //private static String filename = "C:\\Users\\asus\\Desktop\\flower.tiff";
    //用于对存放模糊处理图像的容器的操作
    private Stack<ScrollingImagePanel> boxBlurStack = new Stack<>();
    //用于对存放原始图像的容器的操作
    private Stack<ScrollingImagePanel> oriStack = new Stack<>();
    //设置图片的位置
    private int oriX = 30, oriY = 140;
    private int afterX = 600, afterY = 140;
    //设置图片大小
    private int width = 500, height = 400;
    //设置标题图标
    public static ImageIcon image = new ImageIcon("Icon.png");

    public MainFrame() {
        //设置图片模糊处理、边缘检测按钮
        JButton sobel = new JButton("边缘检测");
        JButton boxblur = new JButton("模糊处理");
        JButton boxBlurAndSobel = new JButton("一键处理");
        //设置清空图片的按钮
        JButton clearTiff = new JButton("清空图像");

        //设置文本信息
        JPanel info = new JPanel(new GridLayout(1, 2));
        JLabel before = new JLabel();
        JLabel after = new JLabel();
        after.setText("after:");
        before.setText("before:");
        info.add(before);
        info.add(after);
        info.setBounds(40, 120, 1150, 20);
        add(info);

        setIconImage(image.getImage());
        //设置菜单
        MenuBar mb = new MenuBar();

        Menu help = new Menu(" Directions For Use ");
        Menu author = new Menu("Author : 2017141463149");
        Menu file = new Menu("File");

        mb.add(file);
        mb.add(help);
        mb.add(author);

        MenuItem instruction = new MenuItem("Read Me");
        MenuItem open = new MenuItem("Open");
        MenuItem Save_as = new MenuItem("Save_as");

        //将使用说明添加至help的Menu，并为其添加监听器
        help.add(instruction);
        instruction.addActionListener(event ->{
            new HelpFrame(this);
        });

        //将打开文件的MenuItem添加至File的Menu，并为其添加监听器
        file.add(open);
        open.addActionListener(event ->
        {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        //目录可见
                        if (f.isDirectory()) return true;
                        //读tiff文件
                        return f.getName().endsWith(".tiff");
                    }

                    @Override
                    public String getDescription() {
                        return ".tiff";
                    }
                });
                jfc.showDialog(new JLabel(), "选择");
                File file_2 = jfc.getSelectedFile();
                if (filename != null) {
                    if (!oriStack.isEmpty())
                        remove(oriStack.pop());
                    filename = file_2.getAbsolutePath();
                    loadTiff(filename, oriStack, oriX, oriY);
                    //一旦插入图片，ifOpen设置为true
                    ifOpen = true;
                    //一旦插入图片，需要重新选择保存的位置
                    ifSave = false;
                }
        });

        //将另存为添加至File的Menu，并为其添加监听器
        file.add(Save_as);
        Save_as.addActionListener(event ->
        {
            if (!ifOpen)    JOptionPane.showMessageDialog(new JLabel(), "请先打开要操作的图片文件");
            else {
                JFileChooser save = new JFileChooser();
                //设置并添加后缀名过滤器，保证只允许另存为.tiff文件
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".tiff", "tiff");
                save.setFileFilter(filter);

                //用户按下保存按钮且输入不为空
                //用户按下取消按钮
                int option = save.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {    //假如选择了保存
                    File saveFile = save.getSelectedFile();
                    //将输入的文件名存为文件名
                    save_filename = saveFile.getAbsolutePath();

                    //假如输入的文件名不含.tiff后缀
                    if (save_filename.indexOf(".tiff") == -1) {
                        save_filename += ".tiff";
                        System.out.println(save_filename);
                    }

                    //利用该filename建立新的tiff文件
                    PixeImage ori = ImageUtility.readTIFFImageFromFile(filename);
                    PixeImage clone = (PixeImage) ori.clone();
                    ImageUtility.writeTIFFImageToFile(clone, save_filename);
                    //保存后将ifSave设置为true
                    ifSave = true;
                }
            }
        });

        //将MenuBar添加至MainFrame
        setMenuBar(mb);

        //设置按钮位置
        setLayout(null);
        boxblur.setBounds(700, 20, 120, 40);
        sobel.setBounds(900, 20, 120, 40);
        clearTiff.setBounds(200, 20, 120, 40);
        boxBlurAndSobel.setBounds(800, 70, 120, 40);
        //设置按钮颜色
        clearTiff.setBackground(Color.red);

        //将清空当前图像的按钮添加至窗口并添加监听器
        add(clearTiff);
        clearTiff.addActionListener(event ->
        {
            //清除原始图像
            if (!oriStack.isEmpty())
                remove(oriStack.pop());
            //清除处理后的图像
            if (!boxBlurStack.isEmpty())
                remove(boxBlurStack.pop());

            //设置为不可操作
            ifOpen = false;
            ifSave = false;
        });

        //将模糊处理一次的按钮添加至窗口并设置监听器
        add(boxblur);
        boxblur.addActionListener(event ->
        {
            if (!ifOpen){
                JOptionPane.showMessageDialog(new JLabel(), "请先打开图片文件");
            }
            else if (!ifSave) {
                JOptionPane.showMessageDialog(new JLabel(), "请设置保存图片的位置");
            }
            //当加载图片并设置的保存位置
            else {
                //模糊处理次数times
                int times = 0;
                String input;
                //获取输入的内容
                input = JOptionPane.showInputDialog("请输入模糊处理的次数：");
                while (!ImageUtility.isNumeric(input)) {
                    JOptionPane.showMessageDialog(new JLabel(), "输入有误，请重新输入");
                    input = JOptionPane.showInputDialog("请输入模糊处理的次数：");
                }
                //模糊处理times次
                times = Integer.parseInt(input);
                if(!boxBlurStack.isEmpty())
                    remove(boxBlurStack.pop());
                for (int i = 0; i < times; i++) {
                    try {
                        ImageUtility.boxBlur(save_filename);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                loadTiff(save_filename, boxBlurStack, afterX, afterY);
            }
        });

        //将边缘处理的按钮添加至窗口并添加监听器
        add(sobel);
        sobel.addActionListener(event ->
        {
            if (!ifOpen){
                JOptionPane.showMessageDialog(new JLabel(), "请先打开图片文件");
            }
            else if (!ifSave) {
                JOptionPane.showMessageDialog(new JLabel(), "请设置保存图片的位置");
            }
            //当加载了图片并设置了保存位置
            else{
                if (!boxBlurStack.isEmpty())
                    remove(boxBlurStack.pop());
                try {
                    ImageUtility.sobel(save_filename);
                }catch(CloneNotSupportedException e){
                    e.printStackTrace();
                }
                loadTiff(save_filename, boxBlurStack, afterX, afterY);
            }
        });

        //将一键处理按钮添加至窗口并设置监听器
        add(boxBlurAndSobel);
        boxBlurAndSobel.addActionListener(event ->
        {
            if (!ifOpen){
                JOptionPane.showMessageDialog(new JLabel(), "请先打开图片文件");
            }
            else if (!ifSave) {
                JOptionPane.showMessageDialog(new JLabel(), "请设置保存图片的位置");
            }
            //当加载了图片并设置了文件保存的位置
            else{
                if(!boxBlurStack.isEmpty())
                    remove(boxBlurStack.pop());
                //一键处理：五次模糊处理加一次边缘检测
                for (int times = 0; times < 5; times++) {
                    try {
                        ImageUtility.boxBlur(save_filename);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ImageUtility.sobel(save_filename);
                }catch (CloneNotSupportedException e){
                    e.printStackTrace();
                }
                loadTiff(save_filename, boxBlurStack, afterX, afterY);
            }
        });
    }

    /*
     *  加载图片的静态方法
     *  filepath - 要处理图片的路径
     *  stack - 存放对应容器的栈
     *  x,y - 容器的位置
     */

    private void loadTiff(String filepath, Stack<ScrollingImagePanel> stack, int x, int y)
    {
        SeekableStream s = null;
        File file = new File(filepath);
        try {
            s = new FileSeekableStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TIFFDecodeParam param = null;
        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
        int imageToLoad = 0;
        RenderedImage op = null;
        try {
            op = new NullOpImage(dec.decodeAsRenderedImage(imageToLoad), null,
                    OpImage.OP_IO_BOUND, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ScrollingImagePanel panel = new ScrollingImagePanel(op, width, height);
        panel.setLocation(x, y);
        this.add(panel);
        stack.push(panel);
    }

    public static void main(String args[]) {
        EventQueue.invokeLater( ()->
        {
            JFrame frame = null;
            frame = new MainFrame();
            frame.setTitle("TIFF图像处理");
            frame.setBounds(200, 100, 1150, 600);
            //窗口不可调
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
