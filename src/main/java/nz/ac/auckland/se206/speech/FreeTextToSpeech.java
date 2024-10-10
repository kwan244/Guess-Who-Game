package nz.ac.auckland.se206.speech;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class FreeTextToSpeech {
  private static boolean enabled = true;
  private static final Synthesizer synthesizer;

  static {
    try {
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

      synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(java.util.Locale.ENGLISH));
      synthesizer.allocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  public static void speak(final String text) {
    if (!enabled) {
      return;
    }

    new Thread(
            () -> {
              try {
                synthesizer.resume();
                synthesizer.speakPlainText(text, null);
                synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
              } catch (final AudioException | InterruptedException e) {
                throw new TextToSpeechException(e.getMessage());
              }
            })
        .start();
  }

  public static void toggleEnabled() {
    enabled = !enabled;
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static void deallocateSynthesizer() {
    try {
      if (synthesizer != null && synthesizer.testEngineState(Synthesizer.ALLOCATED)) {
        synthesizer.deallocate();
      }
    } catch (final EngineException e) {
      throw new TextToSpeechException("Error deallocating the synthesizer: " + e.getMessage());
    }
  }

  static class TextToSpeechException extends RuntimeException {
    public TextToSpeechException(final String message) {
      super(message);
    }
  }
}
