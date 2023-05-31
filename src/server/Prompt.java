
public class Prompt implements IPrompt{
    String prompt;

    Prompt() {
        this.prompt = "";
    }

    public String updatePrompt(String newPrompt) {
        this.prompt = newPrompt;
        return this.prompt;
    }

    public String getPrompt() {
        return this.prompt;
    }
}
