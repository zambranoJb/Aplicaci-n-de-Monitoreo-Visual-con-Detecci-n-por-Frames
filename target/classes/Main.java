import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Launcher sencillo para probar las vistas UI creadas.
 * Pantalla de bienvenida con botones que abren cada vista.
 * No contiene lógica de procesamiento, sólo navegación entre escenas.
 */
public class Main extends Application {
    private Stage primaryStage;
    private Scene welcomeScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Aplicación de Monitoreo Visual - Demo UI");

        createWelcomeScene();

        primaryStage.setScene(welcomeScene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    private void createWelcomeScene() {
        Label title = new Label("Bienvenido");
        title.setFont(Font.font(28));

        Button btnImage = new Button("Abrir Image Recognition");
        Button btnVideo = new Button("Abrir Video Processing");
        Button btnCamera = new Button("Abrir Camera Live");

        btnImage.setPrefWidth(260);
        btnVideo.setPrefWidth(260);
        btnCamera.setPrefWidth(260);

        btnImage.setOnAction(e -> openImageRecognitionView());
        btnVideo.setOnAction(e -> openVideoProcessingView());
        btnCamera.setOnAction(e -> openCameraLiveView());

        HBox buttonsRow = new HBox(12, btnImage, btnVideo, btnCamera);
        buttonsRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, title, buttonsRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        welcomeScene = new Scene(root);
    }

    private void openImageRecognitionView() {
        ImageRecognitionView view = new ImageRecognitionView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));

        // Añadimos un pequeño HBox superior para el botón volver
        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));

        // Reutilizamos el BorderPane que ya tiene la vista; envolvemos en VBox
        VBox container = new VBox(top, view);
        Scene scene = new Scene(container, 1000, 700);
        primaryStage.setScene(scene);
    }

    private void openVideoProcessingView() {
        VideoProcessingView view = new VideoProcessingView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));

        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));

        VBox container = new VBox(top, view);
        Scene scene = new Scene(container, 1000, 700);
        primaryStage.setScene(scene);
    }

    private void openCameraLiveView() {
        CameraLiveView view = new CameraLiveView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));

        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));

        VBox container = new VBox(top, view);
        Scene scene = new Scene(container, 1000, 700);
        primaryStage.setScene(scene);
    }

    // Métodos vacíos o de ayuda (si necesitas personalizar comportamiento)
    public void showWelcome() {
        primaryStage.setScene(welcomeScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}