package org.ctf.ui.customobjects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Timer extends Label {
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
	
	public void reset() {
		this.hour = 0;
		this.minute = 0;
		this.second = 0;
	}

	public String getCurrentTime() {
		String hourText;
		String minuteText;
		String secondText;
		if (hour < 10) {
			hourText = "0" + hour;
		} else {
			hourText = String.valueOf(hour);
		}
		if (minute < 10) {
			minuteText = "0" + minute;
		} else {
			minuteText = String.valueOf(minute);
		}
		if (second < 10) {
			secondText = "0" + second;
		} else {
			secondText = String.valueOf(second);
		}
		return hourText + ":" + minuteText + ":" + secondText;
	}

	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
		this.oneSecondPassed();
		this.setText(this.getCurrentTime());
	}));

	public void oneSecondPassed() {
		second++;
		if (second == 60) {
			minute++;
			second = 0;
			if (minute == 60) {
				hour++;
				minute = 0;
				if (hour == 24) {
					hour = 0;
					System.out.println("Next day");
				}
			}
		}
	}
}
