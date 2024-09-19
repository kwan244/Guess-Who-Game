package nz.ac.auckland.se206.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javazoom.jl.player.Player;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class GuessController implements TimerListener {

  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView Incorrect;
  @FXML private ImageView Correct;
  @FXML private ImageView FemaleImageGlow;
  @FXML private ImageView MaleImageGlow;
  @FXML private ImageView ManagerImageGlow;
  @FXML private ImageView FemaleImage;
  @FXML private ImageView MaleImage;
  @FXML private ImageView ManagerImage;
  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;
  private boolean isGuessed = false;
  private String currentGuess;

  @Override
  public void onTimerFinished() {}

  @FXML
  public void initialize() {
    ManagerImage.setOnMouseClicked(this::handleGuessManager);
    FemaleImage.setOnMouseClicked(this::handleGuessFemale);
    MaleImage.setOnMouseClicked(this::handleGuessMale);
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

  // private void applyClickEffect(ImageView selectedImage) {
  //   // Reset styles for all icons
  //   FemaleImage.setStyle("-fx-effect: null;");
  //   MaleImage.setStyle("-fx-effect: null;");
  //   ManagerImage.setStyle("-fx-effect: null;");

  //   // Apply drop shadow effect to the selected icon
  //   selectedImage.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 20, 0.7, 0, 0);");
  // }

  @FXML
  private void handleGuessMale(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "male"; // Store the guess
      MaleImageGlow.setVisible(true);
      ManagerImageGlow.setVisible(false);
      FemaleImageGlow.setVisible(false);
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void handleGuessManager(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "manager"; // Store the guess
      MaleImageGlow.setVisible(false);
      ManagerImageGlow.setVisible(true);
      FemaleImageGlow.setVisible(false);
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void handleGuessFemale(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "female"; // Store the guess
      MaleImageGlow.setVisible(false);
      ManagerImageGlow.setVisible(false);
      FemaleImageGlow.setVisible(true);
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void onPlayAgain(ActionEvent event) throws IOException {
    GuessCondition.INSTANCE.setComputerClicked(false);
    GuessCondition.INSTANCE.setShoeprintClicked(false);
    GuessCondition.INSTANCE.setPaperClicked(false);
    GuessCondition.INSTANCE.setManagerClicked(false);
    GuessCondition.INSTANCE.setThiefClicked(false);
    GuessCondition.INSTANCE.setFemaleCustomerClicked(false);
    RoomController.context.setState(RoomController.context.getGameStartedState());
    RoomController.isFirstTimeInit = true;
    App.setRoot("CrimeScene");
        // Reset and start the timer
        SharedTimer sharedTimer = SharedTimer.getInstance();
        sharedTimer.stop(); // Ensure the timer is stopped
        sharedTimer.resetToFiveMins(); // Reset the timer to 10 seconds
        sharedTimer.start(); // Start the timer again
    
        // Update state and navigate to the "CrimeScene" view
        RoomController.context.setState(RoomController.context.getGameStartedState());
        RoomController.isFirstTimeInit = true;
        App.setRoot("CrimeScene");
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    return PromptEngineering.getPrompt("prompts/" + profession + ".txt", map);
  }

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setProfession(String profession) {
    this.profession = profession;
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      runGpt(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    String sender;
    if (msg.getRole().equals("user")) {
      sender = "You";
    } else {
      sender = "AI";
    }
    Platform.runLater(() -> txtaChat.appendText(sender + ": " + msg.getContent() + "\n\n"));
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    if (chatCompletionRequest == null) {
      throw new IllegalStateException(
          "ChatCompletionRequest is not initialized. Make sure to call setProfession first.");
    }
    chatCompletionRequest.addMessage(msg);
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGpt(msg);
    if (currentGuess != null) {
      switch (currentGuess) {
        case "male":
          Correct.setVisible(true);
          break;
        case "female":
        case "manager":
          Incorrect.setVisible(true);
          break;
      }
      isGuessed = true; // Mark that the player has guessed
    }
  }

  private void playAudio(String mp3FilePath) {
    try {
      FileInputStream fileInputStream =
          new FileInputStream("src/main/resources/sounds/" + mp3FilePath + ".mp3");
      Player player = new Player(fileInputStream);
      player.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
