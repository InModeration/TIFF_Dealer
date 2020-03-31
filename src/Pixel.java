import java.lang.management.MemoryType;

public class Pixel implements Cloneable {
    //像素的RGB三个分量，每个分量用short类型保存
    //0 ~ 255
    private short red;
    private short green;
    private short blue;

    public short getRed() {
        return red;
    }

    public void setRed(short red) {
        this.red = red;
    }

    public short getGreen() {
        return green;
    }

    public void setGreen(short green) {
        this.green = green;
    }

    public short getBlue() {
        return blue;
    }

    public void setBlue(short blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    //像素点的邻居均值
    public void setPixel(Pixel P_1, Pixel P_2, Pixel P_3, Pixel P_4, Pixel P_5, Pixel P_6, Pixel P_7, Pixel P_8, Pixel P_9, int neighbor)
    {
        short red = (short) ((P_1.getRed() + P_2.getRed() + P_3.getRed() + P_4.getRed() +
                P_5.getRed() + P_6.getRed() + P_7.getRed() + P_8.getRed() + P_9.getRed())/neighbor);
        short green = (short) ((P_1.getGreen() + P_2.getGreen() + P_3.getGreen() + P_4.getGreen() +
                P_5.getGreen() + P_6.getGreen() + P_7.getGreen() + P_8.getGreen() + P_9.getGreen())/neighbor);
        short blue = (short) ((P_1.getBlue() + P_2.getBlue() + P_3.getBlue() + P_4.getBlue() +
                P_5.getBlue() + P_6.getBlue() + P_7.getBlue() + P_8.getBlue() + P_9.getBlue())/neighbor);

        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    /*
     *  计算RGB分量梯度值
     *  P_1  P_2  P_3
     *  P_4  THIS P_5
     *  P_6  P_7  P_8
     */
    public long energy(Pixel P_1, Pixel P_2, Pixel P_3, Pixel P_4, Pixel P_5, Pixel P_6, Pixel P_7, Pixel P_8) {
        long gxRed = gxCal(P_1.getRed(), P_3.getRed(), P_4.getRed(),
                P_5.getRed(), P_6.getRed(), P_8.getRed());
        long gxBlue = gxCal(P_1.getBlue(), P_3.getBlue(), P_4.getBlue(),
                P_5.getBlue(), P_6.getBlue(), P_8.getBlue());
        long gxGreen = gxCal(P_1.getGreen(), P_3.getGreen(), P_4.getGreen(),
                P_5.getGreen(), P_6.getGreen(), P_8.getGreen());
        long gyRed = gyCal(P_1.getRed(), P_2.getRed(), P_3.getRed(),
                P_6.getRed(), P_7.getRed(), P_8.getRed());
        long gyBlue = gyCal(P_1.getBlue(), P_2.getBlue(), P_3.getBlue(),
                P_6.getBlue(), P_7.getBlue(), P_8.getBlue());
        long gyGreen = gyCal(P_1.getGreen(), P_2.getGreen(), P_3.getGreen(),
                P_6.getGreen(), P_7.getGreen(), P_8.getGreen());
        return gxRed*gxRed + gxBlue*gxBlue + gxGreen*gxGreen +
                gyRed*gyRed + gyBlue*gyBlue + gyGreen*gyGreen;
    }
    //子方法
    private long gxCal(short p_1, short p_3, short p_4, short p_5, short p_6, short p_8){
        return p_1 - p_3 + p_4*2 - p_5*2 + p_6 - p_8;
    }
    private long gyCal(short p_1, short p_2, short p_3, short p_6, short p_7, short p_8){
        return p_1 + p_2*2 + p_3 - p_6 - p_7*2 - p_8;
    }

    //将像素点三原色设置为0
    public void setZero()
    {
        short zero = (short) 0;
        this.setBlue(zero);
        this.setRed(zero);
        this.setGreen(zero);
    }

    //将三原色设置为统一值
    public void setSame(short value){
        this.setRed(value);
        this.setBlue(value);
        this.setGreen(value);
    }

    //clone method
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
