package dto;

public class MaterialDTO {
    private Long id;
    private String nombre;
    private String unidad;

    // Constructores
    public MaterialDTO() {}

    public MaterialDTO(String nombre, String unidad) {
        this.nombre = nombre;
        this.unidad = unidad;
    }

    public MaterialDTO(Long id, String nombre, String unidad) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
}