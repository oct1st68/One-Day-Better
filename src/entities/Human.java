package entities;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Human {
    private int x, y;
    private int speed = 3;
    private boolean isMoving = false;
    private boolean isFollowing = false;
    private String currentTask = "idle";
    private int taskProgress = 0;
    private int currentRoom = 0;
    private BufferedImage sprite;
    private Cat targetCat;
    private boolean isFacingRight = true;


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
        if (isFollowing && targetCat != null) {
            // Move towards the cat
            int targetX = targetCat.getX();
            int distanceX = Math.abs(x - targetX);
            if (distanceX > 10) {
                if (x < targetX) {
                    x += speed;
                    isFacingRight = true;
                } else {
                    x -= speed;
                    isFacingRight = false;
                }
            } else {
                // Stop when close enough
                stopFollowing();
            }
        } else if (isMoving) {
            x += speed;
            isFacingRight = true;
            if (x > 800) {
                x = 0;
                currentRoom = (currentRoom + 1) % 3;
                isMoving = false;  // Stop moving after reaching the next room
                currentTask = "idle";
            }
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(sprite, x, y, null);
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        if (sprite != null) {
            if (!isFacingRight) {
                g2d.drawImage(sprite, x + offsetX + sprite.getWidth(), y + offsetY, -sprite.getWidth(), sprite.getHeight(), null);
            } else {
                g2d.drawImage(sprite, x + offsetX, y + offsetY, null);
            }
        }
    }

    public void moveToNextRoom() {
        if (!isMoving && !isFollowing) {  // Only start moving if not already moving or following
            isMoving = true;
            currentTask = "Moving to next room...";
        }
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

    public void startFollowing(Cat cat) {
        this.targetCat = cat;
        this.isFollowing = true;
        this.currentTask = "Following cat...";
    }

    public void stopFollowing() {
        this.isFollowing = false;
        this.targetCat = null;
        this.currentTask = "idle";
    }
}