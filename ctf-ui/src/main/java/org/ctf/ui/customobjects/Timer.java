package org.ctf.ui.customobjects;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Timer extends Text {
    private int hour;
    private int minute;
    private int second;
   

    public Timer(int hour, int minute, int second) {
    	this.setFont(Font.font(30));
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.setText(this.getCurrentTime());
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public String getCurrentTime(){
        return hour + ":" + minute + ":" + second;
    }
    
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1),
                    e -> {
//                        if(time.getCurrentTime().equals(alarmTime.getText())){
//                            System.out.println("ALARM!");
//                        }
                        this.oneSecondPassed();
                        this.setText(this.getCurrentTime());
            }));

    public void oneSecondPassed(){
        second++;
        if(second == 60){
            minute++;
            second = 0;
            if(minute == 60){
                hour++;
                minute = 0;
                if(hour == 24){
                    hour = 0;
                    System.out.println("Next day");
                }
            }
        }
    }
}