import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import javazoom.jl.player.Player;
import javax.swing.*;
import javax.swing.border.Border;


public class MusicPlayer implements ActionListener {

    private JButton selectMP3, start, stop, forward, backward;
    private JLabel songname;
    private JPanel buttonTile, displayTile;

    public void setupGUI() {
        initializeComponents();
        setupButtonsLayout();
        setupDisplayLayout();

        JFrame frame = new JFrame("MP3 Player");
        frame.add(buttonTile, BorderLayout.SOUTH);
        frame.add(displayTile, BorderLayout.NORTH);
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setVisible(true);
    }




    private void initializeComponents() {
        selectMP3 = new JButton("Select MP3 File.");

        selectMP3.setBackground(Color.white);
        songname = new JLabel("I AM HERE");

        start = new JButton("PLAY");
        stop = new JButton("STOP");
        forward = new JButton(">>");
        backward = new JButton("<<");

        start.setBackground(Color.WHITE);
        stop.setBackground(Color.WHITE);
        forward.setBackground(Color.WHITE);
        backward.setBackground(Color.WHITE);

        //this looks better instead of copy and pasting 4 buttons doing setborder and setbackground 4 times.
        JButton[] buttonsARRAY = {start, stop, forward, backward};
        Border ButtonBorder = BorderFactory.createLineBorder(Color.BLACK);
        for (JButton button : buttonsARRAY) {
            button.setBackground(Color.WHITE);
            button.setBorder(ButtonBorder);
        }
        selectMP3.addActionListener(this);
        start.addActionListener(this);
        stop.addActionListener(this);
        forward.addActionListener(this);
        backward.addActionListener(this);
    }

    private void setupButtonsLayout() {
        buttonTile = new JPanel();
        buttonTile.setBackground(Color.LIGHT_GRAY);
        buttonTile.add(backward);
        buttonTile.add(start);
        buttonTile.add(stop);
        buttonTile.add(forward);
    }
    private void setupDisplayLayout() {
        displayTile = new JPanel();
        displayTile.setBackground(Color.LIGHT_GRAY);

        displayTile.add(selectMP3);
        displayTile.add(songname);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == selectMP3) {

    }



    }
}
