package entities;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private int id;
    private String name;
    private int x;
    private int width;
    private List<GameObject> objects;
    private Color backgroundColor;
    private boolean isCompleted;
    private Rectangle leftWall;
    private Rectangle rightWall;

    public Room(int id, String name, int x, int width, Color backgroundColor) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.width = width;
        this.backgroundColor = backgroundColor;
        this.objects = new ArrayList<>();
        this.isCompleted = false;
        this.leftWall = new Rectangle(x, 0, 20, 720);
        this.rightWall = new Rectangle(x + width - 20, 0, 20, 720);
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void update() {
        for (GameObject object : objects) {
            object.update();
        }
        checkRoomCompletion();
    }

    private void checkRoomCompletion() {
        boolean allObjectsRemoved = true;
        for (GameObject object : objects) {
            if (!object.isRemoved()) {
                allObjectsRemoved = false;
                break;
            }
        }
        if (allObjectsRemoved && !objects.isEmpty()) {
            isCompleted = true;
            // Remove walls when room is completed
            leftWall = null;
            rightWall = null;
        }
    }

    public void drawBackground(Graphics2D g2d, int cameraX) {
        // Draw room background
        g2d.setColor(backgroundColor);
        g2d.fillRect(x - cameraX, 0, width, 720);

        // Only draw walls if room is not completed
        if (!isCompleted && leftWall != null && rightWall != null) {
            g2d.setColor(new Color(100, 100, 100));
            g2d.fillRect(leftWall.x - cameraX, leftWall.y, leftWall.width, leftWall.height);
            g2d.fillRect(rightWall.x - cameraX, rightWall.y, rightWall.width, rightWall.height);
        }
    }

    public void drawObjects(Graphics2D g2d, int cameraX) {
        // Draw room objects
        for (GameObject object : objects) {
            object.draw(g2d, -cameraX, 0);
        }
    }

    public boolean isPointInRoom(int x) {
        return x >= this.x && x < this.x + width;
    }

    public boolean checkCollision(Rectangle entityBounds) {
        // Only check for collisions if room is not completed
        if (!isCompleted && leftWall != null && rightWall != null) {
            return leftWall.intersects(entityBounds) || rightWall.intersects(entityBounds);
        }
        return false;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if (completed) {
            leftWall = null;
            rightWall = null;
        }
    }

    public int getCenterX() {
        return x + width / 2;
    }
} 