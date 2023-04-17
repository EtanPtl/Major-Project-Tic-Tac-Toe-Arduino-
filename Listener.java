import org.firmata4j.IODevice;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class Listener implements IODeviceEventListener {

    SSD1306 OledObj;
    Pin pot;
    Pin button;
    Pin Led;
    Pin Buzzer;
    IODevice device;
    ArrayList<Square> Board;
    int select =1;
    int oldSelect=1;
    int counter = 0;
    int time;
    int timeOld = 0;
    Boolean win = false;
    Boolean gameOver = false;
    String winningValue = "";


    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss:ms");
    LocalDateTime now = LocalDateTime.now();


    public Listener(Pin Pot, SSD1306 OledObj, Pin Button, ArrayList<Square> board, Pin led, Pin buzzer, IODevice device) {
        this.button = Button;
        this.pot = Pot;
        this.OledObj = OledObj;
        this.Board = board;
        this.Led = led;
        this.Buzzer = buzzer;
        this.device = device;

    }
    @Override
    public void onPinChange(IOEvent event) {

        if (event.getPin().getIndex() == this.pot.getIndex()){


            if (this.pot.getValue() <= 113){
                select = 0;
            }
            else if(this.pot.getValue() <= 226 && this.pot.getValue() > 113 && Board.size() > 1) {
                select = 1;
            }
            else if(this.pot.getValue() <= 339 && this.pot.getValue() > 226 && Board.size() > 2){
                select = 2;
            }
            else if(this.pot.getValue() <= 452 && this.pot.getValue() > 339 && Board.size() > 3) {
                select = 3;
            }
            else if(this.pot.getValue() <= 565 && this.pot.getValue() > 452 && Board.size() > 4) {
                select = 4;
            }
            else if(this.pot.getValue() <= 678 && this.pot.getValue() > 565 && Board.size() > 5) {
                select = 5;
            }
            else if(this.pot.getValue() <= 791 && this.pot.getValue() > 678 && Board.size() > 6) {
                select = 6;
            }
            else if(this.pot.getValue() <= 904 && this.pot.getValue() > 791 && Board.size() > 7) {
                select = 7;
            }
            else if(this.pot.getValue() <=  1023 && this.pot.getValue() > 904 && Board.size() > 8) {
                select = 8;
            }

            if (oldSelect != select){
                Board.get(select).drawSelection();
                OledObj.display();
                oldSelect = select;

                Board.get(oldSelect).remove1();


            }
        }


        if (event.getPin().getIndex() == this.button.getIndex()){

            if(button.getValue() == 1) {
                now = LocalDateTime.now();
            }

            time = Integer.parseInt(dtf.format(now).replace(":",""));


            if (counter%2 !=0 && button.getValue()==1 && time != timeOld){
                Board.get(select).drawValue("x");
                Board.get(select).removed = true;
                counter++;
                timeOld = time;

            }
            else if (counter%2 ==0 && button.getValue()==1 && time != timeOld){
                timeOld = time;
                Board.get(select).drawValue("o");
                Board.get(select).removed = true;
                counter++;
            }

            if (!Board.get(0).value.equals("")) {
                if (Board.get(0).value.equals(Board.get(1).value) && Board.get(0).value.equals(Board.get(2).value)) {
                   win = true;
                    winningValue = Board.get(0).value;
                }
                else if(Board.get(0).value.equals(Board.get(3).value) && Board.get(0).value.equals(Board.get(6).value)){
                    win = true;
                    winningValue = Board.get(0).value;
                }
            }
            if (!Board.get(4).value.equals("")){
                if (Board.get(4).value.equals(Board.get(3).value) && Board.get(4).value.equals(Board.get(5).value)){
                    win = true;
                    winningValue = Board.get(4).value;
                }
                else if (Board.get(4).value.equals(Board.get(1).value) && Board.get(4).value.equals(Board.get(7).value)){
                    win = true;
                    winningValue = Board.get(4).value;
                }
                else if (Board.get(4).value.equals(Board.get(0).value) && Board.get(4).value.equals(Board.get(8).value)){
                    win = true;
                    winningValue = Board.get(4).value;
                }
                else if (Board.get(4).value.equals(Board.get(2).value) && Board.get(4).value.equals(Board.get(6).value)){
                    win = true;
                    winningValue = Board.get(4).value;
                }
            }
            if (!Board.get(8).value.equals("")){
                if (Board.get(8).value.equals(Board.get(7).value) && Board.get(8).value.equals(Board.get(6).value)){
                    win = true;
                    winningValue = Board.get(8).value;
                }
                else if(Board.get(8).value.equals(Board.get(5).value) && Board.get(8).value.equals(Board.get(2).value)){
                    win = true;
                    winningValue = Board.get(8).value;
                }
            }

            if (win && button.getValue() == 1){
                gameOver = true;
            }

            int numSquaresTaken = 0;

            for (Square s: Board){
                if (!s.value.equals("")){
                    numSquaresTaken++;
                }
            }
            if (numSquaresTaken == 9 && !win){
                gameOver = true;
            }


            if (gameOver){
                try {
                    pot.setMode(Pin.Mode.OUTPUT);
                    for (Square s : Board)
                    {
                        s.won(" ");
                        s.value = "";
                        s.removed = false;
                    }
                    OledObj.getCanvas().drawRoundRect(51,15,25,25,-20, MonochromeCanvas.Color.DARK);

                    if (win){
                        this.Led.setValue(1);
                        this.Buzzer.setValue(1);
                        OledObj.getCanvas().drawString(30,25,(winningValue + " wins!"));

                    }
                    else {
                        OledObj.getCanvas().drawString(35,25, "Tie!");
                    }

                    OledObj.display();
                    Thread.sleep(2000);

                    this.Led.setValue(0);
                    this.Buzzer.setValue(0);
                    pot.setMode(Pin.Mode.ANALOG);
                    OledObj.clear();
                    gameOver = false;
                    win = false;
                    counter = 0;
                    timeOld = 0;
                    winningValue = "";
                    OledObj.getCanvas().drawRoundRect(51,15,25,25,-20, MonochromeCanvas.Color.BRIGHT);


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            OledObj.display();
        }
    }


    @Override
    public void onStart(IOEvent ioEvent) {}
    @Override
    public void onStop(IOEvent ioEvent) {}
    @Override
    public void onMessageReceive(IOEvent ioEvent, String s) {}
}
