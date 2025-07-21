
package dto;

public class HistorialMaterialDTO {
    private Long materialId;
    private String fechaRegistro;
    private String cantidad;
    private String origen;
    private String referencia;
    private String numeroDocumento;
    private String fechaOrigen;

    public HistorialMaterialDTO(Long material_id, String cantidad, String fechaRegistro, String origen, String referencia, String numeroDocumeto, String fechaOrigen) {
        this.materialId = material_id;
        this.cantidad = cantidad;
        this.fechaRegistro = fechaRegistro;
        this.origen = origen;
        this.referencia = referencia;
        this.numeroDocumento = numeroDocumento;
        this.fechaOrigen = fechaOrigen;
    }

    public Long getMaterialId() { return materialId; }
    public String getCantidad() { return cantidad; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getOrigen() { return origen; }
    public String getReferencia() { return referencia; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getFechaOrigen() { return fechaOrigen; }
    
}
