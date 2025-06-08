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
import states.PlayState;
import java.util.HashMap;
import entities.GameObject;

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
    private PlayState playState;
    
    // Sprite related fields
    private BufferedImage sprite;
    private BufferedImage pawing;
    private BufferedImage headbutting;
    private static final String ANIM_PAW = "pawing";
    private static final String ANIM_HEADBUTT = "headbutting";
    private static final String ANIM_MEOW = "meowing";
    private int spriteWidth = 64;  
    private int spriteHeight = 64; 
    private Map<String, BufferedImage> animations;
    private boolean isHeadbutting = false;
    private boolean isPawing = false;
    private boolean isMeowing = false;
    private int headbuttTimer = 0;
    private static final int HEADBUTT_DURATION = 30;

    public Cat(int x, int y) {
        this.x = x;
        this.y = y;
        loadMeowSound();
        loadSprite();
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }

    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("res/sprites/cat/cat.jpg"));
            pawing = ImageIO.read(new File("res/sprites/cat/IDLE.png"));
            headbutting = ImageIO.read(new File("res/sprites/cat/RUN.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void update() {
        // Update headbutt state
        if (isHeadbutting) {
            headbuttTimer++;
            if (headbuttTimer >= HEADBUTT_DURATION) {
                isHeadbutting = false;
                currentAnimation = "idle";
            }
        }

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

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        BufferedImage imageToDraw = sprite;
        // Select the appropriate sprite based on current animation
        switch (currentAnimation) {
            case "pawing":
                imageToDraw = pawing;
                break;
            case "headbutting":
                imageToDraw = headbutting;
                break;
            default:
                imageToDraw = sprite;
                break;
        }
        int drawX = x + offsetX;
        int drawY = y + offsetY;
        if (imageToDraw != null) {
            if (!isFacingRight) {
                g2d.drawImage(imageToDraw, drawX + spriteWidth, drawY, -spriteWidth, spriteHeight, null);
            } else {
                g2d.drawImage(imageToDraw, drawX, drawY, spriteWidth, spriteHeight, null);
            }
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(drawX, drawY, spriteWidth, spriteHeight);
        }
    }

    private void loadMeowSound() {
        try {
            // Get the absolute path to the sound file
            File soundFile = new File(meowSoundPath);
            if (!soundFile.exists()) {
                // Try alternative path
                soundFile = new File("src/" + meowSoundPath);
                if (!soundFile.exists()) {
                    System.err.println("Meow sound file not found");
                    return;
                }
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            meowSound = AudioSystem.getClip();
            meowSound.open(audioInputStream);
            audioInputStream.close();
        } catch (Exception e) {
            System.err.println("Error loading meow sound: " + e.getMessage());
        }
    }

    private void playMeowSound() {
        try {
            if (meowSound != null) {
                // Stop any currently playing meow
                if (meowSound.isRunning()) {
                    meowSound.stop();
                }
                meowSound.setFramePosition(0);
                meowSound.start();
                
                // Stop the sound after 1.5 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        if (meowSound.isRunning()) {
                            meowSound.stop();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            System.err.println("Error playing meow sound: " + e.getMessage());
        }
    }

    public void meow(Human human) {
        if (!isHeadbutting && !isPawing && !isMeowing) {
            isMeowing = true;
            currentAnimation = "meow";
            // Play the meow sound effect
            playMeowSound();
            int humanRoom = human.getX() / 1200;  // Each room is 1200 pixels wide
            int catRoom = this.x / 1200;
            
            if (humanRoom == catRoom) {  // Only interact if in same room
                // Make human face toward cat's position instead of following
                human.setFacingDirection(this.x > human.getX());
            }
            
            // Reset animation after meow
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    isMeowing = false;
                    currentAnimation = "idle";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void paw(Human human) {
        if (!isHeadbutting && !isPawing && !isMeowing) {
            isPawing = true;
            currentAnimation = "pawing";
            
            // Check if human is in the same room
            int humanRoom = human.getX() / 1200;  // Each room is 1200 pixels wide
            int catRoom = this.x / 1200;
            
            if (humanRoom == catRoom) {  // Only interact if in same room
                // Make human move to cat's position
                human.moveToCatPosition(this);
                
                // Find nearest interactable object to the cat
                GameObject nearestObject = null;
                double minDistance = Double.MAX_VALUE;
                if (playState != null) {
                    for (GameObject object : playState.currentRoom.getObjects()) {
                        if (object.isInteractable() && !object.isRemoved()) {
                            double distance = Math.abs(object.getX() - this.x);
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestObject = object;
                            }
                        }
                    }
                }
                
                // If there's an object nearby, make human interact with it after reaching cat
                if (nearestObject != null && minDistance <= 150) {
                    human.setTaskAfterCatInteraction(nearestObject);
                }
            }
            
            // Reset animation after paw
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    isPawing = false;
                    currentAnimation = "idle";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
        
    public void moveLeft() {
        if (x > 0) {  // Only prevent going beyond the leftmost boundary
            x -= speed;
            isFacingRight = false;
            currentAnimation = "walk";
        }
    }

    public void moveRight() {
        if (x < 4800) {
            x += speed;
            isFacingRight = true;
            currentAnimation = "walk";
        }
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpForce;
            isJumping = true;
            currentAnimation = "jump";
        }
    }

    public void headbutt(Human human) {
        if (!isHeadbutting && !isPawing && !isMeowing) {
            isHeadbutting = true;
            headbuttTimer = 0;
            currentAnimation = "headbutting";
            
            // Check if human is nearby (within 100 pixels)
            int distanceToHuman = Math.abs(x - human.getX());
            if (distanceToHuman <= 100 && playState != null) {
                int humanRoomIdx = human.getX() / 1200;
                int catRoomIdx = this.x / 1200;
                
                // Only grant permission if cat and human are in the same room
                if (humanRoomIdx == catRoomIdx && humanRoomIdx < playState.rooms.size() - 1 && 
                    playState.rooms.get(humanRoomIdx).isCompleted()) {
                    // Grant permission for human to move to next room
                    human.grantCatPermissionToMoveRoom();
                    human.moveToNextRoom();
                }
            }
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
