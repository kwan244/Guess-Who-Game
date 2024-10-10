package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.GuessController;
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String profession) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + profession + ".fxml"));
    // Initial font
    Font.loadFont(App.class.getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);

    Parent root = loader.load();

    ChatController chatController = loader.getController();
    chatController.setProfession(profession);

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
    stage.show();
  }

  public static void openGuess(Stage currentStage) throws IOException {
    // Open the guess view
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/AI.fxml"));
    // Initial font
    Font.loadFont(App.class.getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);
    Font.loadFont(App.class.getResource("/fonts/SweetGothicMed.otf").toExternalForm(), 24.0);
    Parent root = loader.load();

    GuessController guessController = loader.getController();
    guessController.setProfession();

    // Set the root of the scene to the guess view
    Stage stage = currentStage;
    scene = new Scene(root);
    stage.setScene(scene);
    scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
    stage.show();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openClue(MouseEvent event, String clueName) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + clueName + ".fxml"));
    // Initial font
    Font.loadFont(App.class.getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);
    Parent root = loader.load();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
    stage.show();
  }

  public static void openIntro(Stage currentStage) throws IOException {
    // Open the guess view
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/IntroScene.fxml"));

    // Initial font
    Font.loadFont(App.class.getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);
    Font.loadFont(App.class.getResource("/fonts/SweetGothicMed.otf").toExternalForm(), 24.0);

    Parent root = loader.load();

    // Set the root of the scene to the guess view
    Stage stage = currentStage;
    scene = new Scene(root);
    stage.setScene(scene);
    scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
    stage.show();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {

    // Initial font
    Font.loadFont(getClass().getResource("/fonts/DigitalDismay.otf").toExternalForm(), 24.0);
    Font.loadFont(getClass().getResource("/fonts/SweetGothicMed.otf").toExternalForm(), 24.0);
    // Load scene
    Parent root = loadFxml("CrimeScene");
    scene = new Scene(root);
    stage.setScene(scene);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    stage.show();
    stage.setOnCloseRequest(event -> handleWindowClose(event));
    root.requestFocus();
  }

  private void handleWindowClose(WindowEvent event) {
    FreeTextToSpeech.deallocateSynthesizer();
  }
}
