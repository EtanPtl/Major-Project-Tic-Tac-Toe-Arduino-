import java.io.IOException;
import java.util.ArrayList;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.*;
import org.firmata4j.IODevice;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;


public class mainClass {
    public static void main(String[] args) throws IOException, InterruptedException {
        String myPort = "/dev/cu.usbserial-0001";
        IODevice device = new FirmataDevice(myPort);
        device.start();
        System.out.println("Board started.");
        device.ensureInitializationIsDone();

        I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C);
        SSD1306 OledObj = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        OledObj.init();

        game(device, OledObj);
    }

    public static void game(IODevice device, SSD1306 OledObj){

        try {

            var pot = device.getPin(14);
            var button = device.getPin(6);
            var led = device.getPin(4);
            var buzzer = device.getPin(5);
            buzzer.setMode(Pin.Mode.PWM);
            pot.setMode(Pin.Mode.ANALOG);
            button.setMode(Pin.Mode.INPUT);

            OledObj.getCanvas().setTextsize(2);
            OledObj.getCanvas().drawRoundRect(51,15,25,25,-20, MonochromeCanvas.Color.BRIGHT);

            int firstRow = -2;
            int secondRow = 20;
            int thirdRow = 42;

            int firstCol = 35;
            int secondCol = 58;
            int thirdCol = 81;

            Square square1 = new Square(firstCol,firstRow,OledObj);
            Square square2 = new Square(firstCol,secondRow,OledObj);
            Square square3 = new Square(firstCol,thirdRow,OledObj);
            Square square4 = new Square(secondCol,firstRow,OledObj);
            Square square5 = new Square(secondCol,secondRow,OledObj);
            Square square6 = new Square(secondCol,thirdRow,OledObj);
            Square square7 = new Square(thirdCol,firstRow,OledObj);
            Square square8 = new Square(thirdCol,secondRow,OledObj);
            Square square9 = new Square(thirdCol,thirdRow,OledObj);

            ArrayList<Square> Board = new ArrayList<Square>();
            Board.add(square1);
            Board.add(square2);
            Board.add(square3);
            Board.add(square4);
            Board.add(square5);
            Board.add(square6);
            Board.add(square7);
            Board.add(square8);
            Board.add(square9);

            device.addEventListener(new Listener(pot, OledObj, button, Board, led, buzzer, device));
            OledObj.display();

        }
        catch (Exception ex){
            System.out.println("couldn't connect to board." + ex);
        }

    }
}
