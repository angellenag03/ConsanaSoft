
package dto;

public class MaterialOutputDTO {
    private Long id;
    private String clave;
    private String nombre;
    private String unidad;
    private Double cantidad;

    public MaterialOutputDTO(Long id, String clave, String nombre, String unidad, Double cantidad) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getUnidad() {  return unidad; }
    public Double getCantidad() { return cantidad; }
    
}
