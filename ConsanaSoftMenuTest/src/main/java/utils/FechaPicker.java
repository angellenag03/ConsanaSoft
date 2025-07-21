package utils;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FechaPicker extends DatePicker {
    // Formateador para el formato yyyy-MM-dd
    private static final DateTimeFormatter DB_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public FechaPicker() {
        super(crearConfiguracion());
        this.setToolTipText("Selecciona una fecha");
    }
    
    private static DatePickerSettings crearConfiguracion() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setLocale(Locale.forLanguageTag("es-ES"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");    
        return settings;
    }
    
    /**
     * Obtiene la fecha seleccionada en formato yyyy-MM-dd (ej: "2025-05-10")
     * @return String con la fecha formateada o null si no hay fecha seleccionada
     */
    public String getFecha() {
        LocalDate fecha = this.getDate();
        if (fecha != null) {
            return fecha.format(DB_DATE_FORMATTER);
        }
        return null;
    }
    
}
