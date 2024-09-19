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

public class ShoeprintController implements TimerListener {

  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView shoeprint;
  @FXML private ImageView magnifier;
  @FXML private ImageView largeMag;
  @FXML private Labeled size;
  private boolean magnifierClick = false;

  @Override
  public void onTimerFinished() {
    // Reset timer to sixty seconds
    sharedTimer.resetToSixtySeconds();
    // Open the guess view
    try {
      Stage currentStage = (Stage) timerLabel.getScene().getWindow();
      App.openGuess(currentStage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void initialize() {
    // Set the smaller shoeprint image
    magnifier.setOnMouseEntered(this::onMagnifierHover);
    magnifier.setOnMouseExited(this::onMagnifierExit);
    magnifier.setOnMousePressed(this::handleMagnifierClick);

    // Set the smaller shoeprint image
    shoeprint.setOnMouseEntered(this::onShoeprintHover);
    shoeprint.setOnMouseExited(this::onShoeprintExit);
    shoeprint.setOnMousePressed(this::handleShoeprintClick);

    // Start the timer
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();
  }

  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  @FXML
  private void handleShoeprintClick(MouseEvent event) {
    if (magnifierClick) {
      try {
        // Load the larger shoeprint image
        Image largeShoeprint = new Image("/images/ShoeprintScene.jpg");

        // Set the larger image in the existing ImageView
        shoeprint.setImage(largeShoeprint);

        // Optionally, resize the ImageView to match the new image's size
        shoeprint.setFitWidth(500); // Adjust these values as necessary
        shoeprint.setFitHeight(500); // Adjust these values as necessary

        // Move the image upwards by adjusting its layoutY property
        shoeprint.setLayoutY(
            shoeprint.getLayoutY() - 200); // Move 100 units upward, adjust as necessary

        largeMag.setVisible(true);
        size.setVisible(true);

      } catch (Exception e) {
        System.out.println("Failed to load the large shoeprint image: " + e.getMessage());
      }
    }
  }

  @FXML
  private void handleMagnifierClick(MouseEvent event) {
    magnifier.setCursor(Cursor.CLOSED_HAND);
    magnifierClick = true;
  }

  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }

  @FXML
  private void onShoeprintHover(MouseEvent event) {
    if (magnifierClick) {
      shoeprint.setCursor(Cursor.OPEN_HAND);
      shoeprint.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
    }
  }

  @FXML
  private void onShoeprintExit(MouseEvent event) {
    if (magnifierClick) {
      shoeprint.setCursor(Cursor.DEFAULT);
      shoeprint.setStyle("-fx-effect: null;");
    }
  }

  @FXML
  private void onMagnifierHover(MouseEvent event) {
    magnifier.setCursor(Cursor.OPEN_HAND);
    magnifier.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  @FXML
  private void onMagnifierExit(MouseEvent event) {
    magnifier.setCursor(Cursor.DEFAULT);
    magnifier.setStyle("-fx-effect: null;");
  }
}
