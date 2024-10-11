package nz.ac.auckland.se206;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;

/**
 * A helper class for interacting with the GPT model asynchronously.
 *
 * <p>This class provides methods to run GPT model requests and manage chat messages, including
 * displaying progress indicators while processing and appending chat messages to the user
 * interface. It uses CompletableFuture to handle asynchronous execution without blocking the UI
 * thread.
 */
public class GptHelper {

  /**
   * Callback interface that defines the behavior for appending chat messages to the user interface.
   * This can be implemented in various contexts, such as when new messages are received from a
   * server or a local user sends a message. By separating the logic for appending messages, the
   * interface allows for flexible implementations, such as updating the chat window in real-time.
   */
  public interface AppendChatCallback {
    /**
     * Appends a chat message to the UI.
     *
     * @param message the chat message to append
     */
    void appendMessage(ChatMessage message);
  }

  /**
   * Runs the GPT model asynchronously with a given chat message using CompletableFuture.
   *
   * @param msg the chat message to process
   * @param chatCompletionRequest the request object for chat completion
   * @param progressIndicator the progress indicator to show loading status
   * @param appendChatMessage a callback to append the chat message on the UI
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  public static void runGptAsync(
      ChatMessage msg,
      ChatCompletionRequest chatCompletionRequest,
      ProgressIndicator progressIndicator,
      AppendChatCallback appendChatMessage) {

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
                      appendChatMessage.appendMessage(choice.getChatMessage());
                      progressIndicator.setVisible(false); // Hide the loading indicator
                    });
              }
            });
  }

  /**
   * Runs the initial GPT message asynchronously.
   *
   * @param systemMessage the system message to send
   * @param chatCompletionRequest the request object for chat completion
   * @param progressIndicator the progress indicator to show loading status
   * @param appendChatMessage a callback to append the chat message on the UI
   */
  public static void runInitialGptMessage(
      ChatMessage systemMessage,
      ChatCompletionRequest chatCompletionRequest,
      ProgressIndicator progressIndicator,
      AppendChatCallback appendChatMessage) {

    // Use the helper to run GPT asynchronously
    runGptAsync(systemMessage, chatCompletionRequest, progressIndicator, appendChatMessage);
  }
}
