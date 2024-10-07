package nz.ac.auckland.se206.controllers;

public enum GuessCondition {
  INSTANCE;

  private boolean isComputerClicked = false;
  private boolean isShoeprintClicked = false;
  private boolean isPaperClicked = false;
  private boolean isThiefClicked = false;
  private boolean isManagerClicked = false;
  private boolean isFemaleCustomerClicked = false;
  private boolean isConditionMet = false;

  public boolean isComputerClicked() {
    return isComputerClicked;
  }

  public boolean isShoeprintClicked() {
    return isShoeprintClicked;
  }

  public boolean isPaperClicked() {
    return isPaperClicked;
  }

  public boolean isThiefClicked() {
    return isThiefClicked;
  }

  public boolean isFemaleCustomerClicked() {
    return isFemaleCustomerClicked;
  }

  public boolean isManagerClicked() {
    return isManagerClicked;
  }

  public boolean isConditionMet() {
    return isConditionMet;
  }

  public void setComputerClicked(boolean isComputerClicked) {
    this.isComputerClicked = isComputerClicked;
  }

  public void setShoeprintClicked(boolean isFeatureEnabled) {
    this.isShoeprintClicked = isFeatureEnabled;
  }

  public void setPaperClicked(boolean isFeatureEnabled) {
    this.isPaperClicked = isFeatureEnabled;
  }

  public void setManagerClicked(boolean isFeatureEnabled) {
    this.isManagerClicked = isFeatureEnabled;
  }

  public void setThiefClicked(boolean isFeatureEnabled) {
    this.isThiefClicked = isFeatureEnabled;
  }

  public void setFemaleCustomerClicked(boolean isFeatureEnabled) {
    this.isFemaleCustomerClicked = isFeatureEnabled;
  }

  public void setConditionMet(boolean isConditionMet) {
    this.isConditionMet = isConditionMet;
  }
}
