
package dto;

public class MaterialOutputDTO {
    private Long id;
    private String nombre;
    private String unidad;
    private Double cantidad;

    public MaterialOutputDTO(Long id, String nombre, String unidad, Double cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidad() {  return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) {  this.cantidad = cantidad; }
    
    
}
