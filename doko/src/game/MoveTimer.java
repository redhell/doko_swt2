package game;

import gui.GameScreen;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class MoveTimer extends Thread {

	private int seconds = GameScreen.ROUNDTIMER;
	private volatile boolean done = false;

	private PlayerField playerField;
	private Label timeLabel;

	public MoveTimer(PlayerField playerField, Label timeLabel) {
		this.playerField = playerField;
		this.timeLabel = timeLabel;
	}

	@Override
	public void run() {

		do {
			if (seconds == 0) {
				done = true;
			}
			if (seconds > 5) {
				timeLabel.setTextFill(Color.GREEN);
			}
			if (seconds > 0 && seconds <= 5) {
				timeLabel.setTextFill(Color.ORANGE);
			}
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
			
					timeLabel.setText("Time left:\n" + seconds);
					
				}
			});
			try {
				Thread.sleep(1000);
				seconds--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!done);

		playerField.timeExpired();

	}

	public void done(){
		done = true;
	}
	
	public boolean hasTime() {
		return seconds > 0;
	}

}
