package entities;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class Human {
    private int x, y;
    private int speed = 3;
    private boolean isMoving = false;
    private String currentTask = "idle";
    private int taskProgress = 0;
    private int currentRoom = 0;
    private BufferedImage sprite;


    public Human(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("res/sprites/human/human.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (isMoving) {
            x += speed;
            if (x > 800) {
                x = 0;
                currentRoom = (currentRoom + 1) % 3;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(sprite, x, y, null);
    }

    public void moveToNextRoom() {
        isMoving = true;
        currentTask = "Moving to next room...";
    }

    public void interactWithCurrentObject() {
        if (currentTask.equals("idle")) {
            currentTask = "Working on task...";
            taskProgress = 0;
        }
        taskProgress += 10;
        if (taskProgress >= 100) {
            currentTask = "idle";
            taskProgress = 0;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }
}