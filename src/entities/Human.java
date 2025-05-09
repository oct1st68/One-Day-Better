package entities;
import java.awt.Graphics2D;
import java.awt.Color;

public class Human {
    private int x, y;
    private int speed = 3;
    private boolean isMoving = false;
    private String currentTask = "idle";
    private int taskProgress = 0;
    private int currentRoom = 0;

    public Human(int x, int y) {
        this.x = x;
        this.y = y;
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
        // Draw body
        g2d.setColor(Color.BLUE);
        g2d.fillRect(x, y, 32, 48);
        
        // Draw head
        g2d.setColor(Color.PINK);
        g2d.fillOval(x + 8, y - 16, 16, 16);
        
        // Draw task progress
        if (!currentTask.equals("idle")) {
            g2d.setColor(Color.BLACK);
            g2d.drawString(currentTask + ": " + taskProgress + "%", x, y - 20);
        }
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