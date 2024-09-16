package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class PaperController {

  @FXML private ImageView draggableGlasses;
  @FXML private ImageView paperWithWords;

  @FXML
  public void initialize() {
    paperWithWords.setOpacity(0.0);
    draggableGlasses.setOnMouseEntered(this::handleMouseEnterDragGlasses);
    draggableGlasses.setOnMouseExited(this::handleMouseExitDragGlasses);
    draggableGlasses.setOnMousePressed(this::handleMousePressDragGlasses);
    draggableGlasses.setOnMouseDragged(this::handleMouseDragGlasses);
  }

  private void handleMouseEnterDragGlasses(MouseEvent event) {
    // Handle mouse enter event for draggable glasses
    draggableGlasses.setCursor(Cursor.OPEN_HAND);
    draggableGlasses.setStyle("-fx-effect: dropshadow(three-pass-box, green, 10, 0.5, 0, 0);");
  }

  private void handleMouseExitDragGlasses(MouseEvent event) {
    // Handle mouse exit event for draggable glasses
    draggableGlasses.setCursor(Cursor.DEFAULT);
    draggableGlasses.setStyle("-fx-effect: null;");
  }

  private void handleMousePressDragGlasses(MouseEvent event) {
    // Handle mouse press event for draggable glasses
    draggableGlasses.setCursor(Cursor.CLOSED_HAND);
  }

  private void handleMouseDragGlasses(MouseEvent event) {
    // Handle dragging of the glasses
    draggableGlasses.setLayoutX(event.getX() - draggableGlasses.getFitWidth() / 2);
    draggableGlasses.setLayoutY(event.getY() - draggableGlasses.getFitHeight() / 2);

    // System.out.println(
    //     "Dragging glasses" + draggableGlasses.getLayoutX() + " " +
    // draggableGlasses.getLayoutY());
    // Check if glasses are inside the paper
    if (isInside(draggableGlasses, paperWithWords)) {
      paperWithWords.setOpacity(1.0);
    } else {
      paperWithWords.setOpacity(0.0);
    }
  }

  private boolean isInside(ImageView glasses, ImageView paper) {
    // Get the bounds of the glasses and paper
    double glassesX = glasses.getLayoutX();
    double glassesY = glasses.getLayoutY();
    double glassesWidth = glasses.getFitWidth();
    double glassesHeight = glasses.getFitHeight();

    double paperX = paper.getLayoutX();
    double paperY = paper.getLayoutY();
    double paperWidth = paper.getFitWidth();
    double paperHeight = paper.getFitHeight();

    // Check if glasses are inside the paper
    return glassesX + glassesWidth > paperX
        && glassesX < paperX + paperWidth
        && glassesY + glassesHeight > paperY
        && glassesY < paperY + paperHeight;
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
