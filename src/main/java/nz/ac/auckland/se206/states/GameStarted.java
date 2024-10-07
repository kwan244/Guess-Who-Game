package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.controllers.GuessCondition;

/**
 * The GameStarted state of the game. Handles the initial interactions when the game starts,
 * allowing the player to chat with characters and prepare to make a guess.
 */
public class GameStarted implements GameState {

  private final GameStateContext context;

  /**
   * Constructs a new GameStarted state with the given game state context.
   *
   * @param context the context of the game state
   */
  public GameStarted(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Depending on the clicked rectangle, it either
   * provides an introduction or transitions to the chat view.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    // Transition to chat view or provide an introduction based on the clicked rectangle
    switch (rectangleId) {
      case "rectComputer":
        GuessCondition.INSTANCE.setComputerClicked(true);
        App.openClue(event, "ComputerScene");
        return;
      case "rectShoeprint":
        GuessCondition.INSTANCE.setShoeprintClicked(true);
        App.openClue(event, "ShoeprintScene");
        return;
      case "rectPaper":
        GuessCondition.INSTANCE.setPaperClicked(true);
        App.openClue(event, "PaperScene");
        return;
      case "rectPerson1":
        GuessCondition.INSTANCE.setFemaleCustomerClicked(true);
        App.openChat(event, context.getProfession(rectangleId));
        return;
      case "rectPerson2":
        GuessCondition.INSTANCE.setThiefClicked(true);
        App.openChat(event, context.getProfession(rectangleId));
        return;
      case "rectPerson3":
        GuessCondition.INSTANCE.setManagerClicked(true);
        App.openChat(event, context.getProfession(rectangleId));
        return;
      default:
        throw new IllegalArgumentException("Unknown rectangle ID: " + rectangleId);
    }
  }

  /**
   * Handles the event when the guess button is clicked. Prompts the player to make a guess and
   * transitions to the guessing state.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {
    context.setState(context.getGuessingState());
  }
}
