import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Main {
    public static Starship starship;

    public static void main(String[]args)
            throws InterruptedException, UnsupportedAudioFileException, LineUnavailableException, IOException {
        starship = new Starship();
    }
}
