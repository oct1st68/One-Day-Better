package states;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import entities.Human;
import entities.Cat;
import entities.Room;
import entities.GameObject;
import input.KeyboardInput;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class PlayState extends State {
    private Human human;
    private Cat cat;
    private KeyboardInput keyboardInput;
    private Clip bgMusic;
    private int cameraX = 0;
    private final int SCREEN_WIDTH = 1280;
    private final int WORLD_WIDTH = 6000; // 5 rooms * 1200px each
    public List<Room> rooms;
    public Room currentRoom;
    private String currentMusicFile = "res/sounds/bgmusicmenu.wav";
    private boolean isEnding = false;
    private BufferedImage endingImage;
    private long endingStartTime = 0;
    private static final long ENDING_DURATION = 5000; // 5 seconds for ending scene
    private long bothInRoom4Time = 0;
    private boolean bothInRoom4Started = false;

    public PlayState(GameStateManager gsm, KeyboardInput keyboardInput) {
        super(gsm);
        // Initialize rooms first so entities can reference them correctly
        initializeRooms();

        human = new Human(50, 150);
        cat = new Cat(50, 150);
        human.setPlayState(this);
        cat.setPlayState(this);
        this.keyboardInput = keyboardInput;
        playMusic();
        
        // Load ending image
        try {
            endingImage = ImageIO.read(new File("res/sprites/GameState/end.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeRooms() {
        rooms = new ArrayList<>();
        // Bedroom (0-1200) -- USE IMAGE
        Room room1 = new Room(
            0,
            "Bedroom",
            0,
            1200,
            new Color(220, 220, 240),
            "res/sprites/room/Bedroom/background.png"
        );
        // Add scattered trash on the floor (these will disappear when cleaned)
        room1.addObject(new GameObject(200, 400, 300, 200, "Clothes", true));
        room1.addObject(new GameObject(600,530, 200, 200, "Floor Trash", true));
        
        // Add empty trash can (will become full when cleaning is done)
        room1.addObject(new GameObject(850, 500, 150, 150, "Trash Can",  true));
        rooms.add(room1);


        // Bathroom (1200-2400)
        Room room2 = new Room(1, "Bathroom", 1200, 1200, new Color(220, 240, 220),"res/sprites/room/Bathroom/Bathroom.png");
        // Add bathtub (dirty -> clean)
        room2.addObject(new GameObject(1500, 260, 450, 400, "Bathtub",  true));
        rooms.add(room2);

        // Kitchen (2400-3600)
        Room room3 = new Room(2, "Kitchen", 2400, 1200, new Color(240, 220, 220),"res/sprites/room/Kitchen/Kitchen.png");
        room3.addObject(new GameObject(2700, 550, 120, 120, "Cat food",  true));
        room3.addObject(new GameObject(2930, 280, 150, 150, "Dishes",  true));
        rooms.add(room3);

        // Living Room (3600-4800)
        Room room4 = new Room(3, "Living Room", 3600, 1200, new Color(220, 220, 240),"res/sprites/room/Living room/Living room.png");
        rooms.add(room4);

        Room room5 = new Room(4, "Study", 4800, 1200, new Color(235, 235, 210));
        rooms.add(room5);
        
        currentRoom = room1;
    }

    private void playMusic() {
        if (isMusicPlaying()) return;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(currentMusicFile));
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

    private void changeMusic(String musicFile) {
        if (currentMusicFile.equals(musicFile)) return;
        currentMusicFile = musicFile;
        stopMusic();
        playMusic();
    }

    @Override
    public void update() {
        if (isEnding) {
            // Check if ending duration has passed
            if (System.currentTimeMillis() - endingStartTime >= ENDING_DURATION) {
                // You might want to transition to a credits screen or menu here
                gsm.setState(GameState.MENU);
            }
            return;
        }

        // Detect both in room 4 (index 3) to start ending timer
        int catRoomIndex = cat.getX() / 1200;
        int humanRoomIndex = human.getX() / 1200;
        if (catRoomIndex == 3 && humanRoomIndex == 3) {
            if (!bothInRoom4Started) {
                bothInRoom4Started = true;
                bothInRoom4Time = System.currentTimeMillis();
            } else if (!isEnding && System.currentTimeMillis() - bothInRoom4Time >= 2000) {
                triggerEnding();
            }
        } else {
            bothInRoom4Started = false;
            bothInRoom4Time = 0;
        }

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
        
        // Update current room and its objects
        updateCurrentRoom();
        
        // Check cat proximity to objects
        if (currentRoom != null) {
            for (GameObject object : currentRoom.getObjects()) {
                object.checkCatProximity(cat);
            }
        }
        
        // Update camera position
        updateCamera();
        // Check for pause
        if (keyboardInput.escapePressed) {
            gsm.setState(GameState.PAUSED);
        }
    }

    private void updateCamera() {
        // Simple camera following
        int catCenterX = cat.getX() + 32;
        int targetX = catCenterX - SCREEN_WIDTH / 2;
        // Clamp camera position
        if (targetX < 0) targetX = 0;
        if (targetX > WORLD_WIDTH - SCREEN_WIDTH) targetX = WORLD_WIDTH - SCREEN_WIDTH;
        cameraX = targetX;
    }

    private void updateCurrentRoom() {
        // Find the current room based on cat's position
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isPointInRoom(cat.getX())) {
                if (currentRoom != room) {
                    currentRoom = room;
                    // Change music based on room
                    if (room.getName().equals("Kitchen") || room.getName().equals("Living Room")) {
                        changeMusic("res/sounds/happy.wav");
                    } else {
                        changeMusic("res/sounds/bgmusicmenu.wav");
                    }
                }
                // Update current room and its objects
                currentRoom.update();
                // --- Wall collision for cat ---
                Rectangle catBounds = new Rectangle(cat.getX(), cat.getY(), 64, 64);
                boolean leftWallActive = false;
                boolean rightWallActive = false;
                if (cat.getX() < currentRoom.getX() + 30 && i > 0) {
                    Room prevRoom = rooms.get(i - 1);
                    leftWallActive = !prevRoom.isCompleted();
                }
                if (cat.getX() > currentRoom.getX() + currentRoom.getWidth() - 94 && i < rooms.size() - 1) {
                    rightWallActive = !currentRoom.isCompleted();
                }
                if (leftWallActive && cat.getX() < currentRoom.getX() + 20) {
                    cat.setPosition(currentRoom.getX() + 20, cat.getY());
                }
                if (rightWallActive && cat.getX() > currentRoom.getX() + currentRoom.getWidth() - 84) {
                    cat.setPosition(currentRoom.getX() + currentRoom.getWidth() - 84, cat.getY());
                }
                // --- Wall collision for human ---
                Rectangle humanBounds = new Rectangle(human.getX(), human.getY(), 64, 64);
                boolean humanLeftWallActive = false;
                boolean humanRightWallActive = false;
                if (human.getX() < currentRoom.getX() + 30 && i > 0) {
                    Room prevRoom = rooms.get(i - 1);
                    humanLeftWallActive = !prevRoom.isCompleted();
                }
                if (human.getX() > currentRoom.getX() + currentRoom.getWidth() - 94 && i < rooms.size() - 1) {
                    humanRightWallActive = !currentRoom.isCompleted();
                }
                if (humanLeftWallActive && human.getX() < currentRoom.getX() + 20) {
                    human.setPosition(currentRoom.getX() + 20, human.getY());
                }
                if (humanRightWallActive && human.getX() > currentRoom.getX() + currentRoom.getWidth() - 84) {
                    human.setPosition(currentRoom.getX() + currentRoom.getWidth() - 84, human.getY());
                }
                break;
            }
        }
    }

    public GameObject findNearestInteractableObject(Human human) {
        GameObject nearestObject = null;
        double minDistance = Double.MAX_VALUE;
        List<GameObject> objects = currentRoom.getObjects();
        for (GameObject object : objects) {
            if (object.isInteractable()) {
                double distance = Math.abs(object.getX() - human.getX());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestObject = object;
                }
            }
        }
        if (minDistance <= 100) {
            return nearestObject;
        }
        return null;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isEnding) {
            // Draw ending scene
            if (endingImage != null) {
                // Draw the ending image centered on screen
                int x = (SCREEN_WIDTH - endingImage.getWidth()) / 2;
                int y = (720 - endingImage.getHeight()) / 2; // Assuming 720 is your screen height
                g2d.drawImage(endingImage, x, y, null);
            }
            return;
        }

        // Draw all room backgrounds first
        for (Room room : rooms) {
            room.drawBackground(g2d, cameraX);
        }
        
        // Draw human (behind objects)
        human.draw(g2d, -cameraX, 0);
        
        // Draw all room objects
        for (Room room : rooms) {
            room.drawObjects(g2d, cameraX);
        }
        
        // Draw cat last (in front of everything)
        cat.draw(g2d, -cameraX, 0);
        
        // Draw dividing walls between rooms
        drawRoomDividers(g2d);
        
        // Draw darkness overlay for unopened rooms
        for (int i = 1; i < rooms.size(); i++) {
            Room prevRoom = rooms.get(i - 1);
            Room thisRoom = rooms.get(i);
            // If previous room is not completed, cover this room with darkness
            if (!prevRoom.isCompleted()) {
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(thisRoom.getX() - cameraX, 0, thisRoom.getWidth(), 720);
            }
        } 
    }

    // Add this new method to handle room dividers
    private void drawRoomDividers(Graphics2D g2d) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room currentRoom = rooms.get(i);
            
            // Draw right wall of current room if it's not completed
            // This creates a dividing line between current room and next room
            if (!currentRoom.isCompleted()) {
                g2d.setColor(Color.BLACK);
                int wallX = currentRoom.getX() + currentRoom.getWidth() - 20;
                g2d.fillRect(wallX - cameraX, 0, 20, 720);
            }
        }
    }

    public KeyboardInput getKeyboardInput() {
        return keyboardInput;
    }

    // Add this method to trigger the ending
    public void triggerEnding() {
        isEnding = true;
        endingStartTime = System.currentTimeMillis();
        stopMusic(); // Stop the background music
        // You might want to play ending music here
    }
} 