import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Modality;
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
        title.getStyleClass().add("title-welcome");

        Button btnImage = new Button("Abrir Image Recognition");
        Button btnVideo = new Button("Abrir Video Processing");
        Button btnCamera = new Button("Abrir Camera Live");

        btnImage.setPrefWidth(260);
        btnVideo.setPrefWidth(260);
        btnCamera.setPrefWidth(260);

        btnImage.getStyleClass().add("primary-button");
        btnVideo.getStyleClass().add("secondary-button");
        btnCamera.getStyleClass().add("secondary-button");
        // Aplicar estilo moderno y más visible al botón de Camera Live
        btnCamera.getStyleClass().add("card-button");

        btnImage.setOnAction(e -> openImageRecognitionView());
        btnVideo.setOnAction(e -> openVideoProcessingView());
        btnCamera.setOnAction(e -> openCameraLiveView());

        HBox buttonsRow = new HBox(12, btnImage, btnVideo, btnCamera);
        buttonsRow.setAlignment(Pos.CENTER);

        VBox card = new VBox(14, buttonsRow);
        card.getStyleClass().add("card-container");

        VBox content = new VBox(20, title, card);
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.getStyleClass().add("app-root");
        root.getStyleClass().add("welcome-root");
        root.setPadding(new Insets(20));

        Region overlay = new Region();
        overlay.getStyleClass().add("welcome-overlay");

        // Botón de créditos en la esquina superior derecha
        Button btnCredits = new Button("CREDITOS");
        btnCredits.getStyleClass().addAll("card-button", "small");
        btnCredits.setOnAction(e -> showCreditsModal());

        // Crear la escena primero para poder vincular el overlay
        welcomeScene = new Scene(root, 1000, 700);
        // Bind overlay al tamaño de la escena
        overlay.prefWidthProperty().bind(welcomeScene.widthProperty());
        overlay.prefHeightProperty().bind(welcomeScene.heightProperty());

        root.getChildren().addAll(overlay, content, btnCredits);
        StackPane.setAlignment(btnCredits, Pos.TOP_RIGHT);
        StackPane.setMargin(btnCredits, new Insets(16));

        // Cargar hoja de estilos localizada en el proyecto (desarrollo)
        welcomeScene.getStylesheets().add("file:src/styles.css");
    }

    private void openImageRecognitionView() {
        ImageRecognitionView view = new ImageRecognitionView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));
        btnBack.getStyleClass().add("secondary-button");

        // Añadimos un pequeño HBox superior para el botón volver
        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));
        top.getStyleClass().add("top-bar");

        // Reutilizamos el BorderPane que ya tiene la vista; envolvemos en VBox
        VBox container = new VBox(top, view);
        container.getStyleClass().add("app-root");
        Scene scene = new Scene(container, 1000, 700);
        scene.getStylesheets().add("file:src/styles.css");
        primaryStage.setScene(scene);
    }

    private void openVideoProcessingView() {
        VideoProcessingView view = new VideoProcessingView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));
        btnBack.getStyleClass().add("secondary-button");

        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));
        top.getStyleClass().add("top-bar");

        VBox container = new VBox(top, view);
        container.getStyleClass().add("app-root");
        Scene scene = new Scene(container, 1000, 700);
        scene.getStylesheets().add("file:src/styles.css");
        primaryStage.setScene(scene);
    }

    private void openCameraLiveView() {
        CameraLiveView view = new CameraLiveView();
        Button btnBack = new Button("Volver");
        btnBack.setOnAction(e -> primaryStage.setScene(welcomeScene));
        btnBack.getStyleClass().add("secondary-button");

        HBox top = new HBox(btnBack);
        top.setPadding(new Insets(8));
        top.getStyleClass().add("top-bar");

        VBox container = new VBox(top, view);
        container.getStyleClass().add("app-root");
        Scene scene = new Scene(container, 1000, 700);
        scene.getStylesheets().add("file:src/styles.css");
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