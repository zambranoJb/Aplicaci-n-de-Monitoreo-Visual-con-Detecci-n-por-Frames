# Estructura del Proyecto - API Detección de Personas

```
api-deteccion/
│
├── app.py                              # Aplicación principal (Flask)
├── requirements.txt                    # Dependencias Python
├── test_api.py                         # Script de pruebas
├── DetectorPersonasClient.java        # Cliente Java
├── README.md                           # Documentación completa
├── ESTRUCTURA.md                       # Este archivo
│
├── modelos/                            # 📁 Crear esta carpeta
│   ├── frozen_inference_graph.pb
│   ├── ssd_mobilenet_v2_coco_2018_03_29.pbtxt
│   └── object_detection_classes_coco.txt
│
├── uploads/                            # 📁 Se crea automáticamente
│   └── (archivos temporales)
│
└── ejemplos/                           # 📁 Crear para ejemplos
    ├── ejemplo_java_simple.java
    ├── ejemplo_java_avanzado.java
    └── imagen_prueba.jpg
```

## 📋 Pasos para configurar

### 1. Instalar dependencias Python
```bash
pip install -r requirements.txt
```

### 2. Descargar modelos
```bash
# Crear carpeta
mkdir modelos

# Descargar frozen_inference_graph.pb
# Desde: http://download.tensorflow.org/models/object_detection/ssd_mobilenet_v2_coco_2018_03_29.tar.gz

# Descargar ssd_mobilenet_v2_coco_2018_03_29.pbtxt
# Desde: https://raw.githubusercontent.com/opencv/opencv_extra/master/testdata/dnn/ssd_mobilenet_v2_coco_2018_03_29.pbtxt

# Descargar object_detection_classes_coco.txt
# Desde: https://raw.githubusercontent.com/opencv/opencv/master/samples/data/dnn/object_detection_classes_coco.txt
```

### 3. Ejecutar la API
```bash
python app.py
```

### 4. En otra terminal, probar
```bash
python test_api.py
```

## 🔌 Consumir desde Java

### Opción 1: Código simple
```java
DetectorPersonasClient.DetectionResult resultado = 
    DetectorPersonasClient.detectarDesdeBase64("imagen.jpg", 0.7);

if (resultado.exito) {
    System.out.println("Detecciones: " + resultado.totalDetecciones);
}
```

### Opción 2: Con visualización
```java
DetectorPersonasClient.DetectionResultConImagen resultadoConImg = 
    DetectorPersonasClient.detectarConVisualizacion("imagen.jpg", 0.7);

if (resultadoConImg.exito) {
    resultadoConImg.guardarImagen("resultado.jpg");
}
```

## 📡 Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Verifica disponibilidad |
| GET | `/info` | Información de la API |
| POST | `/detect` | Detecta personas |
| POST | `/detect-con-visualizacion` | Detecta y devuelve imagen |

## 🛠️ Configuración personalizada

Edita `app.py`:

```python
# Rutas de modelos (línea ~18)
RUTA_MODELO = "modelos/frozen_inference_graph.pb"
RUTA_CONFIGURACION = "modelos/ssd_mobilenet_v2_coco_2018_03_29.pbtxt"
RUTA_CLASES = "modelos/object_detection_classes_coco.txt"

# Puerto (línea ~320)
app.run(debug=True, host='0.0.0.0', port=5000)

# Máximo tamaño de imagen (línea ~15)
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB
```

## 🐛 Troubleshooting

**Error: "Modelo no cargado"**
- ✓ Verifica que los archivos estén en `modelos/`
- ✓ Comprueba los nombres exactos

**Error: "No se pudo decodificar la imagen"**
- ✓ Envía un archivo JPG o PNG válido
- ✓ Si usas base64, verifica que esté correcto

**API lenta**
- ✓ Usa Gunicorn: `pip install gunicorn && gunicorn -w 4 -b 0.0.0.0:5000 app:app`

## 📚 Recursos útiles

- [Flask Documentation](https://flask.palletsprojects.com/)
- [OpenCV DNN](https://docs.opencv.org/master/d2/d58/tutorial_table_of_content_dnn.html)
- [COCO Dataset](http://cocodataset.org/)
- [TensorFlow Models](https://github.com/tensorflow/models)
