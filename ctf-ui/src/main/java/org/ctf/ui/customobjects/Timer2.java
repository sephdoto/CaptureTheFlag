package org.ctf.ui.customobjects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Timer2 extends Text {
    private int hour;
    private int minute;
    private int second;
   

    public Timer2(int hour, int minute, int second) {
    	this.setFont(Font.font(30));
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.setText(this.getCurrentTime());
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    public void reset() {
    	this.hour = 0;
    	this.second= 0;
    	this.minute = 5;
    	this.setFill(Color.BLACK);
    }

    public String getCurrentTime(){
        return hour + ":" + minute + ":" + second;
    }
    
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1),
                    e -> {
                        this.oneSecondPassed();
                        if(this.minute <= 4 && this.second <= 30) {
                        	this.setFill(Color.RED);
                        }
                        this.setText(this.getCurrentTime());
            }));

    public void oneSecondPassed(){
       
        if(second == 0){
            minute--;
            second = 60;
            if(minute == 0){
                hour--;
                minute = 60;
                if(hour == 0){
                    hour = 0;
                    System.out.println("Next day");
                }
            }
        }
        second--;
    }
}

