import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtility {

    //从文件读取TIFF图像数据到内存，图像数据存储在PixImage对象中
    //并返回该PixImage对象
    public static PixeImage readTIFFImageFromFile(String vInputFilePath) {
        File file = new File(vInputFilePath);
        PixeImage pixImage = new PixeImage();

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            Pixel[][] pixels = new Pixel[width][height];

            for (int x = 0; x < width; ++x)
                for (int y = 0; y < height; ++y) {
                    int rgb = bufferedImage.getRGB(x, y);
                    short r = (short) ((rgb & 0xff0000) >> 16);
                    short g = (short) ((rgb & 0xff00) >> 8);
                    short b = (short) (rgb & 0xff);
                    pixels[x][y] = new Pixel();
                    pixels[x][y].setRed(r);
                    pixels[x][y].setGreen(g);
                    pixels[x][y].setBlue(b);
                }

            pixImage.setWidth(width);
            pixImage.setHeight(height);
            pixImage.setPixels(pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pixImage;
    }

    //将图像数据从内存写出，保存为TIFF图像文件
    public static void writeTIFFImageToFile(PixeImage vPixImage, String vOutputFilePath) {
        BufferedImage bufferedImage = new BufferedImage(vPixImage.getWidth(), vPixImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        File file = new File(vOutputFilePath);

        Pixel[][] pixels = vPixImage.getPixels();
        for (int x = 0; x < vPixImage.getWidth(); ++x)
            for (int y = 0; y < vPixImage.getHeight(); ++y) {
                short r = pixels[x][y].getRed();
                short g = pixels[x][y].getGreen();
                short b = pixels[x][y].getBlue();
                int rgb = new Color(r,g,b).getRGB();
                bufferedImage.setRGB(x, y, rgb);
            }

        try {
            ImageIO.write(bufferedImage, "tiff", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  图像模糊处理
     *  ***** Method One *****
     *  PixeImage ReturnBoxBlur(String vSourceImagePath, String vNewImagePath)
     *  每次调用该方法对图像进行模糊处理，会返回一个新的PixImage对象
     *  并将该对象储存至指定路径处
     *
     *  ***** Method Two *****
     *  void ChangeBoxBlur(String vSourceImagePath)
     *  调用该方法对图像进行模糊处理，结果直接覆盖原图
     *  并将更改储存至源文件
     */
    public static void boxBlur(String vSourceImagePath, String vNewImagePath) throws CloneNotSupportedException {
        //获取目标路径的PixImage对象
        PixeImage sImage = readTIFFImageFromFile(vSourceImagePath);
        //深克隆该对象
        PixeImage image = (PixeImage) sImage.clone();
        //获取该对象的Pixel二维数组
        Pixel[][] sPixel = image.getPixels();
        //模糊用的Pixel二维数组
        Pixel[][] bbPixel = image.rebuildPixelsForBoxBlur();
        int x = image.getWidth() + 2;
        int y = image.getHeight() + 2;
        //利用子方法进行遍历模糊处理
        subBoxBlur(x, y, sPixel, bbPixel);

        image.setPixels(sPixel);
        writeTIFFImageToFile(image, vNewImagePath);
    }
    //Overloading
    public static void boxBlur(String vSourceImagePath) throws CloneNotSupportedException {
        //获取目标路径的PixImage对象
        PixeImage sImage = readTIFFImageFromFile(vSourceImagePath);
        //获取该对象的Pixel二维数组 - x*y
        Pixel[][] sPixel = sImage.getPixels();
        //模糊用的Pixel二维数组 - (x + 2)*(y+2)
        Pixel[][] bbPixel = sImage.rebuildPixelsForBoxBlur();
        //以及该数组的大小
        int x = sImage.getWidth() + 2;
        int y = sImage.getHeight() + 2;
        //利用子方法进行遍历模糊处理
        subBoxBlur(x, y, sPixel, bbPixel);

        sImage.setPixels(sPixel);
        writeTIFFImageToFile(sImage, vSourceImagePath);
    }
    //submethod
    private static void subBoxBlur(int x, int y, Pixel[][] sPixel, Pixel[][] bbPixel)
    {
        //遍历每一个像素点做模糊处理
        for (int i = 1; i < x - 1; i++)
            for (int j = 1; j < y - 1; j++){
                //角
                if ((i == 1 && j == 1) || (i == 1 && j == y - 2)||
                        (i == x - 2 && j == 1) || (i == x - 2 && j == y - 2))
                    sPixel[i - 1][j - 1].setPixel(bbPixel[i - 1][j - 1], bbPixel[i - 1][j], bbPixel[i - 1][j + 1],
                            bbPixel[i][j - 1], bbPixel[i][j], bbPixel[i][j + 1], bbPixel[i + 1][j - 1],
                            bbPixel[i + 1][j], bbPixel[i + 1][j + 1], 4);
                    //边
                else if ((i == 1 && j != 1) || (i == 1 && j != y - 2) ||
                        (i != x - 2 && j == 1) || (i != x - 2 && j == y - 2))
                    sPixel[i - 1][j - 1].setPixel(bbPixel[i - 1][j - 1], bbPixel[i - 1][j], bbPixel[i - 1][j + 1],
                            bbPixel[i][j - 1], bbPixel[i][j], bbPixel[i][j + 1], bbPixel[i + 1][j - 1],
                            bbPixel[i + 1][j], bbPixel[i + 1][j + 1], 6);
                    //其余
                else sPixel[i - 1][j - 1].setPixel(bbPixel[i - 1][j - 1], bbPixel[i - 1][j], bbPixel[i - 1][j + 1],
                            bbPixel[i][j - 1], bbPixel[i][j], bbPixel[i][j + 1], bbPixel[i + 1][j - 1],
                            bbPixel[i + 1][j], bbPixel[i + 1][j + 1], 9);
            }
    }

    //将能量值转换为灰色像素值
    public static short energy2gray(long vEnergy) {
        short intensity = (short) (30.0 * Math.log(1.0 + (double) vEnergy) - 256.0);

        // Make sure the returned intensity is in the range 0...255, regardless of
        // the input value.
        if (intensity < 0) {
            intensity = 0;
        } else if (intensity > 255) {
            intensity = 255;
        }
        return intensity;
    }

    /*
     * sobel边缘检测
     * 传入图片路径参数
     * 处理后覆盖原图
     */
    public static void sobel(String vSourceImagePath) throws CloneNotSupportedException {
        //获取目标路径的PixImage对象
        PixeImage sImage = readTIFFImageFromFile(vSourceImagePath);
        //获取该图的Pixels数组
        Pixel[][] pixels = sImage.getPixels();
        //获取边缘处理用的数组
        Pixel[][] sobelPixels = sImage.rebuildPixelsForSobel();
        int x = sImage.getWidth() + 2;
        int y = sImage.getHeight() + 2;

        //处理每一像素点
        //将处理结果送回pixels数组
        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                pixels[i - 1][j - 1].setSame(ImageUtility.energy2gray(sobelPixels[i][j].energy(sobelPixels[i - 1][j - 1],
                        sobelPixels[i][j - 1], sobelPixels[i + 1][j - 1], sobelPixels[i - 1][j], sobelPixels[i + 1][j],
                        sobelPixels[i - 1][j + 1], sobelPixels[i][j + 1], sobelPixels[i + 1][j + 1])));
            }
        }
        sImage.setPixels(pixels);
        writeTIFFImageToFile(sImage, vSourceImagePath);

    }

    //判断字符串是否为正整数的静态方法
    public static boolean isNumeric(String string){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }

}