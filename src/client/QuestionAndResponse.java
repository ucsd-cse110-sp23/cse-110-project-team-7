import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Text region UI component that displays a question and its response.
 */
class QuestionAndResponse extends JPanel {
  private static final int WIDTH = 200;
  private static final int HEIGHT = 60;

  private JTextPane questionArea;
  private JTextPane responseArea;

  Color darkGray = new Color(59, 59, 59);

  /**
   * Set common properties between text areas, to reduce
   *   duplicated code.
   */
  private void setTextProps(JTextPane area) {
    area.setEditable(false);
    area.setPreferredSize(new Dimension(WIDTH, HEIGHT));
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

    // Special alignment rules for question area
    SimpleAttributeSet attribs = new SimpleAttributeSet();
    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
    StyleConstants.setForeground(attribs, Color.WHITE);

    questionArea = new JTextPane();
    questionArea.setParagraphAttributes(attribs, true);
    setTextProps(questionArea);
    add(questionArea, BorderLayout.LINE_END);

    responseArea = new JTextPane();
    setTextProps(responseArea);
    add(responseArea, BorderLayout.LINE_START);
  }

  /**
   * Given a question/response pair, update the UI to
   *   match.
   */
  void show(HistoryItem item) {
    if (item != null) {
      questionArea.setText(item.question);
      responseArea.setText(item.response);
    } else {
      questionArea.setText("");
      responseArea.setText("");
    }
  }
}
