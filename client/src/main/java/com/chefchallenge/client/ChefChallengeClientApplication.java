package com.chefchallenge.client;

import com.chefchallenge.client.ui.MainFrame;
import javax.swing.SwingUtilities;

public class ChefChallengeClientApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
