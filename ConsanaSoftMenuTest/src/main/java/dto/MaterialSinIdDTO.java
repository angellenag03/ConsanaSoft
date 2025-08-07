 
package dto;

public class MaterialSinIdDTO {
    private String nombre;
    private String unidad;
    private Double cantidad;

    public MaterialSinIdDTO(String nombre, String unidad, Double cantidad) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public Double getCantidad() { return cantidad; }
    
}
