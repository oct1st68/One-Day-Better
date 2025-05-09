package entities;
import java.awt.Graphics2D;
import java.awt.Color;

public class Cat {
    private int x, y;
    private int speed = 5;
    private int jumpForce = -15;
    private int gravity = 1;
    private int velocityY = 0;
    private boolean isJumping = false;
    private boolean isFacingRight = true;
    private String currentAnimation = "idle";

    public Cat(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Apply gravity
        velocityY += gravity;
        y += velocityY;
        
        // Ground collision
        if (y > 400) {
            y = 400;
            velocityY = 0;
            isJumping = false;
        }
    }

    public void draw(Graphics2D g2d) {
                
    }

    public void moveLeft() {
        x -= speed;
        isFacingRight = false;
        currentAnimation = "walking";
    }

    public void moveRight() {
        x += speed;
        isFacingRight = true;
        currentAnimation = "walking";
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpForce;
            isJumping = true;
            currentAnimation = "jumping";
        }
    }

    public void meow() {
        currentAnimation = "meowing";
        // TODO: Add meow sound effect
    }

    public void headbutt(Human human) {
        if (isNearHuman(human)) {
            human.moveToNextRoom();
            currentAnimation = "headbutting";
        }
    }

    public void paw(Human human) {
        if (isNearHuman(human)) {
            human.interactWithCurrentObject();
            currentAnimation = "pawing";
        }
    }

    private boolean isNearHuman(Human human) {
        
        return catBounds.intersects(humanBounds);
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
}