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

  @FXML
  public void initialize() {
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
    // 获取相对于父容器的坐标，而不是全局坐标
    double offsetX = event.getSceneX() - draggableGlasses.getBoundsInParent().getMinX();
    double offsetY = event.getSceneY() - draggableGlasses.getBoundsInParent().getMinY();

    double newX = event.getSceneX() - offsetX;
    double newY = event.getSceneY() - offsetY;

    // 防止眼镜移出屏幕的边界
    if (newX >= 0
        && newX
            <= draggableGlasses.getParent().getBoundsInLocal().getWidth()
                - draggableGlasses.getFitWidth()) {
      draggableGlasses.setLayoutX(newX);
    }
    if (newY >= 0
        && newY
            <= draggableGlasses.getParent().getBoundsInLocal().getHeight()
                - draggableGlasses.getFitHeight()) {
      draggableGlasses.setLayoutY(newY);
    }
    // Handle dragging of the glasses
    // draggableGlasses.setLayoutX(event.getX() - draggableGlasses.getFitWidth() / 2);
    // draggableGlasses.setLayoutY(event.getY() - draggableGlasses.getFitHeight() / 2);

    // System.out.println(
    //     "Dragging glasses" + draggableGlasses.getLayoutX() + " " +
    // draggableGlasses.getLayoutY());
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
