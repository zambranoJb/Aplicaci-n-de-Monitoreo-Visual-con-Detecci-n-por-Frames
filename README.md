# API REST Detección de Personas

API REST en Flask que utiliza SSD MobileNet v2 para detectar personas en imágenes. Diseñada para ser consumida por sistemas externos como Java.

## 📋 Requisitos

- Python 3.7+
- OpenCV
- Flask
- NumPy

## 🚀 Instalación

### 1. Preparar modelos

La API requiere los siguientes archivos del modelo SSD MobileNet v2:

```
modelos/
├── frozen_inference_graph.pb
├── ssd_mobilenet_v2_coco_2018_03_29.pbtxt
└── object_detection_classes_coco.txt
```

**Descargar modelos:**
- [frozen_inference_graph.pb](http://download.tensorflow.org/models/object_detection/ssd_mobilenet_v2_coco_2018_03_29.tar.gz)
- [ssd_mobilenet_v2_coco_2018_03_29.pbtxt](https://raw.githubusercontent.com/opencv/opencv_extra/master/testdata/dnn/ssd_mobilenet_v2_coco_2018_03_29.pbtxt)
- [object_detection_classes_coco.txt](https://raw.githubusercontent.com/opencv/opencv/master/samples/data/dnn/object_detection_classes_coco.txt)

### 2. Instalar dependencias

```bash
pip install -r requirements.txt
```

### 3. Ejecutar la API

```bash
python app.py
```

La API estará disponible en `http://localhost:5000`

## 📡 Endpoints

### GET `/health`
Verifica que la API esté operativa

**Respuesta:**
```json
{
    "status": "ok",
    "modelo_cargado": true
}
```

---

### GET `/info`
Información de la API

**Respuesta:**
```json
{
    "nombre": "API Detección de Personas",
    "version": "1.0",
    "modelo": "SSD MobileNet v2",
    "dataset": "COCO",
    "tamaño_entrada": 300,
    "endpoints": { ... }
}
```

---

### POST `/detect`
Detecta personas en una imagen

**Métodos de envío:**

#### Opción 1: Archivo (multipart/form-data)
```bash
curl -X POST \
  -F "file=@imagen.jpg" \
  -F "umbral=0.7" \
  http://localhost:5000/detect
```

#### Opción 2: Base64 (application/json)
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "imagen_base64": "iVBORw0KGgoAAAANSUhEUgAAA...",
    "umbral": 0.7
  }' \
  http://localhost:5000/detect
```

**Parámetros:**
- `file` (multipart) o `imagen_base64` (json): Imagen a procesar
- `umbral` (opcional): Confianza mínima (0-1), por defecto 0.7

**Respuesta exitosa:**
```json
{
    "exito": true,
    "total_detecciones": 2,
    "umbral_utilizado": 0.7,
    "detecciones": [
        {
            "clase": "persona",
            "clase_id": 1,
            "confianza": 0.9512,
            "confianza_porcentaje": 95.12,
            "x": 150,
            "y": 200,
            "ancho": 180,
            "alto": 320
        },
        {
            "clase": "persona",
            "clase_id": 1,
            "confianza": 0.8734,
            "confianza_porcentaje": 87.34,
            "x": 450,
            "y": 100,
            "ancho": 200,
            "alto": 350
        }
    ]
}
```

**Respuesta de error:**
```json
{
    "exito": false,
    "error": "Descripción del error"
}
```

---

### POST `/detect-con-visualizacion`
Detecta personas Y devuelve la imagen con los rectángulos dibujados

**Igual que `/detect` pero con parámetro adicional:**

**Respuesta adicional:**
```json
{
    "exito": true,
    "total_detecciones": 2,
    "umbral_utilizado": 0.7,
    "detecciones": [ ... ],
    "imagen_resultado_base64": "iVBORw0KGgoAAAANSUhEUgAAA..."
}
```

---

## 💻 Consumo desde Java

### Usando el cliente Java proporcionado

#### Opción 1: Detección básica
```java
DetectorPersonasClient.DetectionResult resultado = 
    DetectorPersonasClient.detectarDesdeBase64("imagen.jpg", 0.7);

if (resultado.exito) {
    System.out.println("Detecciones: " + resultado.totalDetecciones);
    for (int i = 0; i < resultado.detecciones.length(); i++) {
        JSONObject det = resultado.detecciones.getJSONObject(i);
        System.out.println("Persona " + i + ": " + 
            det.getDouble("confianza_porcentaje") + "%");
    }
}
```

#### Opción 2: Con visualización
```java
DetectorPersonasClient.DetectionResultConImagen resultadoConImg = 
    DetectorPersonasClient.detectarConVisualizacion("imagen.jpg", 0.7);

if (resultadoConImg.exito) {
    // Acceder a detecciones
    int totalDetecciones = resultadoConImg.detectionResult.totalDetecciones;
    
    // Guardar imagen con dibujos
    resultadoConImg.guardarImagen("resultado.jpg");
}
```

### Dependencias Maven

Agregar al `pom.xml`:
```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20231013</version>
</dependency>
```

---

## 📊 Estructura de respuesta de detección

Cada objeto en el array `detecciones`:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `clase` | string | Nombre de la clase: "persona" |
| `clase_id` | int | ID de clase en COCO (1 para persona) |
| `confianza` | float | Score de confianza (0-1) |
| `confianza_porcentaje` | float | Score en porcentaje (0-100) |
| `x` | int | Coordenada X de la esquina superior izquierda |
| `y` | int | Coordenada Y de la esquina superior izquierda |
| `ancho` | int | Ancho del bounding box en píxeles |
| `alto` | int | Alto del bounding box en píxeles |

---

## 🔧 Configuración

**En `app.py`, modifica estas variables según necesites:**

```python
# Ruta de los modelos
RUTA_MODELO = "modelos/frozen_inference_graph.pb"
RUTA_CONFIGURACION = "modelos/ssd_mobilenet_v2_coco_2018_03_29.pbtxt"
RUTA_CLASES = "modelos/object_detection_classes_coco.txt"

# Tamaño de entrada de la red
TAMAÑO_IMAGEN = 300

# Límite de tamaño de archivo
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB
```

---

## 🌐 Despliegue en producción

Para producción, NO uses `debug=True`. Usa un servidor WSGI:

```bash
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:5000 app:app
```

O con uWSGI:
```bash
pip install uwsgi
uwsgi --http :5000 --wsgi-file app.py --callable app --processes 4 --threads 2
```

---

## 📝 Ejemplo completo en Java

```java
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class EjemploDeteccion {
    public static void main(String[] args) throws Exception {
        // 1. Leer imagen
        String rutaImagen = "mi_imagen.jpg";
        
        // 2. Realizar detección
        DetectorPersonasClient.DetectionResult resultado = 
            DetectorPersonasClient.detectarDesdeBase64(rutaImagen, 0.7);
        
        // 3. Procesar resultados
        if (resultado.exito) {
            System.out.println("✓ Se detectaron " + resultado.totalDetecciones + " personas");
            
            for (int i = 0; i < resultado.detecciones.length(); i++) {
                JSONObject deteccion = resultado.detecciones.getJSONObject(i);
                double confianza = deteccion.getDouble("confianza_porcentaje");
                int x = deteccion.getInt("x");
                int y = deteccion.getInt("y");
                int ancho = deteccion.getInt("ancho");
                int alto = deteccion.getInt("alto");
                
                System.out.printf("  Persona %d: %.2f%% en (%d,%d) %dx%d\n", 
                    i+1, confianza, x, y, ancho, alto);
            }
        } else {
            System.out.println("✗ Error: " + resultado.error);
        }
    }
}
```

---

## ⚙️ Solución de problemas

### Error: "Modelo no cargado"
- Verifica que los archivos del modelo estén en la carpeta `modelos/`
- Revisa los nombres exactos de los archivos

### Error: "No se pudo decodificar la imagen"
- Asegúrate de enviar un archivo de imagen válido (JPG, PNG)
- Si usas base64, verifica que esté bien codificado

### API lenta
- Aumenta el número de procesos con Gunicorn: `gunicorn -w 8 -b 0.0.0.0:5000 app:app`
- Reduce el tamaño de las imágenes si es posible

---

## 📄 Licencia

MIT

## 📧 Contacto

Para preguntas o problemas, contacta al equipo de desarrollo.
