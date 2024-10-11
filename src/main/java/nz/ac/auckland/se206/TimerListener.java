package nz.ac.auckland.se206;

/**
 * An interface to listen for timer completion events. Implementing classes should define what
 * actions to take when the timer finishes.
 */
public interface TimerListener {
  /**
   * This method is called when the timer reaches zero or completes its countdown. Implement this
   * method to define what should happen when the timer is finished.
   */
  void onTimerFinished();
}
