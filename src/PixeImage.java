import java.util.Arrays;

public class PixeImage implements Cloneable {
    //图像的像素存储在二维数组中，例如pixels[x][y]表示图像中坐标(x,y)位置的像素
    //x列y行
    private Pixel[][] pixels;
    //width, height - length rather than index
    private int width;         //图像宽度
    private int height;        //图像高度

    //获取图像像素的二位数组
    public Pixel[][] getPixels() {
        return pixels;
    }

    public void setPixels(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /*
     *  重建Pixel数组,用于模糊处理
     *  若原先为x * y,将其扩展为 (x+2) * (y+2)
     *  其中第0、x+1列，第0、y+1行为0
     *  全部利用clone方法对重建数组的元素进行赋值
     */
    public Pixel[][] rebuildPixelsForBoxBlur() throws CloneNotSupportedException {
        int x = getWidth() + 2;
        int y = getHeight() + 2;
        Pixel pixel = new Pixel();
        pixel.setZero();

        //重建数组的声明
        Pixel[][] rebuild = new Pixel[x][y];
        //temp数组用于导入重建数组
        Pixel[][] temp = getPixels();

        //将第0、x+1列与第0、y+1行设为0
        for (int i = 0; i < y; i++){
            rebuild[0][i] = (Pixel) pixel.clone();
            rebuild[x - 1][i] = (Pixel) pixel.clone();
        }
        for (int j = 1; j < x - 1; j++){
            rebuild[j][0] = (Pixel) pixel.clone();
            rebuild[j][y - 1] = (Pixel) pixel.clone();
        }

        //将原数组数据导入重建的数组
        for (int i = 1; i < x - 1; i++)
            for (int j = 1; j < y - 1; j++)
                rebuild[i][j] = (Pixel) temp[i - 1][j - 1].clone();

        return rebuild;
    }

    /*
     *  重建Pixel数组，用于边缘检测
     *  将x*y扩展至 （x+2）*（y+2）
     *  使原像素点都有九个邻居
     *  全部利用clone对重建的数组元素赋值
     */
    public Pixel[][] rebuildPixelsForSobel() throws CloneNotSupportedException {
        //初始化重建数组的两个下标
        int x = getWidth() + 2;
        int y = getHeight() + 2;
        //重建数组的初始化
        Pixel[][] rebuild = new Pixel[x][y];
        //利用temp数组向重建数组导入数据
        Pixel[][] temp = getPixels();

        //将第0、x-1列设置
        //将第0、y-1行设置
        for (int i = 1; i < x - 1; i++){
            rebuild[i][0] = (Pixel) temp[i - 1][0].clone();
            rebuild[i][y - 1] = (Pixel) temp[i - 1][y - 3].clone();
        }
        for (int j = 1; j < y - 1; j++){
            rebuild[0][j] = (Pixel) temp[0][j - 1].clone();
            rebuild[x - 1][j] = (Pixel) temp[x - 3][j - 1].clone();
        }
        //四个角设置
        rebuild[0][0] = (Pixel) temp[0][0].clone();
        rebuild[0][y - 1] = (Pixel) temp[0][y - 3].clone();
        rebuild[x - 1][0] = (Pixel) temp[x - 3][0].clone();
        rebuild[x - 1][y - 1] = (Pixel) temp[x - 3][y - 3].clone();

        //将temp数据导入rebuild中的 [1][1] -- [x - 3][y - 3]
        for (int i = 0; i < x - 2; i++)
            for (int j = 0; j < y - 2; j++)
                rebuild[i + 1][j + 1] = (Pixel) temp[i][j].clone();
        return rebuild;
    }

    //clone method
    //实现深克隆
    public Object clone()
    {
        PixeImage o = null;
        try{
            o = (PixeImage)super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                try {
                    o.pixels[i][j] = (Pixel)pixels[i][j].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        return o;
    }
}
