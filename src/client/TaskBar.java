import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Taskbar UI component with three buttons.
 */
class TaskBar extends JPanel {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 80;

  private static final int BTN_WIDTH = WIDTH / 2;
  private static final int BTN_HEIGHT = HEIGHT - 10;

  JButton startButton;

  Color lightGray = new Color(217, 217, 217);
  Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

  /**
   * Instantiate a new panel with 3 equally-spaced buttons.
   */
  TaskBar() {
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    setBackground(lightGray);

    startButton = new JButton("Start");
    startButton.setFont(font);
    startButton.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    add(startButton, BorderLayout.CENTER);
  }
}
