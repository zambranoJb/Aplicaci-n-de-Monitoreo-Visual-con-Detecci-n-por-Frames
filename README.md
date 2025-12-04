Instrucciones para compilar y ejecutar (Windows - PowerShell)

Requisitos:
- JDK 11+ (se recomienda 17 o 20).
- JavaFX SDK correspondiente a tu JDK (descargar desde https://openjfx.io).

Pasos rápidos (ejemplo usando la ruta que tienes: `C:\javafx-sdk-25\javafx-sdk-25.0.1\lib`)

1) Compilar (abre PowerShell en la carpeta del proyecto):

```powershell
cd "C:\Users\jonat\OneDrive\Desktop\Aplicación de Monitoreo Visual con Detección por Frames" 
Guía rápida y muy simple para charly (Windows PowerShell)

Qué necesitas
- Java instalado (JDK 11 o superior).
- JavaFX descargado y descomprimido. Ejemplo de ruta que tú usas: `C:\javafx-sdk-25\javafx-sdk-25.0.1\lib`.

Pasos (copia y pega en PowerShell)

1) Ve a la carpeta del proyecto y crea la carpeta `out`:

```powershell
cd "C:\Users\jonat\OneDrive\Desktop\Aplicación de Monitoreo Visual con Detección por Frames"
mkdir out
```

2) Dile a PowerShell dónde está JavaFX (cambia la ruta si la guardaste en otro lugar):

```powershell
$env:JAVAFX = "C:\javafx-sdk-25\javafx-sdk-25.0.1\lib"
```

3) Compilar (crea los archivos listos para ejecutar):

```powershell
javac --module-path $env:JAVAFX --add-modules javafx.controls,javafx.fxml -d out src\\*.java
```

4) Ejecutar la aplicación:

```powershell
java --module-path $env:JAVAFX --add-modules javafx.controls,javafx.fxml -cp out Main
```

Consejos rápidos
- Si ves un error que dice que falta `javafx.controls` o `javafx.fxml`, revisa que la ruta en `$env:JAVAFX` sea la correcta y que exista la carpeta `lib` dentro del SDK.
- Si el compilador muestra una advertencia "uses unchecked or unsafe operations", no te preocupes: es solo una advertencia y no impide que la app funcione.

Usar una imagen de fondo personalizada
- Crea una carpeta `img` dentro del proyecto (si aún no existe) y coloca la imagen que quieres usar como fondo.
- Nombra la imagen `background.jpg` (ruta esperada: `img/background.jpg`). El CSS carga `file:img/background.jpg` por defecto.
- Si prefieres usar otra ubicación o nombre, edita `src/styles.css` y cambia la URL en la regla `.app-root`.

Ejemplo (PowerShell) para crear la carpeta y copiar una imagen desde otra ruta:
```powershell
cd "C:\Users\jonat\OneDrive\Desktop\Aplicación de Monitoreo Visual con Detección por Frames"
mkdir img
Copy-Item "C:\ruta\a\tu\imagen.jpg" -Destination .\img\background.jpg
``` 

cd "C:\Users\jonat\OneDrive\Desktop\Aplicación de Monitoreo Visual con Detección por Frames"
$env:JAVAFX = "C:\javafx-sdk-25\javafx-sdk-25.0.1\lib"   # ajusta la ruta a tu SDK
javac --module-path $env:JAVAFX --add-modules javafx.controls,javafx.fxml -d out src\*.java
java --module-path $env:JAVAFX --add-modules javafx.controls,javafx.fxml -cp out Main


