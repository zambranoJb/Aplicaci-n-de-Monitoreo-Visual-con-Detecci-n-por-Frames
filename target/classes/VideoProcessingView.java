import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Maqueta visual para procesamiento de video.
 * Contiene botones, tabla de resultados y vista de frame.
 * Sin lógica ni procesamiento: solo UI.
 */
public class VideoProcessingView extends BorderPane {
    private Button btnSelectVideo;
    private Button btnProcessVideo;

    private TableView<FrameResult> table;
    private ImageView frameView;

    public VideoProcessingView() {
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        btnSelectVideo = new Button("Seleccionar video");
        btnProcessVideo = new Button("Procesar video");

        table = new TableView<>();
        TableColumn<FrameResult, String> colFrame = new TableColumn<>("Frame");
        colFrame.setCellValueFactory(new PropertyValueFactory<FrameResult, String>("frame"));

        TableColumn<FrameResult, String> colClase = new TableColumn<>("Clase");
        colClase.setCellValueFactory(new PropertyValueFactory<FrameResult, String>("clase"));

        TableColumn<FrameResult, String> colPrecision = new TableColumn<>("Precisión");
        colPrecision.setCellValueFactory(new PropertyValueFactory<FrameResult, String>("precision"));

        table.getColumns().addAll(colFrame, colClase, colPrecision);
        table.setPrefWidth(360);

        frameView = new ImageView();
        frameView.setPreserveRatio(true);
        frameView.setFitWidth(640);
        frameView.setFitHeight(480);
    }

    private void layoutComponents() {
        VBox leftPane = new VBox(8, table);
        leftPane.setPadding(new Insets(10));

        VBox rightPane = new VBox(10, new VBox(8, btnSelectVideo, btnProcessVideo), new Label("Frame seleccionado:"),
                frameView);
        rightPane.setPadding(new Insets(10));

        SplitPane split = new SplitPane(leftPane, rightPane);
        split.setDividerPositions(0.35);

        this.setCenter(split);
        this.setPadding(new Insets(8));
    }

    // Métodos vacíos
    public void seleccionarVideo() {
        // TODO: implementar selección de video
    }

    public void procesarVideo() {
        // TODO: implementar procesamiento de video
    }

    public void onFrameSelected() {
        // TODO: manejar selección de fila/ frame (vacío)
    }

    // Modelo simple para la tabla (sin lógica de negocio)
    public static class FrameResult {
        private String frame;
        private String clase;
        private String precision;

        public FrameResult() {
            this.frame = "";
            this.clase = "";
            this.precision = "";
        }

        public String getFrame() {
            return frame;
        }

        public void setFrame(String frame) {
            this.frame = frame;
        }

        public String getClase() {
            return clase;
        }

        public void setClase(String clase) {
            this.clase = clase;
        }

        public String getPrecision() {
            return precision;
        }

        public void setPrecision(String precision) {
            this.precision = precision;
        }
    }
}