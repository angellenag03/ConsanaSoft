
package utils;

import com.sun.tools.javac.Main;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontSetter {
    
    public static void setDefaultFont() {
        try {
            // Cargar la fuente desde src/main/resources
            try (InputStream is = FontSetter.class.getResourceAsStream("/IBMPlexSans-Regular.ttf")) {
                if (is == null) {
                    throw new IOException("No se encontró la fuente");
                }
                Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(13f);
                System.out.println(is.toString());
                // Registrar la fuente en el entorno gráfico
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

                // Establecer como fuente por defecto para componentes Swing
                setUIFont(new FontUIResource(font));
            }

        } catch (Exception e) {
            // Si falla, usar una fuente sans-serif por defecto
            System.err.println("No se pudo cargar la fuente. Usando fuente por defecto.");
            setUIFont(new FontUIResource("SansSerif", Font.PLAIN, 12));
        }
    }
    
    private static void setUIFont(FontUIResource font) {
        // Establecer la fuente para todos los componentes Swing
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
    }
}
