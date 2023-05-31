import java.io.File;

/**
 * Interface for Whisper speech-to-text
 *   functionality.
 */
interface IWhisper {
  public String speechToText(File file);
}
