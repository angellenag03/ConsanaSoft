
package dto;

public class ConceptoObraDTO {
    private String partida;
    private String nombre;
    private String unidad;
    private int cantidad;
    private Long id;

    public ConceptoObraDTO(String partida, String nombre, String unidad, int cantidad, Long id) {
        this.partida = partida;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.id = id;
    }

    public String getPartida() { return partida; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public int getCantidad() { return cantidad; }
    public Long getId() { return id; }
     
    public void setPartida(String partida) { this.partida = partida; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setId(Long id) { this.id = id; }
    
}