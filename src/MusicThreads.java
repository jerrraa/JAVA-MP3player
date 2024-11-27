import java.io.*;

import javazoom.jl.decoder.*;
import javazoom.jl.player.Player;

public class MusicThreads extends Thread {
    private final String filename;
    private Player player;
    private int slider = 0;
    private int timer = 0;
    public MusicThreads(String filename) {
        this.filename = filename;
    }
    public void run(){
        try (FileInputStream inputfile = new FileInputStream(filename)) {
            player = new Player(inputfile);

            Thread sliderThread = new Thread(() -> {
                int i = 0;
                while (i < timer) {
                    try {
                        Thread.sleep(1000);
                        slider++; // IF it runs, it'll update by 1 sec and update the slider

                        i++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            sliderThread.start();
            player.play();

        } catch (IOException ignored) {
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void stoprunningfile() {
        if (player != null) {
            player.close();
            timer = 0;
        }
        interrupt();
    }
    public void setslidertimer(int time) {
        timer = time;
    }
    public void setsliderposition(int slide) {
        slider = slide;
    }
    public int getsliderposition() {
        return slider;
    }

}
