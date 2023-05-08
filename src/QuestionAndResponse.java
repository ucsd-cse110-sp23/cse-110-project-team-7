import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Text region UI component that displays a question and its response.
 */
class QuestionAndResponse extends JPanel {
  private JLabel questionLabel;
  private JLabel responseLabel;

  Color darkGray = new Color(59, 59, 59);
  Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

  /**
   * Instantiate a grid layout with two labels in opposite
   *   corners from each other.
   */
  QuestionAndResponse() {
    setLayout(new GridLayout(2, 2));
    setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    setBackground(darkGray);

    questionLabel = new JLabel("Question here...");
    questionLabel.setPreferredSize(new Dimension(200, 60));
    questionLabel.setFont(font);
    questionLabel.setHorizontalAlignment(JLabel.RIGHT);
    questionLabel.setForeground(Color.WHITE);
    add(questionLabel, BorderLayout.LINE_END);

    responseLabel = new JLabel("Response here...");
    responseLabel.setPreferredSize(new Dimension(200, 60));
    responseLabel.setFont(font);
    responseLabel.setHorizontalAlignment(JLabel.LEFT);
    responseLabel.setForeground(Color.WHITE);
    add(responseLabel, BorderLayout.LINE_START);
  }

  /**
   * Given a question/response pair, update the UI to
   *   match.
   */
  void show(String question, String response) {
    questionLabel.setText(question);
    responseLabel.setText(response);
  }
}
