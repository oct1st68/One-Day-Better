package states;

import java.awt.Graphics2D;
import java.awt.Color;
import entities.Human;
import entities.Cat;
import input.KeyboardInput;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class PlayState extends State {
    private Human human;
    private Cat cat;
    private KeyboardInput keyboardInput;
    private Clip bgMusic;
    private int cameraX = 0;
    private final int SCREEN_WIDTH = 1280;
    private final int WORLD_WIDTH = 2000; // Example world width

    public PlayState(GameStateManager gsm, KeyboardInput keyboardInput) {
        super(gsm);
        human = new Human(100, 300);
        cat = new Cat(50, 300);
        this.keyboardInput = keyboardInput;
        playMusic();
    }

    private void playMusic() {
        if (isMusicPlaying()) return;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/sounds/bgmusicmenu.wav"));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioInputStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMusicPlaying() {
        return bgMusic != null && bgMusic.isRunning();
    }

    public void resetMusic() {
        playMusic();
    }

    public void pauseMusic() {
        stopMusic();
    }

    private void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    @Override
    public void update() {
        // Update human
        human.update();
        
        // Update cat based on keyboard input
        if (keyboardInput.leftPressed) {
            cat.moveLeft();
        }
        if (keyboardInput.rightPressed) {
            cat.moveRight();
        }
        if (keyboardInput.upPressed) {
            cat.jump();
        }
        if (keyboardInput.interactPressed) {
            cat.paw(human);
        }
        if (keyboardInput.meowPressed) {
            cat.meow(human);
        }
        if (keyboardInput.headbuttPressed) {
            cat.headbutt(human);
        }
        cat.update();

        // Camera follows cat
        int catCenterX = cat.getX() + 32; // Assuming cat sprite is 64px wide
        cameraX = catCenterX - SCREEN_WIDTH / 2;
        if (cameraX < 0) cameraX = 0;
        if (cameraX > WORLD_WIDTH - SCREEN_WIDTH) cameraX = WORLD_WIDTH - SCREEN_WIDTH;

        // Check for pause
        if (keyboardInput.escapePressed) {
            gsm.setState(GameState.PAUSED);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, SCREEN_WIDTH, 720);
        
        // Draw entities with camera offset
        human.draw(g2d, -cameraX, 0);
        cat.draw(g2d, -cameraX, 0);
    }

    public KeyboardInput getKeyboardInput() {
        return keyboardInput;
    }
} 