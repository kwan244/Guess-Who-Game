package nz.ac.auckland.se206.controllers;

public enum AudioStatus {
  INSTANCE;

  private boolean isMuted = false;

  public boolean isMuted() {
    return isMuted;
  }

  public void setMuted(boolean isMuted) {
    this.isMuted = isMuted;
  }
}
