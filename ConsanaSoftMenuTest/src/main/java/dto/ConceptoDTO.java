package dto;

public class ConceptoDTO {
    private Long id;
    private String clave;
    private String nombre;
    private String unidad;

    // Constructores
    public ConceptoDTO() {}

    public ConceptoDTO(String nombre) {
        this.nombre = nombre;
    }

    public ConceptoDTO(String nombre, String clave, String unidad) {
        this.nombre = nombre;
        this.clave = clave;
        this.unidad = unidad;
    }

    public ConceptoDTO(Long id, String clave, String nombre, String unidad) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    
}