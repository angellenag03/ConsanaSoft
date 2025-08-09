
package dto;

public class MaterialGeneraValeDTO {
    private String id;
    private String nombre;
    private String unidad;
    private Double suministrado;
    private Double instalado;
    private Double porInstalar;

    public MaterialGeneraValeDTO(String id, String nombre, String unidad, Double suministrado, Double instalado, Double porInstalar) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.suministrado = suministrado;
        this.instalado = instalado;
        this.porInstalar = porInstalar;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public Double getSuministrado() { return suministrado; }
    public Double getInstalado() { return instalado; }
    public Double getPorInstalar() { return porInstalar; }
    
}
