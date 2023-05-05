import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Taskbar UI component with three buttons.
 */
class TaskBar extends JPanel {
  private JButton deleteQuestionButton;
  private JButton newQuestionButton;
  private JButton clearAllButton;

  private boolean recording = false;

  Color lightGray = new Color(217, 217, 217);
  Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

  TaskBar() {
    this.setLayout(new GridLayout(1, 3));
    this.setPreferredSize(new Dimension(640, 80));
    this.setBackground(lightGray);

    deleteQuestionButton = new JButton("Delete Question");
    deleteQuestionButton.setFont(font);
    add(deleteQuestionButton, BorderLayout.WEST);

    newQuestionButton = new JButton("New Question");
    newQuestionButton.setFont(font);
    add(newQuestionButton, BorderLayout.CENTER);

    newQuestionButton.addActionListener((ActionEvent e) -> {
      recording = !recording;
      newQuestionButton.setText(recording ? "Stop Recording" : "New Question");
    });

    clearAllButton = new JButton("Clear All");
    clearAllButton.setFont(font);
    add(clearAllButton, BorderLayout.EAST);
  }
}
