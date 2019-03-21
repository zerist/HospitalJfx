import java.awt.Label;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TestFx extends Application {

	@Override
	public void start(Stage primaStage) {
		ClockPane clockPane = new ClockPane();
		String timeString = clockPane.hour + ":" + clockPane.minute + ":" + clockPane.second;
		Label label_curTimeLabel = new Label(timeString);
		
		BorderPane pane = new BorderPane();
		pane.setCenter(clockPane);
		//pane.setBottom(label_curTimeLabel);
		//BorderPane.setAlignment(label_curTimeLabel, Pos.TOP_CENTER);
		
		Scene scene = new Scene(pane, 250, 250);
		primaStage.setTitle("clock");
		primaStage.setScene(scene);
		primaStage.show();
	}

}

class ClockPane extends Pane{
	public int hour;
	public int minute;
	public int second;
	
	private double w = 250, h = 250;
	
	public ClockPane() {
		setCurrentTime();
	}
	
	public void setCurrentTime() {
		Calendar calendar = new GregorianCalendar();
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		this.second = calendar.get(Calendar.SECOND);
		paintClock();
	}
	
	public void paintClock() {
		double clockRadius = Math.min(w, h) * 0.8 * 0.5;
		double centerX = w / 2;
		double centerY = h / 2;
		
		Circle circle = new Circle(centerX, centerY, clockRadius);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);
		Text t1 = new Text(centerX - 5, centerY - clockRadius + 12, "12");
		Text t2 = new Text(centerX - clockRadius + 3, centerY + 5, "9");
		Text t3 = new Text(centerX + clockRadius - 10, centerY + 3, "3");
		Text t4 = new Text(centerX - 3, centerY + clockRadius - 3, "6");
		
		double sLength = clockRadius * 0.8;
		double secondX = centerX + sLength * Math.sin(second * (2 * Math.PI / 60));
		double secondY = centerY - sLength * Math.acos(second * (2 * Math.PI / 60));
		Line sLine = new Line(centerX, centerY, secondX, secondY);
		sLine.setStroke(Color.RED);
		
		double mLength = clockRadius * 0.65;
		double xMinute = centerX + mLength * Math.sin(minute * (2 * Math.PI / 60));
		double yMinute = centerY - mLength * Math.cos(minute * (2 * Math.PI / 60));
		Line mLine = new Line(centerX, centerY, xMinute, yMinute);
		mLine.setStroke(Color.BLUE);
		
		double hLength = clockRadius * 0.5;
		double xHour = centerX + hLength * Math.sin((hour % 12 + minute / 60.0)* (2 * Math.PI / 12));
		double yHour = centerY - hLength * Math.cos((hour % 12 + minute / 60.0) * (2 * Math.PI / 12) );
		Line hLine = new Line(centerX, centerY, xHour, yHour);
		hLine.setStroke(Color.GREEN);
		
		getChildren().clear();
		getChildren().addAll(circle, t1, t2, t3, t4, sLine, mLine, hLine);
	}
}