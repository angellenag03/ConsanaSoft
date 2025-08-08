package dto;

public class MaterialDTO {
    private Long id;
    private String clave;
    private String nombre;
    private String unidad;

    // Constructores
    public MaterialDTO() {}

    public MaterialDTO(String clave, String nombre, String unidad) {
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
    }

    public MaterialDTO(Long id, String clave, String nombre, String unidad) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
}