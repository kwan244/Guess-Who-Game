package nz.ac.auckland.se206.controllers;

/**
 * Singleton enum to track various game conditions related to the guessing phase.
 *
 * <p>This enum maintains the state of different game conditions, such as whether a wire has been
 * connected, certain clues have been clicked, and whether the overall condition to proceed is met.
 * It is used to determine the player's progress and enable/disable certain game interactions.
 */
public enum GuessCondition {
  /** Singleton instance of the GuessCondition enum. */
  INSTANCE;

  /** Tracks if all required wires have been connected. */
  private boolean hasWireCompleted = false;

  /** Tracks if the computer clue has been clicked. */
  private boolean isComputerClicked = false;

  /** Tracks if the shoeprint clue has been clicked. */
  private boolean isShoeprintClicked = false;

  /** Tracks if the paper clue has been clicked. */
  private boolean isPaperClicked = false;

  /** Tracks if the thief character has been clicked. */
  private boolean isThiefClicked = false;

  /** Tracks if the manager character has been clicked. */
  private boolean isManagerClicked = false;

  /** Tracks if the female customer character has been clicked. */
  private boolean isFemaleCustomerClicked = false;

  /** Tracks if the overall condition to proceed is met. */
  private boolean isConditionMet = false;

  /**
   * Returns whether the wires have been connected.
   *
   * @return {@code true} if all required wires have been connected, {@code false} otherwise
   */
  public boolean hasWireCompleted() {
    return hasWireCompleted;
  }

  /**
   * Returns whether the computer clue has been clicked.
   *
   * @return {@code true} if the computer clue is clicked, {@code false} otherwise
   */
  public boolean isComputerClicked() {
    return isComputerClicked;
  }

  /**
   * Returns whether the shoeprint clue has been clicked.
   *
   * @return {@code true} if the shoeprint clue is clicked, {@code false} otherwise
   */
  public boolean isShoeprintClicked() {
    return isShoeprintClicked;
  }

  /**
   * Returns whether the paper clue has been clicked.
   *
   * @return {@code true} if the paper clue is clicked, {@code false} otherwise
   */
  public boolean isPaperClicked() {
    return isPaperClicked;
  }

  /**
   * Returns whether the thief character has been clicked.
   *
   * @return {@code true} if the thief character is clicked, {@code false} otherwise
   */
  public boolean isThiefClicked() {
    return isThiefClicked;
  }

  /**
   * Returns whether the female customer character has been clicked.
   *
   * @return {@code true} if the female customer character is clicked, {@code false} otherwise
   */
  public boolean isFemaleCustomerClicked() {
    return isFemaleCustomerClicked;
  }

  /**
   * Returns whether the manager character has been clicked.
   *
   * @return {@code true} if the manager character is clicked, {@code false} otherwise
   */
  public boolean isManagerClicked() {
    return isManagerClicked;
  }

  /**
   * Returns whether all conditions to proceed have been met.
   *
   * @return {@code true} if all conditions are met, {@code false} otherwise
   */
  public boolean isConditionMet() {
    return isConditionMet;
  }

  /**
   * Sets the wire completion status.
   *
   * @param hasWireCompleted {@code true} if wires have been connected, {@code false} otherwise
   */
  public void setWireCompleted(boolean hasWireCompleted) {
    this.hasWireCompleted = hasWireCompleted;
  }

  /**
   * Sets the computer click status.
   *
   * @param isComputerClicked {@code true} if the computer clue has been clicked, {@code false}
   *     otherwise
   */
  public void setComputerClicked(boolean isComputerClicked) {
    this.isComputerClicked = isComputerClicked;
  }

  /**
   * Sets the shoeprint click status.
   *
   * @param isShoeprintClicked {@code true} if the shoeprint clue has been clicked, {@code false}
   *     otherwise
   */
  public void setShoeprintClicked(boolean isShoeprintClicked) {
    this.isShoeprintClicked = isShoeprintClicked;
  }

  /**
   * Sets the paper click status.
   *
   * @param isPaperClicked {@code true} if the paper clue has been clicked, {@code false} otherwise
   */
  public void setPaperClicked(boolean isPaperClicked) {
    this.isPaperClicked = isPaperClicked;
  }

  /**
   * Sets the manager click status.
   *
   * @param isManagerClicked {@code true} if the manager character has been clicked, {@code false}
   *     otherwise
   */
  public void setManagerClicked(boolean isManagerClicked) {
    this.isManagerClicked = isManagerClicked;
  }

  /**
   * Sets the thief click status.
   *
   * @param isThiefClicked {@code true} if the thief character has been clicked, {@code false}
   *     otherwise
   */
  public void setThiefClicked(boolean isThiefClicked) {
    this.isThiefClicked = isThiefClicked;
  }

  /**
   * Sets the female customer click status.
   *
   * @param isFemaleCustomerClicked {@code true} if the female customer character has been clicked,
   *     {@code false} otherwise
   */
  public void setFemaleCustomerClicked(boolean isFemaleCustomerClicked) {
    this.isFemaleCustomerClicked = isFemaleCustomerClicked;
  }

  /**
   * Sets the overall condition status.
   *
   * @param isConditionMet {@code true} if all conditions are met, {@code false} otherwise
   */
  public void setConditionMet(boolean isConditionMet) {
    this.isConditionMet = isConditionMet;
  }
}
