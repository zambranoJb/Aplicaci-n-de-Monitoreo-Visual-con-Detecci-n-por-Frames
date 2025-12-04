#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Script de prueba para la API de Detección de Personas
Prueba los endpoints sin necesidad de Java
"""

import requests
import json
import base64
import os
from pathlib import Path

# Configuración
API_URL = "http://localhost:5000"
IMAGEN_PRUEBA = "test_image.jpg"  # Cambia esto por tu imagen de prueba

class ColoresBash:
    """Colores para terminal"""
    VERDE = '\033[92m'
    ROJO = '\033[91m'
    AMARILLO = '\033[93m'
    AZUL = '\033[94m'
    FIN = '\033[0m'
    BOLD = '\033[1m'


def print_exito(msg):
    print(f"{ColoresBash.VERDE}✓ {msg}{ColoresBash.FIN}")


def print_error(msg):
    print(f"{ColoresBash.ROJO}✗ {msg}{ColoresBash.FIN}")


def print_info(msg):
    print(f"{ColoresBash.AZUL}ℹ {msg}{ColoresBash.FIN}")


def print_titulo(msg):
    print(f"\n{ColoresBash.BOLD}{ColoresBash.AMARILLO}{'='*50}{ColoresBash.FIN}")
    print(f"{ColoresBash.BOLD}{msg}{ColoresBash.FIN}")
    print(f"{ColoresBash.BOLD}{ColoresBash.AMARILLO}{'='*50}{ColoresBash.FIN}\n")


def test_health():
    """Prueba endpoint /health"""
    print_titulo("Probando endpoint /health")
    
    try:
        response = requests.get(f"{API_URL}/health", timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            print_exito(f"API operativa - Modelo cargado: {data['modelo_cargado']}")
            return True
        else:
            print_error(f"Error: Status {response.status_code}")
            return False
    
    except requests.exceptions.ConnectionError:
        print_error("No se pudo conectar a la API. ¿Está ejecutándose?")
        print_info(f"Ejecuta: python app.py")
        return False
    except Exception as e:
        print_error(f"Error: {e}")
        return False


def test_info():
    """Prueba endpoint /info"""
    print_titulo("Información de la API")
    
    try:
        response = requests.get(f"{API_URL}/info", timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            print_exito("Información obtenida:")
            print(f"  • Nombre: {data['nombre']}")
            print(f"  • Versión: {data['version']}")
            print(f"  • Modelo: {data['modelo']}")
            print(f"  • Dataset: {data['dataset']}")
            print(f"  • Tamaño entrada: {data['tamaño_entrada']}x{data['tamaño_entrada']}")
            return True
        else:
            print_error(f"Error: Status {response.status_code}")
            return False
    
    except Exception as e:
        print_error(f"Error: {e}")
        return False


def test_detect_archivo(ruta_imagen, umbral=0.7):
    """Prueba detección enviando archivo"""
    print_titulo(f"Detectando desde archivo: {ruta_imagen}")
    
    if not os.path.exists(ruta_imagen):
        print_error(f"Archivo no encontrado: {ruta_imagen}")
        return False
    
    try:
        with open(ruta_imagen, 'rb') as f:
            files = {'file': f}
            params = {'umbral': umbral}
            response = requests.post(
                f"{API_URL}/detect",
                files=files,
                params=params,
                timeout=30
            )
        
        if response.status_code == 200:
            data = response.json()
            
            if data['exito']:
                print_exito(f"Detección exitosa - {data['total_detecciones']} personas detectadas")
                
                for i, det in enumerate(data['detecciones'], 1):
                    print(f"\n  Persona {i}:")
                    print(f"    • Confianza: {det['confianza_porcentaje']}%")
                    print(f"    • Posición: ({det['x']}, {det['y']})")
                    print(f"    • Tamaño: {det['ancho']}x{det['alto']}")
                
                return True
            else:
                print_error(f"Error en detección: {data['error']}")
                return False
        
        else:
            print_error(f"Error HTTP {response.status_code}")
            print(response.text)
            return False
    
    except Exception as e:
        print_error(f"Error: {e}")
        return False


def test_detect_base64(ruta_imagen, umbral=0.7):
    """Prueba detección enviando base64"""
    print_titulo(f"Detectando desde base64: {ruta_imagen}")
    
    if not os.path.exists(ruta_imagen):
        print_error(f"Archivo no encontrado: {ruta_imagen}")
        return False
    
    try:
        print_info("Codificando imagen a base64...")
        
        with open(ruta_imagen, 'rb') as f:
            imagen_base64 = base64.b64encode(f.read()).decode('utf-8')
        
        payload = {
            'imagen_base64': imagen_base64,
            'umbral': umbral
        }
        
        response = requests.post(
            f"{API_URL}/detect",
            json=payload,
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            
            if data['exito']:
                print_exito(f"Detección exitosa - {data['total_detecciones']} personas detectadas")
                
                for i, det in enumerate(data['detecciones'], 1):
                    print(f"\n  Persona {i}:")
                    print(f"    • Confianza: {det['confianza_porcentaje']}%")
                    print(f"    • Posición: ({det['x']}, {det['y']})")
                    print(f"    • Tamaño: {det['ancho']}x{det['alto']}")
                
                return True
            else:
                print_error(f"Error en detección: {data['error']}")
                return False
        
        else:
            print_error(f"Error HTTP {response.status_code}")
            return False
    
    except Exception as e:
        print_error(f"Error: {e}")
        return False


def test_detect_visualizacion(ruta_imagen, umbral=0.7, ruta_salida="resultado.jpg"):
    """Prueba detección con visualización"""
    print_titulo(f"Detectando con visualización: {ruta_imagen}")
    
    if not os.path.exists(ruta_imagen):
        print_error(f"Archivo no encontrado: {ruta_imagen}")
        return False
    
    try:
        print_info("Codificando imagen a base64...")
        
        with open(ruta_imagen, 'rb') as f:
            imagen_base64 = base64.b64encode(f.read()).decode('utf-8')
        
        payload = {
            'imagen_base64': imagen_base64,
            'umbral': umbral
        }
        
        print_info("Enviando solicitud...")
        response = requests.post(
            f"{API_URL}/detect-con-visualizacion",
            json=payload,
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            
            if data['exito']:
                print_exito(f"Detección exitosa - {data['total_detecciones']} personas detectadas")
                
                # Guardar imagen resultado
                print_info("Guardando imagen con detecciones...")
                imagen_resultado_base64 = data['imagen_resultado_base64']
                imagen_bytes = base64.b64decode(imagen_resultado_base64)
                
                with open(ruta_salida, 'wb') as f:
                    f.write(imagen_bytes)
                
                print_exito(f"Imagen guardada: {ruta_salida}")
                
                for i, det in enumerate(data['detecciones'], 1):
                    print(f"\n  Persona {i}:")
                    print(f"    • Confianza: {det['confianza_porcentaje']}%")
                    print(f"    • Posición: ({det['x']}, {det['y']})")
                    print(f"    • Tamaño: {det['ancho']}x{det['alto']}")
                
                return True
            else:
                print_error(f"Error en detección: {data['error']}")
                return False
        
        else:
            print_error(f"Error HTTP {response.status_code}")
            return False
    
    except Exception as e:
        print_error(f"Error: {e}")
        return False


def main():
    print(f"\n{ColoresBash.BOLD}{ColoresBash.AZUL}{'='*50}")
    print("   PRUEBAS API DETECCIÓN DE PERSONAS")
    print(f"   URL: {API_URL}")
    print(f"{'='*50}{ColoresBash.FIN}\n")
    
    # Pruebas básicas
    if not test_health():
        return
    
    test_info()
    
    # Pruebas con imagen (si existe)
    if os.path.exists(IMAGEN_PRUEBA):
        print_info(f"Archivo de prueba encontrado: {IMAGEN_PRUEBA}")
        
        test_detect_archivo(IMAGEN_PRUEBA, umbral=0.7)
        test_detect_base64(IMAGEN_PRUEBA, umbral=0.7)
        test_detect_visualizacion(IMAGEN_PRUEBA, umbral=0.7, ruta_salida="resultado_prueba.jpg")
    else:
        print_info(f"Coloca una imagen en '{IMAGEN_PRUEBA}' para pruebas completas")
    
    print(f"\n{ColoresBash.BOLD}{ColoresBash.VERDE}✓ Pruebas completadas{ColoresBash.FIN}\n")


if __name__ == '__main__':
    main()
