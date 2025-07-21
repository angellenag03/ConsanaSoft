
package dto;

public class MaterialObraDTO {
    private String id;
    private String nombre;
    private String unidad;
    private String cantidadRequerida;
    private String cantidadSuministrada;
    private String cantidadPendiente;

    public MaterialObraDTO(String id, String nombre, String unidad, String cantidadRequerida, String cantidadSuministrada, String cantidadPendiente) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidadRequerida = cantidadRequerida;
        this.cantidadSuministrada = cantidadSuministrada;
        this.cantidadPendiente = cantidadPendiente;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public String getCantidadRequerida() { return cantidadRequerida; }
    public String getCantidadSuministrada() { return cantidadSuministrada; } 
    public String getCantidadPendiente() { return cantidadPendiente; }
    
}
