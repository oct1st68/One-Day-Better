package main;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
    public  GameWindow(){
        setTitle("One Day Better");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setContentPane(new GamePanel(1280, 720));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        ((GamePanel) getContentPane()).StartGameThread();
    }
}
