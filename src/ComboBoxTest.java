import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;

import javax.security.auth.DestroyFailedException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.layout.*;


public class ComboBoxTest extends Application{
	
	private String[] flagTitles = {"Canada", "China", "France", "Norway", "United kingdom"};
	private ImageView[] flagImage = {
			new ImageView("image/p1.jpg"),
			new ImageView("image/p1.jpg"),
			new ImageView("image/p1.jpg"),
			new ImageView("image/p1.jpg"),
			new ImageView("image/p1.jpg")
	};
	private String[] flagDescription = new String[5];
	private DescriptionPane descriptionPane = new DescriptionPane();
	private ComboBox<String> cbo = new ComboBox<String>();
	
	
	@Override
	public void start(Stage priamaryStage) {
		flagDescription[0] = "Canada flag ...";
		flagDescription[1] = "China flag ...";
		flagDescription[2] = "France flag ...";
		flagDescription[3] = "Norway flag ...";
		flagDescription[4] = "United kingdom flag ...";
		
		setDisplay(0);
		
		BorderPane pane = new BorderPane();
		
		BorderPane paneForComBoxBorderPane = new BorderPane();
		paneForComBoxBorderPane.setLeft(new Label("select a country: "));
		paneForComBoxBorderPane.setCenter(cbo);
		pane.setTop(paneForComBoxBorderPane);
		cbo.setPrefWidth(400);
		cbo.setValue("Canada");
		
		ObservableList<String> itemStrings = FXCollections.observableArrayList(flagTitles);
		cbo.getItems().addAll(itemStrings);
		pane.setCenter(descriptionPane);
		
		cbo.setOnAction(e -> setDisplay(itemStrings.indexOf(cbo.getValue())));
		
		Scene scene = new Scene(pane, 450, 170);
		priamaryStage.setTitle("ComboBox");
		priamaryStage.setScene(scene);
		priamaryStage.show();
	}
	
	public void setDisplay(int index) {
		descriptionPane.setTitle(flagTitles[index]);
		descriptionPane.setImageView(flagImage[index]);
		descriptionPane.setDescription(flagDescription[index]);
		System.out.println(cbo.getValue());
	}
	


}

class DescriptionPane extends BorderPane{
	private Label lblImageTitle = new Label();
	private TextArea taDescription = new TextArea();
	
	public DescriptionPane() {
		lblImageTitle.setContentDisplay(ContentDisplay.TOP);
		lblImageTitle.setPrefSize(200, 100);
		lblImageTitle.setFont(new Font("SansSerif", 16));
		
		taDescription.setFont(new Font("Serif", 14));
		taDescription.setWrapText(true);
		taDescription.setEditable(false);
		
		ScrollPane scrollPane = new ScrollPane(taDescription);
		
		setLeft(lblImageTitle);
		setCenter(scrollPane);
		setPadding(new Insets(5, 5, 5, 5));
		
	}
	
	public void setTitle(String title) {
		lblImageTitle.setText(title);
	}
	public void setImageView(ImageView icon) {
		lblImageTitle.setGraphic(icon);
	}
	public void setDescription(String text) {
		taDescription.setText(text);
	}
}