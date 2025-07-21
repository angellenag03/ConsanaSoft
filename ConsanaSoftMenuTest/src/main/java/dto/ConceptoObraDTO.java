
package dto;

public class ConceptoObraDTO {
    private String partida;
    private String nombre;
    private String unidad;
    private int cantidad;

    public ConceptoObraDTO(String partida, String nombre, String unidad, int cantidad) {
        this.partida = partida;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public String getPartida() { return partida; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public int getCantidad() { return cantidad; }

    public void setPartida(String partida) { this.partida = partida; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

}