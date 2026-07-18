
package dto;

public class ConceptoObraDTO {
    private String partida;
    private String nombre;
    private String unidad;
    private int cantidad;
    private int instalado;
    private int porInstalar;
    private Long id;

    public ConceptoObraDTO(String partida, String nombre, 
            String unidad, int cantidad, int instalado, int porInstalar, Long id) {
        this.partida = partida;
        this.nombre = nombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.instalado = instalado;
        this.porInstalar = porInstalar;
        this.id = id;
    }

    public String getPartida() { return partida; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public int getCantidad() { return cantidad; }
    public int getInstalado() { return instalado; }
    public int getPorInstalar() { return porInstalar; }
    public Long getId() { return id; }
     
    public void setPartida(String partida) { this.partida = partida; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setInstalado(int instalado) { this.instalado = instalado; }
    public void setPorInstalar(int porInstalar) { this.porInstalar = porInstalar; }
    public void setId(Long id) { this.id = id; }
}