import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;


public class Square {
    int x;
    int y;
    String value = "";
    SSD1306 OledObj;
    boolean removed = false;


    public Square (int posx, int posy, SSD1306 Oled) {
        this.x = posx;
        this.y = posy;
        this.OledObj = Oled;

    }
    public void setValue (String posValue){
        this.value = posValue;
    }
    public void drawSelection ()
    {

        this.OledObj.getCanvas().drawRect(this.x, this.y+2, 10,10);

    }

    public void remove1 (){

        this.OledObj.getCanvas().drawRect(this.x,this.y+2, 10,10, MonochromeCanvas.Color.DARK);

    }
    public void drawValue (String Value) {
        if (!removed)
        {
            this.value = Value;
            this.OledObj.getCanvas().drawString(this.x,this.y, this.value);
        }
    }

    public void won (String v)
    {
        this.OledObj.getCanvas().drawString(this.x,this.y, v);
    }

}
