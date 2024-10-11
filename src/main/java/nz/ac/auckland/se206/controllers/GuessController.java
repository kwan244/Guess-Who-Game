package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GptHelper;
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
public class GuessController extends BaseController implements TimerListener {

  @FXML private Label timerLabel; // Label to display the timer.
  @FXML private SharedTimer sharedTimer; // Shared timer instance.
  @FXML private ImageView incorrect; // Image view indicating incorrect guess.
  @FXML private ImageView correct; // Image view indicating correct guess.
  @FXML private ImageView femaleImageGlow; // Image glow for the female option.
  @FXML private ImageView maleImageGlow; // Image glow for the male option.
  @FXML private ImageView managerImageGlow; // Image glow for the manager option.
  @FXML private ImageView femaleImage; // Image view for the female option.
  @FXML private ImageView maleImage; // Image view for the male option.
  @FXML private ImageView managerImage; // Image view for the manager option.
  @FXML private ImageView audioImage; // Image view for the audio toggle button.
  @FXML private ImageView timesUp; // Image indicating that time is up.

  @FXML private TextArea txtaChat; // Text area to display chat messages.
  @FXML private TextField txtInput; // Text field for user input.
  @FXML private Button btnSend; // Button to send user input.
  @FXML private Text txtChooseFirst; // Text message displayed if no option is chosen.
  @FXML private ChatController chatController; // Controller for chat functionality.

  @FXML private ProgressIndicator progressIndicator; // Progress indicator for API responses.

  private ChatCompletionRequest chatCompletionRequest; // Request object for chat completion.
  private boolean isGuessed = false; // Boolean to track if a guess has been made.
  private boolean gameEnded = false; // Boolean to track if the game has ended.
  private String currentGuess; // Stores the current user guess.

  /**
   * This method is triggered when the timer finishes. It checks if the player has made a guess and
   * either sends the message or disables further actions when the game ends.
   */
  @Override
  public void onTimerFinished() {
    if (sharedTimer.getHasReset() == false) {
      sharedTimer.resetToSixtySeconds(); // Reset timer to 60 seconds.
    } else if (sharedTimer.getHasReset() == true) {
      String message = txtInput.getText().trim(); // Retrieve the current input text.
      if (currentGuess != null && !message.isEmpty()) {
        try {
          onSendMessage(null); // Send the message if a guess was made.
        } catch (ApiProxyException | IOException e) {
          e.printStackTrace();
        }
      } else {
        timesUp.setVisible(true); // Display time-up message.
        btnSend.setDisable(true); // Disable send button.
        gameEnded = true; // Set the game as ended.
      }
    }
  }

  /**
   * Initializes the controller, setting up the initial state of the game, registering event
   * handlers, and starting the shared timer.
   */
  @FXML
  public void initialize() {
    // Initialize guess conditions.
    GuessCondition.INSTANCE.setManagerClicked(false);
    GuessCondition.INSTANCE.setThiefClicked(false);
    GuessCondition.INSTANCE.setFemaleCustomerClicked(false);

    super.initialize();

    // Set up event handlers for guess selections.
    managerImage.setOnMouseClicked(this::handleGuessManager);
    femaleImage.setOnMouseClicked(this::handleGuessFemale);
    maleImage.setOnMouseClicked(this::handleGuessMale);
    audioImage.setOnMouseClicked(this::handleToggleSpeech);

    // Initialize shared timer and start it.
    sharedTimer = SharedTimer.getInstance();
    sharedTimer.setTimerLabel(timerLabel);
    sharedTimer.setTimerListener(this);
    sharedTimer.start();

    txtChooseFirst.setVisible(false); // Hide "Choose First" text initially.

    // End game if conditions are not met.
    if (!GuessCondition.INSTANCE.isConditionMet()) {
      endGame(true);
      return;
    }

    playAudio("GuessStarted"); // Play the audio cue for starting the game.
  }

  /** Stops the shared timer when needed. */
  public void stopTimer() {
    if (sharedTimer != null) {
      sharedTimer.stop(); // Stop the shared timer if it's active.
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
      onSendMessage(null); // Send message if the Enter key is released.
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
    GuessCondition.INSTANCE.setWireCompleted(false);
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
      // Prepare the initial system message
      ChatMessage systemMessage = new ChatMessage("system", getSystemPrompt());

      // Call the new method in GptHelper
      GptHelper.runInitialGptMessage(
          systemMessage, chatCompletionRequest, progressIndicator, this::appendChatMessage);
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
      // Use the helper to run GPT asynchronously
      GptHelper.runGptAsync(
          msg,
          chatCompletionRequest,
          progressIndicator,
          this::appendChatMessage // Provide the method to append the chat message
          );
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
