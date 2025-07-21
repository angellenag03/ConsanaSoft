package apiclient;

import java.io.*;
import okhttp3.*;

public class Main {
    public static void main(String []args) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"id\": \"E0024/2025\",\r\n    \"nombre\": \"Obra de prueba2\",\r\n    \"direccion\": \"Calle Principal 321\",\r\n    \"latitud\": 20.4326,\r\n    \"longitud\": -109.1332\r\n}");
        Request request = new Request.Builder()
            .url("http://localhost:8080/obra/list")
            .method("GET", body)
            .addHeader("Content-Type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}