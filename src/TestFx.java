import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
public class TestFx extends Application {

	@Override
	public void start(Stage promaryStage) {
		Button btOK = new Button("ok");
		Scene scene = new Scene(btOK, 200, 250);
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("1");
		Application.launch(args);
	}

}
