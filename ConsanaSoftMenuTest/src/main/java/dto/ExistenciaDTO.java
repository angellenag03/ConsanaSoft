
package dto;

public class ExistenciaDTO {
    private String ubicacion;
    private Double existente;

    public ExistenciaDTO(String ubicacion, Double cantidad) {
        this.ubicacion = ubicacion;
        this.existente = cantidad;
    }

    public String getUbicacion() { return ubicacion; }
    public Double getExistente() { return existente; }
}
