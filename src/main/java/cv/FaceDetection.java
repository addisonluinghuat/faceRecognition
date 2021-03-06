package cv;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@Configuration
@ComponentScan(basePackages = "com.utils")
@PropertySource(value = { "classpath:config.properties" })
public class FaceDetection extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceDetection.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			// set a whitesmoke background
			root.setStyle("-fx-background-color: whitesmoke;");
			// create and style a scene
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Face Detection and Tracking");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

			// init the controller
			FaceDetectionController controller = loader.getController();
			controller.init();

			// set the proper behavior on closing the application
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Loader.load(opencv_java.class);
		launch(args);
	}
}
