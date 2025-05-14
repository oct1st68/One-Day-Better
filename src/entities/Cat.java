package entities;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.io.IOException;
public class Cat {
    private int x, y;
    private int speed = 5;
    private int jumpForce = -15;
    private int gravity = 1;
    private int velocityY = 0;
    private boolean isJumping = false;
    private boolean isFacingRight = true;
    private String currentAnimation = "idle";
    private Clip meowSound;
    private String meowSoundPath = "res/sounds/meow.wav";
    
    // Sprite related fields
    private BufferedImage sprite;
    private static final String ANIM_PAW = "pawing";
    private static final String ANIM_HEADBUTT = "headbutting";
    private static final String ANIM_MEOW = "meowing";
    private int spriteWidth = 64;  
    private int spriteHeight = 64; 

    public Cat(int x, int y) {
        this.x = x;
        this.y = y;
        loadMeowSound();
        loadSprite();
    }

    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("res/sprites/cat/cat.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Apply gravity
        velocityY += gravity;
        y += velocityY;
        
        // Ground collision
        if (y > 550) {
            y = 550;
            velocityY = 0;
            isJumping = false;
        }
    }

    public void draw(Graphics2D g2d) {
        BufferedImage image = sprite;
        g2d.drawImage(image, x, y, spriteWidth, spriteHeight, null);
        if (sprite != null) {
            // Draw the sprite
            if (!isFacingRight) {
                // Flip the sprite horizontally when facing left
                g2d.drawImage(sprite, x + spriteWidth, y, -spriteWidth, spriteHeight, null);
            } else {
                g2d.drawImage(sprite, x, y, spriteWidth, spriteHeight, null);
            }
        } else {
            // Fallback rectangle if sprite fails to load
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(x, y, spriteWidth, spriteHeight);
        }
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        BufferedImage image = sprite;
        int drawX = x + offsetX;
        int drawY = y + offsetY;
        if (sprite != null) {
            if (!isFacingRight) {
                g2d.drawImage(sprite, drawX + spriteWidth, drawY, -spriteWidth, spriteHeight, null);
            } else {
                g2d.drawImage(sprite, drawX, drawY, spriteWidth, spriteHeight, null);
            }
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(drawX, drawY, spriteWidth, spriteHeight);
        }
    }

    private void loadMeowSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(meowSoundPath));
            meowSound = AudioSystem.getClip();
            meowSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void meow() {
        currentAnimation = "meowing";
        try {
            if (meowSound != null) {
                meowSound.setFramePosition(0);
                meowSound.start();
                
                // Stop the sound after 1.5 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(1500); // 1.5 seconds
                        if (meowSound.isRunning()) {
                            meowSound.stop();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void meow(Human human) {
        meow();
        if (human != null) {
            // Wait 1 second before starting to follow
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // 1 second delay
                    human.startFollowing(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
        
    public void moveLeft() {
        if (x > 0) {
            x -= speed;
            isFacingRight = false;
            currentAnimation = "walking";
        }
    }

    public void moveRight() {
        if (x < 2000) {
            x += speed;
            isFacingRight = true;
            currentAnimation = "walking";
        }
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpForce;
            isJumping = true;
            currentAnimation = "jumping";
        }
    }

    public void headbutt(Human human) {
        if (isNearHuman(human)) {
            // Only headbutt if facing the human
            boolean isFacingHuman = (isFacingRight && human.getX() > x) || 
                                  (!isFacingRight && human.getX() < x);
            
            if (isFacingHuman) {
                human.moveToNextRoom();
                currentAnimation = "headbutting";
                
                // Return to idle animation after headbutt
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        currentAnimation = "idle";
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    public void paw(Human human) {
        if (isNearHuman(human)) {
            human.interactWithCurrentObject();
            currentAnimation = "pawing";
        }
    }

    private boolean isNearHuman(Human human) {
        int distance = Math.abs(x - human.getX());
        return distance < 50; 
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
