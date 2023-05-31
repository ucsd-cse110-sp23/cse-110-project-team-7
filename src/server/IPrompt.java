public interface IPrompt {
    String prompt = "";

    String updatePrompt(String newPrompt);

    String getPrompt();
}
