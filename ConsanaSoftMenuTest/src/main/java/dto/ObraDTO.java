package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ObraDTO {
    private String id;
    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String fechaCreacion;  // Formateada como String
    private String fechaModificacion;  // Formateada como String

    // Patrón de formato personalizado para México/España
    private static final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("dd/MM/yyyy, hh:mm a")
        .withLocale(new Locale("es", "MX"));

    // Constructor que acepta Timestamp (desde base de datos)
    public ObraDTO(String id, String nombre, String direccion, 
                  Double latitud, Double longitude,
                  String fechaCreacion, String fechaModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitude;
        this.fechaCreacion = formatearFecha(fechaCreacion);
        this.fechaModificacion = formatearFecha(fechaModificacion);
    }

    public ObraDTO(String id, String nombre, String fechaModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaModificacion = fechaModificacion;
    }

    // Constructor vacío
    public ObraDTO() {}

    // Método privado para formatear
    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            DateTimeFormatter formatoEntrada = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a");
            
            LocalDateTime fecha = LocalDateTime.parse(fechaOriginal, formatoEntrada);
            return fecha.format(formatoSalida);
        } catch (DateTimeParseException e) {
            return fechaOriginal; // Devuelve el formato original si hay error
        }
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    
    public String getFechaCreacion() { return formatearFecha(fechaCreacion); }
    public void setFechaCreacion(String fechaCreacion) { 
        this.fechaCreacion = formatearFecha(fechaCreacion); 
    }
    
    public String getFechaModificacion() { return formatearFecha(fechaModificacion); }
    public void setFechaModificacion(String fechaModificacion) { 
        this.fechaModificacion = formatearFecha(fechaModificacion); 
    }
}