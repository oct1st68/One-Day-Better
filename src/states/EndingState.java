package states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EndingState extends State {
    private BufferedImage background;
    private long startTime;
    private static final long DURATION = 5000; // 5 seconds
    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;

    public EndingState(GameStateManager gsm) {
        super(gsm);
        try {
            background = ImageIO.read(new File("res/sprites/GameState/end.png"));
        } catch (IOException e) {
            System.err.println("Error loading ending background: " + e.getMessage());
            background = null;
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - startTime >= DURATION) {
            // Transition back to menu after the ending
            gsm.setState(GameState.MENU);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Draw full-screen background
        if (background != null) {
            g2d.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        }
    }
} 