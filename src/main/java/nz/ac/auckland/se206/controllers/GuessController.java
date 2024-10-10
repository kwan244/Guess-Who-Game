package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javazoom.jl.player.Player;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SharedTimer;
import nz.ac.auckland.se206.TimerListener;
import nz.ac.auckland.se206.prompts.PromptEngineering;

/**
 * Controller class responsible for managing the guessing phase of the game, where players must
 * select the correct suspect in a theft investigation. The controller handles the user interface
 * interactions, manages the timer, sends messages to the GPT model for processing, and provides
 * feedback to the player.
 *
 * <p>Implements the {@link TimerListener} interface to respond to timer events.
 *
 * <ul>
 *   <li>Allows the player to select a suspect from three choices: female, male, and manager.
 *   <li>Displays visual feedback (e.g., correct/incorrect indicators) based on the player's guess.
 *   <li>Handles audio functionality and provides options to mute/unmute the sound.
 *   <li>Sends player input to an AI model for chat responses, and processes the returned messages.
 *   <li>Controls the game state, including whether the player has guessed, if the timer has
 *       expired, and if the game has ended.
 * </ul>
 */
public class GuessController implements TimerListener {

  @FXML private Label timerLabel;
  @FXML private SharedTimer sharedTimer;
  @FXML private ImageView incorrect;
  @FXML private ImageView correct;
  @FXML private ImageView femaleImageGlow;
  @FXML private ImageView maleImageGlow;
  @FXML private ImageView managerImageGlow;
  @FXML private ImageView femaleImage;
  @FXML private ImageView maleImage;
  @FXML private ImageView managerImage;
  @FXML private ImageView audioImage;
  @FXML private ImageView timesUp;

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;
  @FXML private Text txtChooseFirst;
  @FXML private ChatController chatController;

  @FXML private ProgressIndicator progressIndicator;

  private MediaPlayer mediaPlayer;
  private ChatCompletionRequest chatCompletionRequest;
  private boolean isGuessed = false;
  private boolean gameEnded = false;
  private boolean isAudioPlaying = false;
  private String currentGuess;
  private Player mp3Player;

  private final Image soundOnImage = new Image(getClass().getResourceAsStream("/images/audio.png"));
  private final Image soundOffImage =
      new Image(getClass().getResourceAsStream("/images/muteaudio.png"));

  /**
   * This method is triggered when the timer finishes. It checks if the player has made a guess and
   * either sends the message or disables further actions when the game ends.
   */
  @Override
  public void onTimerFinished() {
    // Reset timer to sixty seconds
    if (sharedTimer.getHasReset() == false) {
      sharedTimer.resetToSixtySeconds();
    } else if (sharedTimer.getHasReset() == true) {
      // Check if message if empty
      String message = txtInput.getText().trim();
      // If the player has chosen a guess, automatically send the message
      if (currentGuess != null && !message.isEmpty()) {
        try {
          onSendMessage(null);
        } catch (ApiProxyException | IOException e) {
          e.printStackTrace();
        }
      } else {
        timesUp.setVisible(true);
        btnSend.setDisable(true);
        // Set the game as ended
        gameEnded = true;
      }
    }
  }

  /**
   * Initializes the controller, setting up the initial state of the game, registering event
   * handlers, and starting the shared timer.
   */
  @FXML
  public void initialize() {
    // Reset the guess condition
    GuessCondition.INSTANCE.setManagerClicked(false);
    GuessCondition.INSTANCE.setThiefClicked(false);
    GuessCondition.INSTANCE.setFemaleCustomerClicked(false);

    updateMuteImage();

    // Start the timer
    managerImage.setOnMouseClicked(this::handleGuessManager);
    femaleImage.setOnMouseClicked(this::handleGuessFemale);
    maleImage.setOnMouseClicked(this::handleGuessMale);
    audioImage.setOnMouseClicked(this::handleToggleSpeech);
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();

    // Set the visibliity of the images
    txtChooseFirst.setVisible(false);

    if (!GuessCondition.INSTANCE.isConditionMet()) {
      endGame(true);
      return;
    }

    playAudio("GuessStarted");
  }

  /** Stops the shared timer when needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop();
    }
  }

  /**
   * Handles the key pressed event, currently not used.
   *
   * @param event the key event that was pressed
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {}

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) throws ApiProxyException, IOException {
    if (event.getCode() == KeyCode.ENTER) {
      onSendMessage(null);
    }
  }

  /**
   * Toggles the mute state for the game's audio and updates the UI accordingly.
   *
   * @param event the mouse event for clicking the audio toggle button
   */
  @FXML
  private void handleToggleSpeech(MouseEvent event) {
    boolean currentStatus = AudioStatus.INSTANCE.isMuted();
    AudioStatus.INSTANCE.setMuted(!currentStatus);
    updateMuteImage(); // Update the image in ImageView according to the speech status
    toggleAudioMute(); // Mute or unmute the playing audio
  }

  /** Update the image in ImageView according to the speech status */
  private void updateMuteImage() {
    if (!AudioStatus.INSTANCE.isMuted()) {
      audioImage.setImage(soundOnImage); // Show Speaker Icon
    } else {
      audioImage.setImage(soundOffImage); // Show Mute icon
    }
  }

  private void toggleAudioMute() {
    if (mediaPlayer != null) {
      mediaPlayer.setMute(AudioStatus.INSTANCE.isMuted()); // Mute/unmute the MediaPlayer
    }
    if (mp3Player != null && AudioStatus.INSTANCE.isMuted()) {
      mp3Player.close(); // Stop the mp3Player immediately
    }
  }

  @FXML
  /**
   * Handles the cick event on the male icon.
   *
   * @param event
   * @return void
   */
  private void handleGuessMale(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "male"; // Store the guess
      maleImageGlow.setVisible(true);
      managerImageGlow.setVisible(false);
      femaleImageGlow.setVisible(false);
      GuessCondition.INSTANCE.setThiefClicked(true);
    } else if (!GuessCondition.INSTANCE.isConditionMet()) {
      playAudio("ConditionNotMetGuessing");
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  /**
   * Handles the click event on the manager icon.
   *
   * @param event
   * @return void
   */
  private void handleGuessManager(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "manager"; // Store the guess
      maleImageGlow.setVisible(false);
      managerImageGlow.setVisible(true);
      femaleImageGlow.setVisible(false);
      GuessCondition.INSTANCE.setManagerClicked(true);
    } else if (!GuessCondition.INSTANCE.isConditionMet()) {
      playAudio("ConditionNotMetGuessing");
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void handleGuessFemale(MouseEvent event) {
    if (!isGuessed) {
      currentGuess = "female"; // Store the guess
      maleImageGlow.setVisible(false);
      managerImageGlow.setVisible(false);
      femaleImageGlow.setVisible(true);
      GuessCondition.INSTANCE.setFemaleCustomerClicked(true);
    } else if (!GuessCondition.INSTANCE.isConditionMet()) {
      playAudio("ConditionNotMetGuessing");
    } else {
      playAudio("Guessed");
    }
  }

  @FXML
  private void onPlayAgain(ActionEvent event) throws ApiProxyException, IOException {
    GuessCondition.INSTANCE.setComputerClicked(false);
    GuessCondition.INSTANCE.setShoeprintClicked(false);
    GuessCondition.INSTANCE.setPaperClicked(false);
    GuessCondition.INSTANCE.setManagerClicked(false);
    GuessCondition.INSTANCE.setThiefClicked(false);
    GuessCondition.INSTANCE.setFemaleCustomerClicked(false);
    gameEnded = false;

    RoomController.context.setState(RoomController.context.getGameStartedState());
    RoomController.isFirstTimeInit = true;
    // Reset and start the timer
    sharedTimer.resetHasReset();
    sharedTimer.resetToFiveMins(); // Reset the timer to 10 seconds
    App.setRoot("CrimeScene");
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    return PromptEngineering.getPrompt("prompts/AI.txt", map);
  }

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setProfession() {
    try {
      // Read the API proxy configuration
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      // if time runs out don't run the GPT model
      if (sharedTimer.hasTimerEnded()) {
        return;
      }
      runGptAsync(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      // Handle the exception
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
  /**
   * Runs the GPT model asynchronously with a given chat message using CompletableFuture.
   *
   * @param msg the chat message to process
   */
  private void runGptAsync(ChatMessage msg) {
    // Set progress indicator to visible
    progressIndicator.setVisible(true);
    // Add the message to the request
    chatCompletionRequest.addMessage(msg);

    // Run the request asynchronously to avoid blocking the UI thread
    CompletableFuture.supplyAsync(
            () -> {
              try {
                // Execute the request and get the result
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                return chatCompletionResult.getChoices().iterator().next();
              } catch (ApiProxyException e) {
                e.printStackTrace();
                return null;
              }
            })
        .thenAccept(
            choice -> {
              if (choice != null) {
                // Append chat message on the UI thread
                Platform.runLater(
                    () -> {
                      chatCompletionRequest.addMessage(choice.getChatMessage());
                      appendChatMessage(choice.getChatMessage());
                      progressIndicator.setVisible(false); // Hide the loading indicator
                    });
              }
            });
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
    // Check if the player has guessed
    if (gameEnded) {
      return;
    } else if (GuessCondition.INSTANCE.isFemaleCustomerClicked()
        || GuessCondition.INSTANCE.isManagerClicked()
        || GuessCondition.INSTANCE.isThiefClicked()) {
      // Stop the timer
      stopTimer();
      String message = txtInput.getText().trim();
      txtChooseFirst.setVisible(false);
      if (message.isEmpty()) {
        return;
      }
      // Clear the input field
      txtInput.clear();
      ChatMessage msg = new ChatMessage("user", message);
      appendChatMessage(msg);
      runGptAsync(msg);
      // Check if the player has guessed
      if (currentGuess != null) {
        switch (currentGuess) {
          case "manager":
            correct.setVisible(true);
            break;
          case "female":
          case "male":
            incorrect.setVisible(true);
            break;
        }
        isGuessed = true; // Mark that the player has guessed
      }
    } else {
      txtChooseFirst.setVisible(true);
    }
  }

  /**
   * Plays the audio file.
   *
   * @param mp3FilePath
   */
  private void playAudio(String fileName) {
    if (AudioStatus.INSTANCE.isMuted() || isAudioPlaying) {
      return;
    }

    // Play the audio file
    try {
      // Create a new Media object with the file path
      Media sound =
          new Media(new File("src/main/resources/sounds/" + fileName + ".mp3").toURI().toString());
      mediaPlayer = new MediaPlayer(sound);
      mediaPlayer.play();
      isAudioPlaying = true;

      // Handle the audio finishing
      mediaPlayer.setOnEndOfMedia(
          () -> {
            isAudioPlaying = false;
          });
    } catch (Exception e) {
      e.printStackTrace();
      isAudioPlaying = false;
    }
  }

  private void endGame(boolean won) {
    // Stop timer if it is running and reset it to 60 seconds
    stopTimer();

    // Mark the game as finished
    gameEnded = true;
    isGuessed = true;

    timesUp.setVisible(true); // Display failure message

    // Disable the input box and send button to prevent subsequent interactions
    txtInput.setDisable(true);
    btnSend.setDisable(true);
  }
}
