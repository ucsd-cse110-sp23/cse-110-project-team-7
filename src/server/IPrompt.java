/**
 * A common interface for handling different commands
 *   or prompts.
 */
interface IPrompt {
    String prompt = "";

    String updatePrompt(String newPrompt);

    String getPrompt();
}
