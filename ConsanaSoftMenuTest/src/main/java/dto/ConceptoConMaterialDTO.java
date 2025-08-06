
package dto;

import java.util.List;

public class ConceptoConMaterialDTO {
    private String clave;
    private String nombre;
    private String unidad;
    private List<MaterialConceptoInputDTO> materiales;

    public ConceptoConMaterialDTO(String clave, String nombre, String unidad, List<MaterialConceptoInputDTO> materiales) {
        this.clave = clave;
        this.nombre = nombre;
        this.unidad = unidad;
        this.materiales = materiales;
    }

    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }

    public List<MaterialConceptoInputDTO> getMateriales() {
        return materiales;
    }
    
    public static class MaterialConceptoInputDTO {
        private Long materialId;
        private Double cantidad;

        public MaterialConceptoInputDTO(Long materialId, Double cantidad) {
            this.materialId = materialId;
            this.cantidad = cantidad;
        }

        public Long getMaterialId() { return materialId; }
        public Double getCantidad() { return cantidad; }
    }
}

