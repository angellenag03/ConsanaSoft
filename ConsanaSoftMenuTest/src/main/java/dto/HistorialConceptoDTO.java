
package dto;

public class HistorialConceptoDTO {
    private Long conceptoId;
    private String fechaRegistro;
    private String obra;
    private String estado;
    private int cantidadInstalada;

    public HistorialConceptoDTO(Long conceptoId, String fechaRegistro, String obra, String estado, int cantidadInstalada) {
        this.conceptoId = conceptoId;
        this.fechaRegistro = fechaRegistro;
        this.obra = obra;
        this.estado = estado;
        this.cantidadInstalada = cantidadInstalada;
    }

    public Long getConceptoId() { return conceptoId; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getObra() { return obra; }
    public String getEstado() { return estado; }
    public int getCantidadInstalada() { return cantidadInstalada; }

    public void setConceptoId(Long conceptoId) { this.conceptoId = conceptoId; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setObra(String obra) { this.obra = obra; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCantidadInstalada(int cantidadInstalada) { this.cantidadInstalada = cantidadInstalada; }   
}
