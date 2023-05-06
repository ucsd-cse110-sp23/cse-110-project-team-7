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
  private static final int WIDTH = 100;
  private static final int HEIGHT = 100;

  private Box vbox;
  private JScrollPane scroller;
  private JLabel questionLabel;
  private JLabel responseLabel;

  Color lightBlue = new Color(59, 59, 217);
  Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

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

  void add(HistoryItem item) {
    vbox.add(new JButton(item.question), 0);
  }
}
