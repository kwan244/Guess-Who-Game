package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/** A singleton class to manage a shared timer that can be accessed from multiple controllers. */
public class SharedTimer {

  // Static fields
  private static final int START_TIME = 300; // 5 minutes in seconds
  private static final int GUESS_TIME = 60; // 60 seconds in seconds
  private static SharedTimer instance;

  /**
   * Gets the single instance of the SharedTimer.
   *
   * @return the single instance of SharedTimer
   */
  public static SharedTimer getInstance() {
    if (instance == null) {
      instance = new SharedTimer();
    }
    return instance;
  }

  // Instance fields
  private Label timerLabel;
  private int timeInSeconds;
  private Timeline timeline;
  private boolean timerEnded = false;
  private boolean hasReset = false;
  private TimerListener timerListener;

  /** Private constructor to enforce singleton pattern. */
  private SharedTimer() {
    this.timeInSeconds = START_TIME;
  }

  /**
   * Sets the label to update with the timer.
   *
   * @param timerLabel the label to display the timer
   */
  public void setTimerLabel(Label timerLabel) {
    this.timerLabel = timerLabel;
    updateLabel();
  }

  /**
   * Starts the timer. The timer updates every second and displays the remaining time in the format
   * MM:SS.
   */
  public synchronized void start() {
    if (timeline == null) {
      timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
      timeline.setCycleCount(Timeline.INDEFINITE);
      timeline.play();
    } else {
      timeline.play();
    }
  }

  /** Updates the timer and the label. This method is called every second by the Timeline. */
  private synchronized void updateTimer() {
    if (timeInSeconds > 0) {
      timeInSeconds--;
      updateLabel();
    } else {
      stop();
      notifyTimerFinished();
    }
  }

  /** Updates the label with the current remaining time. */
  private synchronized void updateLabel() {
    if (timerLabel != null) {
      int minutes = timeInSeconds / 60;
      int seconds = timeInSeconds % 60;
      timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
  }

  /**
   * Stops the timer and updates the internal state.
   *
   * <p>This method stops the ongoing timer, halting any scheduled actions or events. It sets the
   * {@code timerEnded} flag to {@code true}, indicating that the timer has completed its countdown
   * or execution. This method is synchronized to ensure thread safety, preventing concurrent
   * modifications to the timer state while it is being stopped.
   *
   * <p>If the timer was not previously running (i.e., {@code timeline} is {@code null}), the method
   * will simply update the state without any further action.
   */
  public synchronized void stop() {
    if (timeline != null) {
      timeline.stop();
    }
    timerEnded = true;
  }

  public synchronized void setTimerListener(TimerListener listener) {
    this.timerListener = listener;
  }

  public synchronized boolean hasTimerEnded() {
    return timerEnded;
  }

  private synchronized void notifyTimerFinished() {
    if (timerListener != null) {
      timerListener.onTimerFinished();
    }
  }

  /**
   * Resets the timer to 60 seconds. If the timer has already been reset once, the game will end.
   * Otherwise, the timer will be reset to 60 seconds.
   */
  public synchronized void resetToSixtySeconds() {
    if (hasReset) {
      notifyTimerFinished(); // End the game if timer has already reset once
    } else {
      this.timeInSeconds = GUESS_TIME;
      hasReset = true; // Set the flag to true after the first reset
      updateLabel();
      start();
    }
  }

  public synchronized void resetToFiveMins() {
    this.timeInSeconds = START_TIME;
    start();
  }

  public synchronized boolean getHasReset() {
    return hasReset;
  }

  public synchronized void resetHasReset() {
    hasReset = false;
  }
}
