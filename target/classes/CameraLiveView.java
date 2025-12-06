import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Vista principal para previsualización de cámara web.
 * Solo UI: botones, contenedores y etiquetas. Métodos vacíos.
 */
public class CameraLiveView extends BorderPane {
    private Button btnTurnOn;
    private Button btnTurnOff;

    private ImageView videoPreview;

    private Label lblClassDetected;
    private Label lblConfidence;
    private Label lblFps;

    public CameraLiveView() {
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        btnTurnOn = new Button("Encender cámara");
        btnTurnOff = new Button("Apagar cámara");

        videoPreview = new ImageView();
        videoPreview.setPreserveRatio(true);
        videoPreview.setFitWidth(960);
        videoPreview.setFitHeight(540);

        lblClassDetected = new Label("Clase detectada:");
        lblConfidence = new Label("Precisión:");
        lblFps = new Label("FPS:");
    }

    private void layoutComponents() {
        HBox topBar = new HBox(10, btnTurnOn, btnTurnOff);
        topBar.setPadding(new Insets(10));
        topBar.getStyleClass().add("top-bar");

        btnTurnOn.getStyleClass().add("primary-button");
        btnTurnOff.getStyleClass().add("secondary-button");

        // Contenedor grande para simular la previsualización del video
        StackPane previewContainer = new StackPane(videoPreview);
        previewContainer.getStyleClass().add("preview");
        previewContainer.setPadding(new Insets(8));

        HBox bottomBar = new HBox(20, lblClassDetected, lblConfidence, lblFps);
        bottomBar.setPadding(new Insets(10));
        lblClassDetected.getStyleClass().add("info-label");
        lblConfidence.getStyleClass().add("info-label");
        lblFps.getStyleClass().add("info-label");

        VBox centerBox = new VBox(8, previewContainer);
        VBox.setVgrow(previewContainer, Priority.ALWAYS);
        centerBox.setPadding(new Insets(8));

        // Encapsular contenido en tarjeta para contraste
        VBox card = new VBox(centerBox);
        card.setPadding(new Insets(6));
        card.getStyleClass().add("card-container");

        this.setTop(topBar);
        this.setCenter(card);
        this.setBottom(bottomBar);
        this.setPadding(new Insets(6));
        this.getStyleClass().add("app-root");
    }

    // Métodos vacíos (sin hilos ni OpenCV)
    public void encenderCamara() {
        // TODO: implementar encendido de cámara (vacío por ahora)
    }

    public void apagarCamara() {
        // TODO: implementar apagado de cámara (vacío por ahora)
    }

    public void actualizarEstadisticas() {
        // TODO: actualizar etiquetas de Clase/Precisión/FPS (vacío)
    }
}