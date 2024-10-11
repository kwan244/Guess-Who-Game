package nz.ac.auckland.se206.controllers;

/**
 * Singleton enum to manage the audio status throughout the application.
 *
 * <p>This enum uses the singleton pattern to maintain a global state for whether the audio is muted
 * or not. It provides methods to check the mute status and to update it.
 */
public enum AudioStatus {
  /** The single instance of the AudioStatus enum. */
  INSTANCE;

  /** Flag to indicate whether the audio is muted or not. */
  private boolean isMuted = false;

  /**
   * Returns whether the audio is currently muted.
   *
   * @return {@code true} if the audio is muted, {@code false} otherwise
   */
  public boolean isMuted() {
    return isMuted;
  }

  /**
   * Sets the mute status for the audio.
   *
   * @param isMuted {@code true} to mute the audio, {@code false} to unmute
   */
  public void setMuted(boolean isMuted) {
    this.isMuted = isMuted;
  }
}
