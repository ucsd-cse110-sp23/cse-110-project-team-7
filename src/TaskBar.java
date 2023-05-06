import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Taskbar UI component with three buttons.
 */
class TaskBar extends JPanel {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 80;

  JButton deleteQuestionButton;
  JButton newQuestionButton;
  JButton clearAllButton;

  Color lightGray = new Color(217, 217, 217);
  Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

  TaskBar() {
    setLayout(new GridLayout(1, 3));
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    setBackground(lightGray);

    deleteQuestionButton = new JButton("Delete Question");
    deleteQuestionButton.setFont(font);
    add(deleteQuestionButton, BorderLayout.WEST);

    newQuestionButton = new JButton("New Question");
    newQuestionButton.setFont(font);
    add(newQuestionButton, BorderLayout.CENTER);

    clearAllButton = new JButton("Clear All");
    clearAllButton.setFont(font);
    add(clearAllButton, BorderLayout.EAST);
  }
}
