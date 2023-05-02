/**
 * SayItAssistant.java: Program entry point, constructs the UI.
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

class QuestionAndResponse extends JPanel {
  private JLabel questionLabel;
  private JLabel responseLabel;

  Color darkGray = new Color(59, 59, 59);

  QuestionAndResponse() {
    this.setLayout(new GridLayout(2, 2));
    this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    this.setBackground(darkGray);

    questionLabel = new JLabel("Question here...");
    questionLabel.setPreferredSize(new Dimension(200, 60));
    questionLabel.setFont(new Font("Sans-serif", Font.BOLD, 14));
    questionLabel.setHorizontalAlignment(JLabel.RIGHT);
    questionLabel.setForeground(Color.WHITE);
    add(questionLabel, BorderLayout.LINE_END);

    responseLabel = new JLabel("Response here...");
    responseLabel.setPreferredSize(new Dimension(200, 60));
    responseLabel.setFont(new Font("Sans-serif", Font.BOLD, 14));
    responseLabel.setHorizontalAlignment(JLabel.LEFT);
    responseLabel.setForeground(Color.WHITE);
    add(responseLabel, BorderLayout.LINE_START);
  }
}

class TaskBar extends JPanel {
  private JButton deleteQuestionButton;
  private JButton newQuestionButton;
  private JButton clearAllButton;

  private boolean recording = false;

  Color lightGray = new Color(217, 217, 217);

  TaskBar() {
    this.setLayout(new GridLayout(1, 3));
    this.setPreferredSize(new Dimension(640, 80));
    this.setBackground(lightGray);

    deleteQuestionButton = new JButton("Delete Question");
    deleteQuestionButton.setFont(new Font("Sans-serif", Font.BOLD, 10));
    add(deleteQuestionButton, BorderLayout.WEST);

    newQuestionButton = new JButton("New Question");
    newQuestionButton.setFont(new Font("Sans-serif", Font.BOLD, 10));
    add(newQuestionButton, BorderLayout.CENTER);

    newQuestionButton.addActionListener(
      (ActionEvent e) -> {
        recording = !recording;
        newQuestionButton.setText(recording ? "Stop Recording" : "New Question");
      }
    );

    clearAllButton = new JButton("Clear All");
    clearAllButton.setFont(new Font("Sans-serif", Font.BOLD, 10));
    add(clearAllButton, BorderLayout.EAST);
  }
}

class AppFrame extends JFrame {
  private QuestionAndResponse convo;
  private TaskBar taskbar;

  AppFrame() {
    this.setSize(640, 480);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    convo = new QuestionAndResponse();
    add(convo, BorderLayout.CENTER);

    taskbar = new TaskBar();
    add(taskbar, BorderLayout.SOUTH);
  }
}

public class SayItAssistant {
  public static void main(String args[]) {
    new AppFrame();
  }
}
