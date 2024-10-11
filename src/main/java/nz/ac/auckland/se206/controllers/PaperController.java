package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * Controller class for managing the paper and glasses interaction phase of the game.
 *
 * <p>This phase involves the user interacting with a draggable pair of glasses and a crumpled
 * paper. The goal is to click on the paper and glasses in a specific sequence to proceed. This
 * class handles the user interactions and updates the visual state accordingly.
 */
public class PaperController implements TimerListener {

  @FXML private ImageView draggableGlasses; // ImageView for the draggable glasses.
  @FXML private ImageView paperImage; // ImageView for the paper image.
  @FXML private Label timerLabel; // Label to display the timer.
  @FXML private SharedTimer sharedTimer; // Shared timer instance.

  private int paperClick = 0; // Counter for the number of clicks on the paper.
  private int glassesClick = 0; // Counter for the number of clicks on the glasses.

  private final String[] paperImages = {
    "/images/CrumplePaper1.png",
    "/images/CrumplePaper2.png",
    "/images/CrumplePaper3.png",
    "/images/CrumplePaper4.png",
  };

  /**
   * Method called when the timer finishes. Transitions the game to the guessing phase by opening
   * the guess view.
   */
  @Override
  public void onTimerFinished() {
    // Open the guess view
    try {
      Stage currentStage = (Stage) timerLabel.getScene().getWindow();
      App.openGuess(currentStage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the draggable glasses and paper. Sets up event handlers for mouse interactions with
   * the glasses, initializes the shared timer, and updates the paper image if it has been clicked
   * before.
   *
   * <p>This method is automatically called after the FXML fields are injected and the FXML loader
   * finishes loading the UI components.
   */
  @FXML
  public void initialize() {
    intializePaper();

    // Set the event handlers for the draggable glasses image.
    draggableGlasses.setOnMouseEntered(this::handleMouseEnterDragGlasses);
    draggableGlasses.setOnMouseExited(this::handleMouseExitDragGlasses);
    draggableGlasses.setOnMousePressed(this::handleMousePressDragGlasses);

    // Start the timer when the view is initialized and set the timer label.
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();

    // Update the paper image if it has been clicked before
    if (GuessCondition.INSTANCE.isPaperClicked()) {
      paperClick = 2;
      updatePaperImage();
    }
  }

  /** Stops the timer. This method can be called to stop the timer when it is no longer needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  /**
   * Initializes the paper image view, sets the image and event handlers for the paper interactions.
   */
  private void intializePaper() {
    if (paperImage != null) {
      paperImage.setImage(new Image(getClass().getResourceAsStream(paperImages[paperClick])));
      paperImage.setOnMouseClicked(event -> handlePaperClick(event));
      paperImage.setOnMouseEntered(this::handleMouseEnterPaper);
      paperImage.setOnMouseExited(this::handleMouseExitPaper);
    }
  }

  /**
   * Handles the click event for the paper image. Updates the paper state and appearance based on
   * the click sequence.
   *
   * @param event the mouse event triggered by clicking the paper image
   */
  private void handlePaperClick(MouseEvent event) {
    // Handle the click event for the paper image
    if (paperClick < 2) {
      paperClick++;
    } else if (paperClick == 2 && glassesClick >= 1) {
      GuessCondition.INSTANCE.setPaperClicked(true);
      paperClick++;
    }

    updatePaperImage();

    // Handle the case where the glasses are clicked before the paper
    if (paperClick == 2 && glassesClick == 0) {
      draggableGlasses.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 12, 0.5, 0, 0);");
    }

    // Handle the case where the glasses are clicked after the paper
    if (paperClick == 3) {
      paperImage.setStyle("");
      paperImage.setCursor(Cursor.DEFAULT);
      draggableGlasses.setStyle("-fx-effect: null;");
    }
  }

  /**
   * Handles mouse enter event for the paper, updating the cursor and visual effect.
   *
   * @param event the mouse event for entering the paper image area
   */
  private void handleMouseEnterPaper(MouseEvent event) {
    paperImage.setCursor(Cursor.OPEN_HAND);
    paperImage.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  /**
   * Handles mouse exit event for the paper, resetting the cursor and visual effect.
   *
   * @param event the mouse event for exiting the paper image area
   */
  private void handleMouseExitPaper(MouseEvent event) {
    paperImage.setCursor(Cursor.DEFAULT);
    paperImage.setStyle("-fx-effect: null;");
  }

  /**
   * Handles mouse enter event for the draggable glasses, updating the cursor and visual effect.
   *
   * @param event the mouse event for entering the glasses image area
   */
  private void handleMouseEnterDragGlasses(MouseEvent event) {
    // Handle mouse enter event for draggable glasses
    draggableGlasses.setCursor(Cursor.OPEN_HAND);
    draggableGlasses.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  /**
   * Handles mouse exit event for the draggable glasses, resetting the cursor and visual effect.
   *
   * @param event the mouse event for exiting the glasses image area
   */
  private void handleMouseExitDragGlasses(MouseEvent event) {
    // Handle mouse exit event for draggable glasses
    draggableGlasses.setCursor(Cursor.DEFAULT);
    draggableGlasses.setStyle("-fx-effect: null;");
  }

  /**
   * Handles mouse press event for the draggable glasses, updating the cursor and incrementing the
   * click counter.
   *
   * @param event the mouse event for pressing the glasses image area
   */
  private void handleMousePressDragGlasses(MouseEvent event) {
    // Handle mouse press event for draggable glasses
    draggableGlasses.setCursor(Cursor.CLOSED_HAND);
    glassesClick++;
  }

  /** Updates the paper image based on the current paper click state. */
  private void updatePaperImage() {
    paperImage.setImage(new Image(getClass().getResourceAsStream(paperImages[paperClick])));
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }
}
