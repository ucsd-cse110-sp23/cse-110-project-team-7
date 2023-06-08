import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Vertical toolbar showing past questions.
 */
class HistoryBar extends JPanel {
  private static final int WIDTH = 200;
  private static final int HEIGHT = 100;

  private static final int BTN_WIDTH = WIDTH - 20;
  private static final int BTN_HEIGHT = 40;

  private Box vbox;
  private JScrollPane scroller;
  private JLabel questionLabel;
  private JLabel responseLabel;

  Color lightBlue = new Color(59, 59, 217);
  Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

  /**
   * A vertically-scrolling box that stores the user's
   *   question/response history as buttons.
   */
  HistoryBar() {
    setLayout(new BorderLayout());

    vbox = Box.createVerticalBox();
    scroller = new JScrollPane(
      vbox,
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    scroller.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    add(scroller, BorderLayout.CENTER);
  }

  /**
   * Add the given question/response pair to the screen.
   */
  JButton add(HistoryItem item) {
    String pre = "Question: ";
    switch (item.type) {
      case "email":
        pre = "Create: ";
        break;
      case "send":
        pre = "Send: ";
        break;
    }
    JButton out = new JButton(pre + item.question);
    out.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    vbox.add(out, 0);
    return out;
  }
}
