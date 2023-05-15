import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Text region UI component that displays a question and its response.
 */
class QuestionAndResponse extends JPanel {
  private static final int WIDTH = 200;
  private static final int HEIGHT = 60;

  private JTextArea questionLabel;
  private JTextArea responseLabel;

  Color darkGray = new Color(59, 59, 59);
  Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

  /**
   * Set common properties between text areas, to reduce
   *   duplicated code.
   */
  private void setTextProps(JTextArea area) {
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    area.setFont(font);
    area.setBackground(darkGray);
    area.setForeground(Color.WHITE);
  }

  /**
   * Instantiate a grid layout with two labels in opposite
   *   corners from each other.
   */
  QuestionAndResponse() {
    setLayout(new GridLayout(2, 2));
    setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    setBackground(darkGray);

    questionArea = new JTextArea();
    questionArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    setTextProps(questionArea);
    add(questionArea, BorderLayout.LINE_END);

    responseArea = new JTextArea();
    setTextProps(responseArea);
    add(responseArea, BorderLayout.LINE_START);
  }

  /**
   * Given a question/response pair, update the UI to
   *   match.
   */
  void show(HistoryItem item) {
    questionArea.setText(item.question);
    responseArea.setText(item.response);
  }
}
