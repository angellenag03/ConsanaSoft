package utils;

import com.google.gson.Gson;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.apache.http.conn.HttpHostConnectException;
import secrets.Secret;

/**
 * Clase HTTPManager con patrón Singleton para manejo centralizado de peticiones HTTP
 * Versión corregida para problemas de encoding JSON
 */
public final class HTTPManager {
    private Secret config = Secret.SERVER_CONFIG;
    // Configuración fija de la API
    private final String URL = config.getUrl();
    private final int PORT = config.getPort();
    
    private final HashMap<String, String> headersMap;
    
    // Instancia Singleton
    private static HTTPManager instance;
    
    private int statusCode;
    
    public enum HttpMethod { GET, POST, PUT, DELETE, PATCH }
    
    private HTTPManager() {
        this.headersMap = new HashMap<>();
        // Configuración de headers con charset UTF-8
        headersMap.put("Content-Type", "application/json; charset=UTF-8");
        headersMap.put("Accept", "application/json; charset=UTF-8");
    }
    
    /**
     * Obtiene la instancia única (Singleton)
     */
    public static synchronized HTTPManager getInstance() {
        if (instance == null) {
            instance = new HTTPManager();
        }
        return instance;
    }
    
    /**
     * Método principal para ejecutar peticiones HTTP
     * @param method
     * @param endpoint
     * @param requestBody
     * @return 
     * @throws java.io.IOException
     */
    public String executeRequest(HttpMethod method, String endpoint, 
                               HashMap<String, Object> requestBody) throws IOException {
        String url = buildUrl(endpoint);
        HttpRequestBase request = createRequest(method, url);
        
        addHeaders(request);
        
        if (requiresBody(method) && requestBody != null) {
            HttpEntityEnclosingRequestBase requestWithBody = (HttpEntityEnclosingRequestBase) request;
            requestWithBody.setEntity(createBody(requestBody));
        }
        
        return execute(request);
    }
    
     
    /**
     * el parametro lo estuve considerando como un String 
     * pero como lo usaré para buscar por id es mejor tenerlo 
     * de esta manera (pienso yo)
     * NOTA: a lo mejor y no ocupo esta clase porque perfectamente podría
     * concatenar el requestParam y usar codificar dato, dejando el map en null
     * ya que en peticiones get solo requiero el requestParam dejando el HashMap
     * en null
     * SEGUNDA NOTA: no we si sirve, hay varias librerías que hacen esto
     * @param method
     * @param endpoint
     * @param requestParam
     * @return
     * @throws IOException 
     */
    public String executeRequest(HttpMethod method, String endpoint, Object requestParam) throws IOException {
        requestParam = codificarDato(requestParam.toString());
        String url = buildUrl(endpoint+requestParam);
        HttpRequestBase request = createRequest(method, url);
        
        addHeaders(request);
        
        return execute(request);
    }
    
    public String executeRequest(String endpoint) throws IOException {
        String url = buildUrl(endpoint);
        HttpRequestBase request = createRequest(HttpMethod.GET, url);

        addHeaders(request);

        String response = execute(request);

        // Validación adicional para asegurar que no se devuelva basura
        if (response.startsWith("http://") || response.startsWith("https://")) {
            throw new IOException("Respuesta inválida: parece contener una URL");
        }

        return response;
    }
    
    /**
     * Añade un header personalizado (opcional)
     */
    public void addCustomHeader(String key, String value) {
        headersMap.put(key, value);
    }
    
    // ================= MÉTODOS PRIVADOS =================
    
    private String buildUrl(String endpoint) {
        return URL + ":" + PORT + endpoint;
    }
    
    private HttpRequestBase createRequest(HttpMethod method, String url) {
        switch (method) {
            case GET: return new HttpGet(url);
            case POST: return new HttpPost(url);
            case PUT: return new HttpPut(url);
            case DELETE: return new HttpDelete(url);
            case PATCH: return new HttpPatch(url);
            default: throw new IllegalArgumentException("Método HTTP no soportado");
        }
    }
    
    private void addHeaders(HttpRequestBase request) {
        headersMap.forEach(request::addHeader);
    }
    
    private boolean requiresBody(HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
    }
    
    private StringEntity createBody(HashMap<String, Object> data) throws UnsupportedEncodingException {
        // Sanitiza los datos antes de convertirlos a JSON
        HashMap<String, Object> sanitizedData = sanitizeData(data);
        String json = new Gson().toJson(sanitizedData);
        
        // Crea el cuerpo con encoding UTF-8 explícito
        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentType("application/json; charset=UTF-8");
        return entity;
    }
    
    private HashMap<String, Object> sanitizeData(HashMap<String, Object> data) {
        HashMap<String, Object> sanitized = new HashMap<>();
        data.forEach((key, value) -> {
            if (value instanceof String) {
                // Escapa caracteres problemáticos
                String stringValue = (String) value;
                sanitized.put(key, stringValue
                    .replace("\\", "\\\\")  // Escapa backslashes
                    .replace("\"", "\\\"")  // Escapa comillas
                    .replace("\n", "\\n")   // Escapa saltos de línea
                    .replace("\t", "\\t")   // Escapa tabs
                );
            } else {
                sanitized.put(key, value);
            }
        });
        return sanitized;
    }
    
    private String execute(HttpRequestBase request) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            // Limpiar la respuesta por si acaso
            responseBody = responseBody.trim();

            if (statusCode >= 200 && statusCode < 300) {
                // Verificar que no contenga la URL base
                if (responseBody.startsWith(URL + ":" + PORT)) {
                    return responseBody.replace(URL + ":" + PORT, "");
                }
                return responseBody;
            } else {
                JOptionPane.showMessageDialog(null, 
                    responseBody, 
                    "Código de Error: "+statusCode, 
                    JOptionPane.ERROR_MESSAGE);
                System.out.println(responseBody);
                throw new IOException("Error en la petición. Código: " 
                        +statusCode+"\n"+responseBody);
            }
        } 
          catch(HttpHostConnectException e) {
            if (statusCode == 404) {
                JOptionPane.showMessageDialog(null, 
                "Hay una falla en el servidor o está desconectado: \n"+e.getMessage(), 
                "Fallo de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            }
            throw new IOException("Error en la conexión: " + e.getMessage());
        } 
    }       
    
    // Implementación para PATCH
    private static class HttpPatch extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "PATCH";
        public HttpPatch(String uri) { setURI(java.net.URI.create(uri)); }
        @Override public String getMethod() { return METHOD_NAME; }
    }
    
    private String codificarDato(String data) {
        return URLEncoder.encode(data, StandardCharsets.UTF_8);
    }
}