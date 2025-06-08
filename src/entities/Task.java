package entities;

public class Task {
    private String name;        
    private String room;        
    private boolean isCompleted; 

    public Task(String name, String room) {
        this.name = name;
        this.room = room;
        this.isCompleted = false;
    }

    // Getters
    public String getName() { return name; }
    public String getRoom() { return room; }
    public boolean isCompleted() { return isCompleted; }

    // Setter
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
