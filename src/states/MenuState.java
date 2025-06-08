package states;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import input.MouseInput;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MenuState extends State {
    private Font mainFont;
    private Font promptFont;
    private MouseInput mouseInput;
    private boolean started = false;
    private Clip bgMusic;
    private BufferedImage startImage;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        mainFont = new Font("Arial", Font.PLAIN, 36);
        promptFont = new Font("Montserrat", Font.PLAIN, 24);
        mouseInput = gsm.getPanel().getMouseInput();
        loadStartImage();
        playMusic();
    }

    private void loadStartImage() {
        try {
            startImage = ImageIO.read(new File("res/sprites/GameState/start.png"));
        } catch (IOException e) {
            System.err.println("Error loading start image: " + e.getMessage());
            startImage = null;
        }
    }

    public void reset() {
        started = false;
        playMusic();
    }

    public void playMusic() {
        stopMusic();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/sounds/bgmusicmenu.wav"));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioInputStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    @Override
    public void update() {
        if (mouseInput.isMousePressed() && !started) {
            started = true;
            stopMusic();
            gsm.setState(GameState.PLAYING);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Draw start image as background if available
        if (startImage != null) {
            // Scale the image to fit the screen (1280x720)
            g2d.drawImage(startImage, 0, 0, 1280, 720, null);
        }
        
        // Draw prompt text on top
        g2d.setFont(promptFont);
        g2d.setColor(new Color(220, 220, 240));
        g2d.drawString("Click Anywhere to Play", 500, 650);
    }
} 