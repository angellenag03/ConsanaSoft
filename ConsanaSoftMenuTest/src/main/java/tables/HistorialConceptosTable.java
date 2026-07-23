package tables;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.HistorialConceptoDTO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistorialConceptosTable extends BaseTable {

    private List<HistorialConceptoDTO> historialCompleto = new ArrayList<>();

    public HistorialConceptosTable() {
        super();
        // Ej: 17/07/2026, 09:00:00 | Se {ESTADO} {CANTIDAD} a la obra
        initializeModel(new String[] {
            "Registro"
        });
    }

    @Override
    public void cargarDatosIniciales() {
        // Esta tabla no carga datos inicialmente
        clearTable();
    }

    public void cargarDatosConceptoObra(Long conceptoId, String obraId) {
        try {
            obraId = URLEncoder.encode(obraId, "UTF-8");
            cargarDatos("/historial/concepto/list?conceptoId="+conceptoId+"&obra="+obraId);
        } catch (Exception e) {
            handleError(e, "cargarDatosConceptoObra HistorialConceptosTable");
        }
    }

    private void cargarDatos(String endpoint) {
        try {
            clearTable();
            String response = http.executeRequest(endpoint);
            Type historialJson = new TypeToken<List<HistorialConceptoDTO>>(){}.getType();
            historialCompleto = gson.fromJson(response, historialJson);
            renderFiltrado(historialCompleto);
        } catch (JsonSyntaxException | IOException e) {
            handleError(e, "cargarDatos HistorialMaterialTable");
        }
    }

    /**
     * Aplica el filtro seleccionado en el combobox (filtrosRegBox de ObraPanel)
     * sobre la lista ya cargada en memoria, sin volver a pegarle al backend.
     */
    public void aplicarFiltro(String filtro) {
        if (historialCompleto == null) return;

        List<HistorialConceptoDTO> filtrado;
        switch (filtro) {
            case "Añadido":
                filtrado = filtrarPorEstados('A');
                break;
            case "Removido":
                filtrado = filtrarPorEstados('R');
                break;
            case "Instalado":
                filtrado = filtrarPorEstados('I');
                break;
            case "Desinstalado":
                filtrado = filtrarPorEstados('D');
                break;
            case "Añadido y Removido":
                filtrado = filtrarPorEstados('A', 'R');
                break;
            case "Instalado y Desinstalado":
                filtrado = filtrarPorEstados('I', 'D');
                break;
            case "General":
            default:
                filtrado = historialCompleto;
        }
        renderFiltrado(filtrado);
    }

    private List<HistorialConceptoDTO> filtrarPorEstados(Character... estados) {
        Set<Character> permitidos = new HashSet<>(Arrays.asList(estados));
        List<HistorialConceptoDTO> resultado = new ArrayList<>();
        for (HistorialConceptoDTO h : historialCompleto) {
            if (permitidos.contains(h.getEstado().charAt(0))) {
                resultado.add(h);
            }
        }
        return resultado;
    }

    private void renderFiltrado(List<HistorialConceptoDTO> lista) {
        clearTable();
        String initTxt;
        for (HistorialConceptoDTO h : lista) {
            switch (h.getEstado().charAt(0)) {
                case 'A': initTxt = "Se añadieron "; break;
                case 'I': initTxt = "Se instalaron "; break;
                case 'D': initTxt = "Se desinstalaron "; break;
                case 'R': initTxt = "Se removieron "; break;
                default: initTxt =
                        "De alguna forma logró salir "
                        + "este texto y eso no es normal... ";
            }
            String registro = initTxt + h.getCantidadInstalada() + " en la obra";
            model.addRow(new Object[]{
                h.getFechaRegistro()+" | "+registro
            });
        }
    }

    public String getConceptoID() {
        Object value = getSelectedValue(6);
        return value != null ? value.toString() : null;
    }

    @Override
    protected void ajustarTabla() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}