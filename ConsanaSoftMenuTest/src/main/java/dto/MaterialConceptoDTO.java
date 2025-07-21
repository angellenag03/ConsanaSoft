package dto;

public class MaterialConceptoDTO {
    private Long id;
    private Long materialId;
    private Long conceptoId;
    private double cantidad;

    // Constructores
    public MaterialConceptoDTO() {}

    public MaterialConceptoDTO(Long materialId, Long conceptoId, double cantidad) {
        this.materialId = materialId;
        this.conceptoId = conceptoId;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public Long getConceptoId() { return conceptoId; }
    public void setConceptoId(Long conceptoId) { this.conceptoId = conceptoId; }
    
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
}
