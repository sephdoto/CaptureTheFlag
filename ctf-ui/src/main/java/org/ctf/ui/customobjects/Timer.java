package org.ctf.ui.customobjects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;
/**
 * This class represents a timer which just counts the time up
 * @author Manuel Krakowski
 */
public class Timer extends Label {
	private int hour;
	private int minute;
	private int second;
	
	public Timer(int hour, int minute, int second) {
		this.setFont(Font.font(30));
		this.hour = hour;
		this.minute = minute;
		this.second = second -1;
		this.setText(this.getCurrentTime());
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	/**
	 * Stops the timer.
	 * 
	 * @author sistumpf
	 */
	public void stop() {
	  timeline.stop();
	}
	
	/**
	 * Resets the time to 0, used to count up for the move time when it is not limited
	 * @author Manuel Krakowski
	 */
	public void reset() {
		this.hour = 0;
		this.minute = 0;
		this.second = -1;
	}

	/**
	 * formats the current time with zeros as padding
	 * @author Manuel Krakowski
	 * @return
	 */
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

	/**
	 * Counts the time up using a timeline animation
	 * @author Manuel Krakowski
	 */
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
				}
			}
		}
	}
}
