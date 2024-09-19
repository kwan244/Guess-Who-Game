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
  @FXML private ImageView Shoeprint;
  @FXML private ImageView Magnifier;
  @FXML private ImageView LargeMag;
  @FXML private Labeled Size;
  private boolean MagnifierClick = false;

  // private final String Shoeprints = "/images/Shoeprint.jpg";

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
    Magnifier.setOnMouseEntered(this::onMagnifierHover);
    Magnifier.setOnMouseExited(this::onMagnifierExit);
    Magnifier.setOnMousePressed(this::handleMagnifierClick);

    Shoeprint.setOnMouseEntered(this::onShoeprintHover);
    Shoeprint.setOnMouseExited(this::onShoeprintExit);
    Shoeprint.setOnMousePressed(this::handleShoeprintClick);

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
    if (MagnifierClick) {
      try {
        // Load the larger shoeprint image
        Image largeShoeprint = new Image("/images/ShoeprintScene.jpg");

        // Set the larger image in the existing ImageView
        Shoeprint.setImage(largeShoeprint);

        // Optionally, resize the ImageView to match the new image's size
        Shoeprint.setFitWidth(500); // Adjust these values as necessary
        Shoeprint.setFitHeight(500); // Adjust these values as necessary

        // Move the image upwards by adjusting its layoutY property
        Shoeprint.setLayoutY(
            Shoeprint.getLayoutY() - 200); // Move 100 units upward, adjust as necessary

        LargeMag.setVisible(true);
        Size.setVisible(true);

      } catch (Exception e) {
        System.out.println("Failed to load the large shoeprint image: " + e.getMessage());
      }
    }
  }

  @FXML
  private void handleMagnifierClick(MouseEvent event) {
    Magnifier.setCursor(Cursor.CLOSED_HAND);
    MagnifierClick = true;
  }

  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("CrimeScene");
  }

  @FXML
  private void onShoeprintHover(MouseEvent event) {
    if (MagnifierClick) {
      Shoeprint.setCursor(Cursor.OPEN_HAND);
      Shoeprint.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
    }
  }

  @FXML
  private void onShoeprintExit(MouseEvent event) {
    if (MagnifierClick) {
      Shoeprint.setCursor(Cursor.DEFAULT);
      Shoeprint.setStyle("-fx-effect: null;");
    }
  }

  @FXML
  private void onMagnifierHover(MouseEvent event) {
    Magnifier.setCursor(Cursor.OPEN_HAND);
    Magnifier.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  @FXML
  private void onMagnifierExit(MouseEvent event) {
    Magnifier.setCursor(Cursor.DEFAULT);
    Magnifier.setStyle("-fx-effect: null;");
  }
}
