import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TestFx extends Application {

	@Override
	public void start(Stage primaryStage) {
		Circle circle = new Circle();
		circle.setCenterX(100);
		circle.setCenterY(100);
		circle.setRadius(50);
		circle.setStroke(Color.BLACK);
		circle.setFill(Color.GREEN);
		
		Pane pane = new HBox(10);
		pane.setPadding(new Insets(5, 5, 5, 5));
		pane.getChildren().add(circle);
		
		Image img = new Image("image/p1.jpg");
		ImageView view = new ImageView(img);
		pane.getChildren().add(view);
		
		
		Scene scene = new Scene(pane, 200, 200);
		primaryStage.setTitle("circle");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestFx a = new TestFx();
		System.out.println("134");
		System.out.println(System.getProperty("user.dir"));
		System.out.println(a.getClass().getResource("/").getPath());
		Application.launch(args);
	}

}
