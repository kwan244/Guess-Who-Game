package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;

/**
 * Controller class for managing the shoeprint scene in the application. This class handles user
 * interactions such as hovering and clicking on the shoeprint and magnifier, and manages the timer
 * for the scene. It is responsible for updating the view to show a larger shoeprint image when the
 * magnifier is clicked.
 */
public class ShoeprintController implements TimerListener {

  @FXML private Label timerLabel; // Label to display the countdown timer
  @FXML private SharedTimer sharedTimer; // Shared timer object for timing operations
  @FXML private ImageView shoeprint; // ImageView for displaying the shoeprint image
  @FXML private ImageView magnifier; // ImageView for displaying the magnifier image
  @FXML private ImageView largeMag; // ImageView to display when the large shoeprint is shown
  @FXML private Labeled size; // Label displaying additional size information for the shoeprint

  private boolean magnifierClick = false; // Flag to indicate if the magnifier has been clicked

  /**
   * Called when the timer finishes. Navigates the application to the guess view when the timer
   * reaches zero.
   */
  @Override
  public void onTimerFinished() {
    try {
      Stage currentStage = (Stage) timerLabel.getScene().getWindow();
      App.openGuess(currentStage); // Open the guess view
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the controller by setting up mouse event handlers for the magnifier and shoeprint
   * images, and starts the shared timer for this scene.
   */
  @FXML
  public void initialize() {
    // Set mouse event handlers for the magnifier image
    magnifier.setOnMouseEntered(this::onMagnifierHover);
    magnifier.setOnMouseExited(this::onMagnifierExit);
    magnifier.setOnMousePressed(this::handleMagnifierClick);

    // Set mouse event handlers for the shoeprint image
    shoeprint.setOnMouseEntered(this::onShoeprintHover);
    shoeprint.setOnMouseExited(this::onShoeprintExit);
    shoeprint.setOnMousePressed(this::handleShoeprintClick);

    // Start the shared timer and set its label and listener
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
  }

  /**
   * Stops the shared timer. This method can be called to stop the timer when it is no longer
   * needed.
   */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop(); // Stop the shared timer
    }
  }

  /**
   * Handles the click event on the shoeprint image. If the magnifier has been clicked, it replaces
   * the shoeprint image with a larger version and updates the display.
   *
   * @param event the mouse event triggered by clicking the shoeprint image
   */
  @FXML
  private void handleShoeprintClick(MouseEvent event) {
    if (magnifierClick) { // Check if magnifier has been clicked
      try {
        // Load the larger shoeprint image from the resources directory
        Image largeShoeprint = new Image("/images/ShoeprintScene.jpg");

        // Set the larger image in the existing ImageView
        shoeprint.setImage(largeShoeprint);

        // Resize the ImageView to match the new image's size
        shoeprint.setFitWidth(500); // Adjust width as necessary
        shoeprint.setFitHeight(500); // Adjust height as necessary

        // Move the image upwards by adjusting its layoutY property
        shoeprint.setLayoutY(shoeprint.getLayoutY() - 200); // Move 200 units upwards

        // Show additional visual elements for the enlarged view
        largeMag.setVisible(true);
        size.setVisible(true);

      } catch (Exception e) {
        System.out.println("Failed to load the large shoeprint image: " + e.getMessage());
      }
    }
  }

  /**
   * Handles the click event on the magnifier image. Updates the cursor to a closed hand and sets
   * the magnifier click flag.
   *
   * @param event the mouse event triggered by clicking the magnifier image
   */
  @FXML
  private void handleMagnifierClick(MouseEvent event) {
    magnifier.setCursor(Cursor.CLOSED_HAND); // Change cursor to closed hand
    magnifierClick = true; // Set magnifierClick flag to true
  }

  /**
   * Handles the "Go Back" button click event. Navigates the application back to the "CrimeScene"
   * view.
   *
   * @param event the action event triggered by clicking the "Go Back" button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error during the view transition
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene"); // Navigate back to the CrimeScene view
  }

  /**
   * Handles the mouse hover event on the shoeprint image. If the magnifier has been clicked,
   * updates the cursor and applies a green drop shadow effect.
   *
   * @param event the mouse event triggered by hovering over the shoeprint image
   */
  @FXML
  private void onShoeprintHover(MouseEvent event) {
    if (magnifierClick) { // Check if magnifier has been clicked
      shoeprint.setCursor(Cursor.OPEN_HAND); // Set cursor to open hand
      shoeprint.setStyle(
          "-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);"); // Apply green shadow
    }
  }

  /**
   * Handles the mouse exit event on the shoeprint image. If the magnifier has been clicked, resets
   * the cursor and removes any applied visual effects.
   *
   * @param event the mouse event triggered by exiting the shoeprint image
   */
  @FXML
  private void onShoeprintExit(MouseEvent event) {
    if (magnifierClick) { // Check if magnifier has been clicked
      shoeprint.setCursor(Cursor.DEFAULT); // Reset cursor to default
      shoeprint.setStyle("-fx-effect: null;"); // Remove any applied effects
    }
  }

  /**
   * Handles the mouse hover event on the magnifier image. Changes the cursor to an open hand and
   * applies a green drop shadow effect.
   *
   * @param event the mouse event triggered by hovering over the magnifier image
   */
  @FXML
  private void onMagnifierHover(MouseEvent event) {
    magnifier.setCursor(Cursor.OPEN_HAND); // Change cursor to open hand
    magnifier.setStyle(
        "-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);"); // Apply green shadow
  }

  /**
   * Handles the mouse exit event on the magnifier image. Resets the cursor and removes any applied
   * visual effects.
   *
   * @param event the mouse event triggered by exiting the magnifier image
   */
  @FXML
  private void onMagnifierExit(MouseEvent event) {
    magnifier.setCursor(Cursor.DEFAULT); // Reset cursor to default
    magnifier.setStyle("-fx-effect: null;"); // Remove any applied effects
  }
}
