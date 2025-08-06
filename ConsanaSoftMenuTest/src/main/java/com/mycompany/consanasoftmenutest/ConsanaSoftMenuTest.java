package com.mycompany.consanasoftmenutest;

import javax.swing.SwingUtilities;
import ui.MenuFrame;
import utils.FontSetter;

public class ConsanaSoftMenuTest {

    public static void main(String[] args) {
        FontSetter.setDefaultFontToBahnschrift();
        
        SwingUtilities.invokeLater(() -> {
            MenuFrame mf = new MenuFrame();
        });
    }
}
