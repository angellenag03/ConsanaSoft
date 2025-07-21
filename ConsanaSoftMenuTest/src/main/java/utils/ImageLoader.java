
package utils;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.InputStream;
import javax.swing.ImageIcon;

public class ImageLoader {
    
    /**
     * Carga una imagen desde los recursos del proyecto
     * @param imagen Ruta relativa dentro de la carpeta resources (ej: "images/icono.png")
     * @return Image cargada o null si falla
     */
    public static Image getImagen(String imagen) {
        try (InputStream is = ImageLoader.class.getResourceAsStream("/" + imagen)) {
            if (is == null) {
                System.err.println("No se encontró la imagen: " + imagen);
                return null;
            }
            return ImageIO.read(is);
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + imagen + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carga una imagen y la convierte a ImageIcon
     * @param ruta Ruta relativa dentro de la carpeta resources
     * @param ancho Ancho deseado (0 para tamaño original)
     * @param alto Alto deseado (0 para tamaño original)
     * @return ImageIcon escalado o null si falla
     */
    public static ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        Image img = getImagen(ruta);
        if (img == null) return null;
        
        if (ancho > 0 && alto > 0) {
            img = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        }
        return new ImageIcon(img);
    }
}
