package a3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AvatarColorSelector {
    private JFrame frame;
    private String playerColorChoice;
    private String ghostColorChoice;
    private boolean choiceMade = false;

    public AvatarColorSelector() {
        createSelectionFrame();
    }

    private void createSelectionFrame() {
        frame = new JFrame("Choose Avatar Color");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select Your Avatar Color", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Red button (player red, ghost green)
        JButton redButton = new JButton("Red Avatar");
        redButton.setBackground(Color.RED);
        redButton.setOpaque(true);
        redButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerColorChoice = "gameAvatarUV.png";
                ghostColorChoice = "ghostAvatarUV.png";
                choiceMade = true;
                frame.dispose();
            }
        });

        // Green button (player green, ghost red)
        JButton greenButton = new JButton("Green Avatar");
        greenButton.setBackground(Color.GREEN);
        greenButton.setOpaque(true);
        greenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerColorChoice = "ghostAvatarUV.png";
                ghostColorChoice = "gameAvatarUV.png";
                choiceMade = true;
                frame.dispose();
            }
        });

        buttonPanel.add(redButton);
        buttonPanel.add(greenButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    public boolean isChoiceMade() {
        return choiceMade;
    }

    public String getPlayerColorChoice() {
        return playerColorChoice;
    }

    public String getGhostColorChoice() {
        return ghostColorChoice;
    }

    public void waitForSelection() {
        while (!choiceMade) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
