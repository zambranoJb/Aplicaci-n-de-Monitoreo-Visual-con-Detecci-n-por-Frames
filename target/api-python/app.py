# -*- coding: utf-8 -*-
"""API REST para detección de personas usando SSD MobileNet

Flask API que recibe imágenes y devuelve detecciones de personas
en formato JSON para ser consumida por sistemas externos (Java, etc.)
"""

import cv2
import numpy as np
import os
from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import base64
from io import BytesIO

# Configuración
app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # Máximo 16MB por imagen

# Rutas de los modelos (ajusta estas rutas según tu entorno)
RUTA_MODELO = "modelos/frozen_inference_graph.pb"
RUTA_CONFIGURACION = "modelos/ssd_mobilenet_v2_coco_2018_03_29.pbtxt"
RUTA_CLASES = "modelos/object_detection_classes_coco.txt"
TAMAÑO_IMAGEN = 300

# Carpeta para archivos temporales
UPLOAD_FOLDER = 'uploads'
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

# Cargar el modelo una sola vez al iniciar
try:
    modelo = cv2.dnn.readNetFromTensorflow(RUTA_MODELO, RUTA_CONFIGURACION)
    print("✓ Modelo cargado exitosamente")
except Exception as e:
    print(f"✗ Error cargando modelo: {e}")
    modelo = None

# Cargar clases
try:
    with open(RUTA_CLASES, 'r') as f:
        clases = [linea.strip() for linea in f.readlines()]
    print(f"✓ {len(clases)} clases cargadas")
except Exception as e:
    print(f"✗ Error cargando clases: {e}")
    clases = []


def detectar_personas(imagen_cv2, umbral_confianza=0.7):
    """
    Detecta personas en una imagen
    
    Args:
        imagen_cv2: Imagen en formato OpenCV (BGR)
        umbral_confianza: Umbral mínimo de confianza (0-1)
    
    Returns:
        Lista de detecciones con formato:
        {
            'clase': 'persona',
            'confianza': 0.95,
            'x': 100,
            'y': 150,
            'ancho': 200,
            'alto': 300,
            'clase_id': 1
        }
    """
    if modelo is None:
        return None, "Modelo no cargado"
    
    detecciones = []
    
    try:
        # Crear blob de la imagen
        blob = cv2.dnn.blobFromImage(
            imagen_cv2, 1.0,
            size=(TAMAÑO_IMAGEN, TAMAÑO_IMAGEN),
            mean=(0, 0, 0),
            swapRB=True,
            crop=False
        )
        
        # Realizar detección
        modelo.setInput(blob)
        resultados = modelo.forward()
        
        filas_imagen = imagen_cv2.shape[0]
        columnas_imagen = imagen_cv2.shape[1]
        total_detecciones = resultados.shape[2]
        
        # Procesar resultados
        for i in range(total_detecciones):
            clase_id = int(resultados[0, 0, i, 1])
            confianza = float(resultados[0, 0, i, 2])
            
            # Solo procesar si cumple el umbral y es clase 1 (persona en COCO)
            if confianza >= umbral_confianza and clase_id == 1:
                x = int(resultados[0, 0, i, 3] * columnas_imagen)
                y = int(resultados[0, 0, i, 4] * filas_imagen)
                ancho = int(resultados[0, 0, i, 5] * columnas_imagen - x)
                alto = int(resultados[0, 0, i, 6] * filas_imagen - y)
                
                detecciones.append({
                    'clase': 'persona',
                    'clase_id': clase_id,
                    'confianza': round(float(confianza), 4),
                    'confianza_porcentaje': round(float(confianza) * 100, 2),
                    'x': int(x),
                    'y': int(y),
                    'ancho': int(ancho),
                    'alto': int(alto)
                })
        
        return detecciones, None
    
    except Exception as e:
        return None, str(e)


def dibujar_detecciones(imagen_cv2, detecciones):
    """
    Dibuja rectángulos y etiquetas en la imagen
    
    Args:
        imagen_cv2: Imagen en formato OpenCV (BGR)
        detecciones: Lista de detecciones
    
    Returns:
        Imagen con detecciones dibujadas
    """
    imagen_copia = imagen_cv2.copy()
    
    for det in detecciones:
        x, y = det['x'], det['y']
        ancho, alto = det['ancho'], det['alto']
        confianza = det['confianza_porcentaje']
        
        # Dibujar rectángulo (cyan)
        cv2.rectangle(imagen_copia, (x, y), (x + ancho, y + alto), (255, 255, 0), 2)
        
        # Dibujar etiqueta
        texto = f"Persona: {confianza}%"
        font = cv2.FONT_HERSHEY_SIMPLEX
        font_scale = 0.5
        thickness = 1
        
        text_size, baseline = cv2.getTextSize(texto, font, font_scale, thickness)
        cv2.rectangle(
            imagen_copia,
            (x, y - text_size[1] - baseline),
            (x + text_size[0], y + baseline),
            (255, 165, 0),
            cv2.FILLED
        )
        cv2.putText(
            imagen_copia,
            texto,
            (x, y - 5),
            font,
            font_scale,
            (255, 255, 255),
            thickness,
            cv2.LINE_AA
        )
    
    return imagen_copia


# ==================== ENDPOINTS ====================

@app.route('/health', methods=['GET'])
def health():
    """Verifica que la API esté operativa"""
    return jsonify({
        'status': 'ok',
        'modelo_cargado': modelo is not None
    }), 200


@app.route('/detect', methods=['POST'])
def detect():
    """
    Endpoint principal para detectar personas en una imagen
    
    Métodos soportados:
    1. Enviar archivo de imagen: Content-Type: multipart/form-data
       Parámetros: file, umbral (opcional)
    
    2. Enviar imagen en base64: Content-Type: application/json
       JSON: { "imagen_base64": "...", "umbral": 0.7 }
    
    Respuesta:
    {
        "exito": true,
        "total_detecciones": 2,
        "umbral_utilizado": 0.7,
        "detecciones": [
            {
                "clase": "persona",
                "confianza": 0.95,
                "confianza_porcentaje": 95.0,
                "x": 100,
                "y": 150,
                "ancho": 200,
                "alto": 300
            }
        ]
    }
    """
    
    if modelo is None:
        return jsonify({'exito': False, 'error': 'Modelo no cargado'}), 500
    
    imagen = None
    umbral = 0.7
    
    try:
        # Opción 1: Archivo de imagen
        if 'file' in request.files:
            archivo = request.files['file']
            if archivo.filename == '':
                return jsonify({'exito': False, 'error': 'No se seleccionó archivo'}), 400
            
            # Leer archivo
            contenido_archivo = archivo.read()
            imagen_array = np.frombuffer(contenido_archivo, np.uint8)
            imagen = cv2.imdecode(imagen_array, cv2.IMREAD_COLOR)
            
            if imagen is None:
                return jsonify({'exito': False, 'error': 'No se pudo decodificar la imagen'}), 400
        
        # Opción 2: Base64
        elif request.is_json:
            datos = request.get_json()
            if 'imagen_base64' not in datos:
                return jsonify({'exito': False, 'error': 'Falta parámetro imagen_base64'}), 400
            
            try:
                imagen_data = base64.b64decode(datos['imagen_base64'])
                imagen_array = np.frombuffer(imagen_data, np.uint8)
                imagen = cv2.imdecode(imagen_array, cv2.IMREAD_COLOR)
            except Exception as e:
                return jsonify({'exito': False, 'error': f'Error decodificando base64: {str(e)}'}), 400
        
        else:
            return jsonify({'exito': False, 'error': 'Envía imagen como archivo o base64'}), 400
        
        # Obtener umbral si se proporciona
        if 'umbral' in request.values:
            try:
                umbral = float(request.values['umbral'])
                if not 0 <= umbral <= 1:
                    umbral = 0.7
            except:
                pass
        
        # Realizar detección
        detecciones, error = detectar_personas(imagen, umbral)
        
        if error:
            return jsonify({'exito': False, 'error': error}), 500
        
        return jsonify({
            'exito': True,
            'total_detecciones': len(detecciones),
            'umbral_utilizado': umbral,
            'detecciones': detecciones
        }), 200
    
    except Exception as e:
        return jsonify({'exito': False, 'error': str(e)}), 500


@app.route('/detect-con-visualizacion', methods=['POST'])
def detect_con_visualizacion():
    """
    Endpoint que devuelve la imagen con las detecciones dibujadas en base64
    
    Parámetros: igual que /detect
    
    Respuesta adicional:
    {
        ...detecciones...,
        "imagen_resultado_base64": "iVBORw0KGgoAAAANSUhEUgAAA..."
    }
    """
    
    if modelo is None:
        return jsonify({'exito': False, 'error': 'Modelo no cargado'}), 500
    
    imagen = None
    umbral = 0.7
    
    try:
        # Opción 1: Archivo
        if 'file' in request.files:
            archivo = request.files['file']
            contenido_archivo = archivo.read()
            imagen_array = np.frombuffer(contenido_archivo, np.uint8)
            imagen = cv2.imdecode(imagen_array, cv2.IMREAD_COLOR)
        
        # Opción 2: Base64
        elif request.is_json:
            datos = request.get_json()
            if 'imagen_base64' not in datos:
                return jsonify({'exito': False, 'error': 'Falta parámetro imagen_base64'}), 400
            
            imagen_data = base64.b64decode(datos['imagen_base64'])
            imagen_array = np.frombuffer(imagen_data, np.uint8)
            imagen = cv2.imdecode(imagen_array, cv2.IMREAD_COLOR)
        
        if imagen is None:
            return jsonify({'exito': False, 'error': 'No se pudo decodificar la imagen'}), 400
        
        # Obtener umbral
        if 'umbral' in request.values:
            try:
                umbral = float(request.values['umbral'])
            except:
                pass
        
        # Detectar
        detecciones, error = detectar_personas(imagen, umbral)
        if error:
            return jsonify({'exito': False, 'error': error}), 500
        
        # Dibujar detecciones
        imagen_resultado = dibujar_detecciones(imagen, detecciones)
        
        # Convertir a base64
        _, imagen_encoded = cv2.imencode('.jpg', imagen_resultado)
        imagen_base64 = base64.b64encode(imagen_encoded).decode('utf-8')
        
        return jsonify({
            'exito': True,
            'total_detecciones': len(detecciones),
            'umbral_utilizado': umbral,
            'detecciones': detecciones,
            'imagen_resultado_base64': imagen_base64
        }), 200
    
    except Exception as e:
        return jsonify({'exito': False, 'error': str(e)}), 500


@app.route('/info', methods=['GET'])
def info():
    """Información de la API"""
    return jsonify({
        'nombre': 'API Detección de Personas',
        'version': '1.0',
        'modelo': 'SSD MobileNet v2',
        'dataset': 'COCO',
        'tamaño_entrada': TAMAÑO_IMAGEN,
        'endpoints': {
            '/health': 'GET - Verifica disponibilidad',
            '/detect': 'POST - Detecta personas en imagen',
            '/detect-con-visualizacion': 'POST - Detecta y devuelve imagen con dibujos',
            '/info': 'GET - Esta información'
        }
    }), 200


# ==================== MAIN ====================

if __name__ == '__main__':
    print("\n" + "="*50)
    print("🚀 API Detección de Personas iniciada")
    print("="*50)
    print(f"📍 Servidor en: http://localhost:5000")
    print(f"📚 Documentación: http://localhost:5000/info")
    print("="*50 + "\n")
    
    app.run(debug=True, host='0.0.0.0', port=5000)
