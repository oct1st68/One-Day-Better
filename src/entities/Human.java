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
    private int startX;  // Store starting X position
    private int startY;  // Store starting Y position
    private boolean shouldReturnToStart = false;
    private long followEndTime = 0;
    private static final long RETURN_DELAY = 4000; // 4 seconds delay
    private boolean[] roomTasksCompleted = new boolean[3]; // Track completed tasks for each room
    private boolean isInteractingWithObject = false;  // New field to track if human is currently interacting

    public Human(int x, int y) {
        this.x = x;
        this.y = y;
        this.startX = x;  // Initialize starting position
        this.startY = y;
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
                followEndTime = System.currentTimeMillis();
                shouldReturnToStart = true;
            }
        } else if (isMoving) {
            x += speed;
            isFacingRight = true;
            if (x > 800) {
                x = 0;
                currentRoom = (currentRoom + 1) % 3;
                isMoving = false;
                currentTask = "idle";
                isInteractingWithObject = false;  // Reset interaction state when moving to new room
            }
        } else if (shouldReturnToStart && currentTask.equals("idle") && !isInteractingWithObject) {
            if (System.currentTimeMillis() - followEndTime >= RETURN_DELAY) {
                returnToStart();
            }
        }
    }

    private void returnToStart() {
        if (x < startX) {
            x += speed;
            isFacingRight = true;
        } else if (x > startX) {
            x -= speed;
            isFacingRight = false;
        } else {
            shouldReturnToStart = false;
            currentTask = "idle";
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
        if (!isMoving && !isFollowing && roomTasksCompleted[currentRoom]) {
            isMoving = true;
            currentTask = "Moving to next room...";
            isInteractingWithObject = false;  // Reset interaction state
            if (currentRoom < 2) {
                roomTasksCompleted[currentRoom + 1] = false;
            }
        }
    }

    public void interactWithCurrentObject() {
        if (!isInteractingWithObject && currentTask.equals("idle")) {
            isInteractingWithObject = true;
            currentTask = "Working on task...";
            taskProgress = 0;
        }
        
        if (isInteractingWithObject) {
            taskProgress += 10;
            if (taskProgress >= 100) {
                currentTask = "idle";
                taskProgress = 0;
                isInteractingWithObject = false;
                roomTasksCompleted[currentRoom] = true;
            }
        }
    }

    public boolean isCurrentRoomTasksCompleted() {
        return roomTasksCompleted[currentRoom];
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