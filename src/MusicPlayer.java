import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

import java.util.ArrayList;



public class MusicPlayer implements ActionListener {

    private JButton selectMP3, start, stop, forward, backward;
    private JLabel songname, songduration;
    private JPanel buttonTile, displayTile;
    private JSlider songdurationslider;
    private final ArrayList<String> songplaylist = new ArrayList<>();

    private int currentSongIndex = -1;
    private MusicThreads musicThreads;
    private Timer musictimer;
    private int grabseconds;

    public void setupGUI() {
        initializeScreen();
        setupButtonsLayout();
        setupDisplayLayout();

        JFrame frame = new JFrame("MP3 Player");
        frame.setLayout(new BorderLayout());
        frame.add(buttonTile, BorderLayout.SOUTH);
        frame.add(displayTile, BorderLayout.NORTH);

        frame.setResizable(false);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializeScreen() {
        //basic setup of the screen
        selectMP3 = new JButton("Select MP3 File(s)!");
        selectMP3.setBackground(Color.WHITE);
        songname = new JLabel("...", SwingConstants.CENTER);
        songdurationslider = new JSlider(0,0,0);
        songduration = new JLabel("...", SwingConstants.CENTER);

        start = new JButton("PLAY");
        stop = new JButton("STOP");
        forward = new JButton(">>");
        backward = new JButton("<<");

        // a fun experiment to mess around with and looks nicer
        JButton[] buttonsARRAY = {start, stop, forward, backward};
        Border buttonBorder = BorderFactory.createLineBorder(Color.BLACK);
        for (JButton button : buttonsARRAY) {
            button.setBackground(Color.WHITE);
            button.setBorder(buttonBorder);
        }

        forward.setEnabled(false);
        backward.setEnabled(false);

        selectMP3.addActionListener(this);
        start.addActionListener(this);
        stop.addActionListener(this);
        forward.addActionListener(this);
        backward.addActionListener(this);

        songdurationslider.addChangeListener(e -> {
            if (!songdurationslider.getValueIsAdjusting() && musicThreads != null) {

                int sliderValue = songdurationslider.getValue();
                musicThreads.setsliderposition(sliderValue);

            }
        });
    }
    private void setupButtonsLayout() {
        buttonTile = new JPanel(new GridLayout(1, 4));
        buttonTile.setBackground(Color.LIGHT_GRAY);
        buttonTile.add(backward);
        buttonTile.add(start);
        buttonTile.add(stop);
        buttonTile.add(forward);
    }

    private void setupDisplayLayout() {
        // tbh adding a row/col fixed my bug where the jlabel wasn't showing up
        displayTile = new JPanel(new GridLayout(4, 1));
        displayTile.setBackground(Color.LIGHT_GRAY);
        displayTile.add(selectMP3);
        displayTile.add(songname);
        displayTile.add(songduration);
        displayTile.add(songdurationslider);
    }

// this functions reads the mp3 file and gets the duration of the song. [this was in selectmp3 action but i moved it to a function-
// - instead due to bugs.]
    private void changeSongDurationText() {
    try (FileInputStream fileInputStream = new FileInputStream(songplaylist.get(currentSongIndex)) ) {
        Bitstream bitstream = new Bitstream(fileInputStream);
        int frames = 0;
        double frameduration = 0;

        Header header;
        // if header is not null it'll read the frame and add to the frames var.
        while ((header = bitstream.readFrame()) != null) {
            frameduration = header.ms_per_frame();
            frames++;
            bitstream.closeFrame();
        }

        double summedupframes = frames * frameduration;
        // multiply both vars by 1000, convert to be used by ints below.
        int totalsumofsecs = (int) (summedupframes / 1000);

        // a cheap way of grabbing the duration timer, but it works..;)
        grabseconds = totalsumofsecs;

        int minutes = totalsumofsecs / 60;
        int seconds = totalsumofsecs % 60;
        String durationString = String.format("%02d:%02d", minutes, seconds);

        // this scales the slider to the total amount of secs in mp3 file
        songdurationslider.setMaximum(totalsumofsecs);
        songdurationslider.setValue(0);
        // update the text to song duration.
        songduration.setText(durationString);

    } catch (BitstreamException | IOException ex) {
        ex.printStackTrace();
        songduration.setText("error grabbing duration");
    }
}

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == selectMP3) {
            JFileChooser fileChooser = new JFileChooser("");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // each time this is run, it'll clear and add files to the arraylist depending on the files selected
                File[] selectedFiles = fileChooser.getSelectedFiles();
                if (selectedFiles.length > 0) {
                    songplaylist.clear();
                    for (File file : selectedFiles) {
                        songplaylist.add(file.getAbsolutePath());
                       // System.out.println(file);
                    }
                    currentSongIndex = 0;
                    String filesongname = new File(songplaylist.get(currentSongIndex)).getName();



                    songname.setText(filesongname);

                    changeSongDurationText();

                    start.setEnabled(true);
                    forward.setEnabled(songplaylist.size() > 1);
                    backward.setEnabled(false);
                }
            }
        }
        if (e.getSource() == start) {
            if (currentSongIndex >= 0 && currentSongIndex < songplaylist.size()) {

                if (musicThreads != null) {
                    musicThreads.stoprunningfile();
                    musicThreads = null;
                }
                musicThreads = new MusicThreads(songplaylist.get(currentSongIndex));

                musictimer = new Timer(1000, e1 -> {
                    if (musicThreads != null) {
                        int songprogress = musicThreads.getsliderposition();
                     //   System.out.println(songprogress);
                        songdurationslider.setValue(songprogress);
                    }
                });
                musictimer.start();
                //it'll start the song and the slider timer and start the tread/song.
                musicThreads.setslidertimer(grabseconds);
                musicThreads.start();
                start.setEnabled(false);
                stop.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a song!");
            }
        }
        if (e.getSource() == stop) {
            // check if not != null and if it is, stop all threads/songs
            if (musicThreads != null) {
                musicThreads.stoprunningfile();
                musictimer.stop();
                start.setEnabled(true);
                stop.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "You don't have a song selected!");
            }
        }
        if (e.getSource() == forward) {
            // if the current song index is less than the size of the arraylist, it'll go to the next song.
            if (currentSongIndex < songplaylist.size() - 1) {
                currentSongIndex++;
                String playingsong = new File(songplaylist.get(currentSongIndex)).getName();
                //   System.out.println(playingsong);
                songname.setText(playingsong);
                //this functions updates the song duration text. and updates if goes through the arraylist.
                changeSongDurationText();
                forward.setEnabled(currentSongIndex < songplaylist.size() - 1);
                backward.setEnabled(true);
            }
        }
        if (e.getSource() == backward) {
            // does the same as before except the oppoosite
            if (currentSongIndex > 0) {
                currentSongIndex--;
                String playingsong = new File(songplaylist.get(currentSongIndex)).getName();
                songname.setText(playingsong);
                changeSongDurationText();
                backward.setEnabled(currentSongIndex > 0);
                forward.setEnabled(true);
            }
        }
    }
}

