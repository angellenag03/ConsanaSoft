 
package dto;

public class MaterialSinIdDTO {
    private String clave;
    private String nombre;
    private String unidad;
    private Double cantidad;

    public MaterialSinIdDTO(String clave, String nombre, String unidad, Double cantidad) {
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public Double getCantidad() { return cantidad; }
    
}
