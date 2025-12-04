import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Cliente Java para consumir la API de Detección de Personas
 * 
 * Requiere: JSON-simple o JSON.org library
 * Instalación con Maven:
 * <dependency>
 *     <groupId>org.json</groupId>
 *     <artifactId>json</artifactId>
 *     <version>20231013</version>
 * </dependency>
 */
public class DetectorPersonasClient {
    
    private static final String API_URL = "http://localhost:5000";
    private static final String ENDPOINT_DETECT = "/detect";
    private static final String ENDPOINT_VISUALIZACION = "/detect-con-visualizacion";
    private static final int TIMEOUT = 30000;
    
    /**
     * Detecta personas en una imagen enviando el archivo
     */
    public static DetectionResult detectarDesdeArchivo(String rutaImagen, double umbral) {
        try {
            URL url = new URL(API_URL + ENDPOINT_DETECT + "?umbral=" + umbral);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT);
            
            // Enviar archivo
            File archivo = new File(rutaImagen);
            conn.setFixedLengthStreamingMode(archivo.length());
            conn.setRequestProperty("Content-Type", "image/jpeg");
            conn.setDoOutput(true);
            
            try (FileInputStream fis = new FileInputStream(archivo);
                 OutputStream os = conn.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            
            return procesarRespuesta(conn);
            
        } catch (Exception e) {
            return new DetectionResult(false, "Error enviando archivo: " + e.getMessage(), null);
        }
    }
    
    /**
     * Detecta personas en una imagen enviando base64
     */
    public static DetectionResult detectarDesdeBase64(String rutaImagen, double umbral) {
        try {
            // Leer archivo y convertir a base64
            byte[] imagenBytes = Files.readAllBytes(Paths.get(rutaImagen));
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
            
            // Crear JSON
            JSONObject json = new JSONObject();
            json.put("imagen_base64", imagenBase64);
            json.put("umbral", umbral);
            
            URL url = new URL(API_URL + ENDPOINT_DETECT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            return procesarRespuesta(conn);
            
        } catch (Exception e) {
            return new DetectionResult(false, "Error enviando base64: " + e.getMessage(), null);
        }
    }
    
    /**
     * Detecta personas y recibe la imagen con dibujos
     */
    public static DetectionResultConImagen detectarConVisualizacion(String rutaImagen, double umbral) {
        try {
            byte[] imagenBytes = Files.readAllBytes(Paths.get(rutaImagen));
            String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
            
            JSONObject json = new JSONObject();
            json.put("imagen_base64", imagenBase64);
            json.put("umbral", umbral);
            
            URL url = new URL(API_URL + ENDPOINT_VISUALIZACION);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            if (conn.getResponseCode() != 200) {
                String error = new String(conn.getErrorStream().readAllBytes());
                return new DetectionResultConImagen(false, "Error: " + error, null, null);
            }
            
            String respuesta = new String(conn.getInputStream().readAllBytes());
            JSONObject respuestaJson = new JSONObject(respuesta);
            
            if (!respuestaJson.getBoolean("exito")) {
                return new DetectionResultConImagen(false, respuestaJson.getString("error"), null, null);
            }
            
            DetectionResult baseResult = new DetectionResult(
                true,
                null,
                respuestaJson.getJSONArray("detecciones")
            );
            
            String imagenResultadoBase64 = respuestaJson.getString("imagen_resultado_base64");
            
            return new DetectionResultConImagen(true, null, baseResult, imagenResultadoBase64);
            
        } catch (Exception e) {
            return new DetectionResultConImagen(false, "Error: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Procesa la respuesta del servidor
     */
    private static DetectionResult procesarRespuesta(HttpURLConnection conn) {
        try {
            int codigo = conn.getResponseCode();
            
            if (codigo != 200) {
                String error = new String(conn.getErrorStream().readAllBytes());
                JSONObject errorJson = new JSONObject(error);
                return new DetectionResult(false, errorJson.getString("error"), null);
            }
            
            String respuesta = new String(conn.getInputStream().readAllBytes());
            JSONObject respuestaJson = new JSONObject(respuesta);
            
            if (!respuestaJson.getBoolean("exito")) {
                return new DetectionResult(false, respuestaJson.getString("error"), null);
            }
            
            return new DetectionResult(
                true,
                null,
                respuestaJson.getJSONArray("detecciones")
            );
            
        } catch (Exception e) {
            return new DetectionResult(false, "Error procesando respuesta: " + e.getMessage(), null);
        }
    }
    
    /**
     * Clase para representar el resultado de la detección
     */
    public static class DetectionResult {
        public boolean exito;
        public String error;
        public JSONArray detecciones;
        public int totalDetecciones;
        
        public DetectionResult(boolean exito, String error, JSONArray detecciones) {
            this.exito = exito;
            this.error = error;
            this.detecciones = detecciones;
            this.totalDetecciones = detecciones != null ? detecciones.length() : 0;
        }
        
        @Override
        public String toString() {
            if (!exito) {
                return "Error: " + error;
            }
            return "Total detecciones: " + totalDetecciones + "\n" + detecciones.toString(2);
        }
    }
    
    /**
     * Clase para resultado con imagen
     */
    public static class DetectionResultConImagen {
        public boolean exito;
        public String error;
        public DetectionResult detectionResult;
        public String imagenBase64;
        
        public DetectionResultConImagen(boolean exito, String error, DetectionResult detectionResult, String imagenBase64) {
            this.exito = exito;
            this.error = error;
            this.detectionResult = detectionResult;
            this.imagenBase64 = imagenBase64;
        }
        
        public void guardarImagen(String rutaSalida) throws IOException {
            if (imagenBase64 == null) {
                throw new IOException("No hay imagen para guardar");
            }
            byte[] decodedBytes = Base64.getDecoder().decode(imagenBase64);
            Files.write(Paths.get(rutaSalida), decodedBytes);
        }
    }
    
    // ==================== EJEMPLO DE USO ====================
    
    public static void main(String[] args) {
        System.out.println("=== Cliente Detección de Personas ===\n");
        
        // Ejemplo 1: Detectar personas
        System.out.println("Ejemplo 1: Detección básica");
        String rutaImagen = "ruta/a/tu/imagen.jpg";
        DetectionResult resultado = DetectorPersonasClient.detectarDesdeBase64(rutaImagen, 0.7);
        System.out.println(resultado);
        
        // Ejemplo 2: Con visualización
        System.out.println("\n\nEjemplo 2: Detección con visualización");
        DetectionResultConImagen resultadoConImagen = 
            DetectorPersonasClient.detectarConVisualizacion(rutaImagen, 0.7);
        
        if (resultadoConImagen.exito) {
            System.out.println("✓ Detección exitosa: " + 
                resultadoConImagen.detectionResult.totalDetecciones + " personas detectadas");
            
            // Guardar imagen con dibujos
            try {
                resultadoConImagen.guardarImagen("resultado_detecciones.jpg");
                System.out.println("✓ Imagen guardada: resultado_detecciones.jpg");
            } catch (IOException e) {
                System.err.println("Error guardando imagen: " + e.getMessage());
            }
        } else {
            System.out.println("✗ Error: " + resultadoConImagen.error);
        }
    }
}
