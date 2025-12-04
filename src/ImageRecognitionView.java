import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Interfaz visual para carga y visualización de imágenes.
 * Solo UI: sin lógica, sin llamadas a APIs, métodos vacíos con TODO.
 */
public class ImageRecognitionView extends BorderPane {
    private Button btnLoadImage;
    private Button btnSendToApi;
    private ImageView imageView;
    private Label lblClassDetected;
    private Label lblConfidence;
    private Canvas boundingCanvas;

    public ImageRecognitionView() {
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        btnLoadImage = new Button("Cargar imagen");
        btnSendToApi = new Button("Enviar a API");

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(700);
        imageView.setFitHeight(450);

        lblClassDetected = new Label("Clase detectada:");
        lblConfidence = new Label("Precisión:");

        // Canvas vacío donde se dibujarán bounding boxes en el futuro
        boundingCanvas = new Canvas(700, 450);
    }

    private void layoutComponents() {
        HBox topBar = new HBox(10, btnLoadImage, btnSendToApi);
        topBar.setPadding(new Insets(10));

        VBox rightBox = new VBox(8, lblClassDetected, lblConfidence);
        rightBox.setPadding(new Insets(10));
        rightBox.setPrefWidth(220);

        // ImageView y Canvas superpuestos
        StackPane imageStack = new StackPane(imageView, boundingCanvas);
        ScrollPane centerScroll = new ScrollPane(imageStack);
        centerScroll.setFitToWidth(true);
        centerScroll.setFitToHeight(true);

        this.setTop(topBar);
        this.setCenter(centerScroll);
        this.setRight(rightBox);
        this.setPadding(new Insets(8));
    }

    // Métodos vacíos (sin lógica)
    public void cargarImagen() {
        // TODO: implementar carga de imagen (vacío por ahora)
    }

    public void enviarAApi() {
        // TODO: implementar envío a API (vacío por ahora)
    }
}