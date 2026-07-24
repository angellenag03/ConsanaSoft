package dto;

import java.time.LocalDate;

public class SuministrarManyDTO {
    private Long[] materialesId;
    private Double[] cantidades;
    private String numeroDocumento;
    private String referencia;
    private String origen;
    private String fechaOrigen; // Se envía como String en formato ISO (YYYY-MM-DD)

    public SuministrarManyDTO(Long[] materialesId, Double[] cantidades, String numeroDocumento, 
                              String referencia, String origen, String fechaOrigen) {
        this.materialesId = materialesId;
        this.cantidades = cantidades;
        this.numeroDocumento = numeroDocumento;
        this.referencia = referencia;
        this.origen = origen;
        this.fechaOrigen = fechaOrigen;
    }

    public Long[] getMaterialesId() { return materialesId; }
    public Double[] getCantidades() { return cantidades; }
    public String getNumeroDocumento() { return numeroDocumento;}
    public String getReferencia() { return referencia; }
    public String getOrigen() { return origen; }
    public String getFechaOrigen() { return fechaOrigen; }

    public void setMaterialesId(Long[] materialesId) { this.materialesId = materialesId; }
    public void setCantidades(Double[] cantidades) { this.cantidades = cantidades; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public void setOrigen(String origen) { this.origen = origen; }
    public void setFechaOrigen(String fechaOrigen) { this.fechaOrigen = fechaOrigen; }
}
