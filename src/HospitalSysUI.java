import javafx.application.Application;

import java.io.Closeable;
import java.sql.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.*;
public class HospitalSysUI extends Application{
	public Connection ct = null;
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	
	public void closeSys() {
		try {
			if(ct != null) ct.close();
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}catch (Exception e) {
			System.out.println("close system err!");
			e.printStackTrace();
		}
	}
	
	public void getConnect() {
		
		String dbName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=test";
		String user = "sa";
		String passwd = "1518079220";
		
		try {
			Class.forName(dbName);
			ct = DriverManager.getConnection(dbURL, user, passwd);
			System.out.println("connect database success!");
		}catch (Exception e) {
			System.out.println("connect database fail!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage priStage) {
		//设置标题
		priStage.setTitle("Hospital System");
		
		//连接数据库
		getConnect();
		
		
		LoginPane loginPane = new LoginPane(priStage, ct);
		
		
		Scene scene = new Scene(loginPane, 400, 300);
		priStage.setScene(scene);
		priStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

class LoginPane extends GridPane{
	PreparedStatement ps = null;
	ResultSet rs = null;
	public LoginPane(Stage stage, Connection ct) {
		//loginPane 设置
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25));
		
		//控件
		Text welcomeText = new Text("Welcome!");
		this.add(welcomeText, 0, 0);
		
		Label userNameLabel = new Label("username: ");
		this.add(userNameLabel, 0, 1);
		
		TextField userNameField = new TextField();
		this.add(userNameField, 1, 1);
		
		Label passwdLabel = new Label("password: ");
		this.add(passwdLabel, 0, 2);
		
		PasswordField passwdField = new PasswordField();
		this.add(passwdField, 1, 2);
		
		RadioButton patientButton = new RadioButton("Patient");
		patientButton.setContentDisplay(ContentDisplay.LEFT);
		patientButton.setSelected(true);
		this.add(patientButton, 0, 3);
		
		RadioButton doctorButton = new RadioButton("Doctor");
		doctorButton.setContentDisplay(ContentDisplay.LEFT);
		this.add(doctorButton, 1, 3);
		
		ToggleGroup typeGroup = new ToggleGroup();
		patientButton.setToggleGroup(typeGroup);
		doctorButton.setToggleGroup(typeGroup);
		
		Button loginButton = new Button("Login");
		this.add(loginButton, 0, 4);
		
		Text rstText = new Text();
		
		loginButton.setOnAction(e -> {
			//rstText.setText("success!");
			try {
				ps = ct.prepareStatement("select * from dbo.T_BRXX where brbh=?");
				ps.setString(1, userNameField.getText());
				rs = ps.executeQuery();
				
				if(rs.next()) {
					String pwd = rs.getString("dlkl");
					
					//System.out.println(userNameField.getText());
					//System.out.println(passwdField.getText());
					//System.out.println(pwd.length());
					//pwd长度为，有空格
					if(passwdField.getText().equals(pwd.trim())) {
					
						this.setVisible(false);
						RegistePane registePane = new RegistePane(stage, this.getScene());
						Scene regScene = new Scene(registePane, 600, 300);	
						stage.setScene(regScene);
					}else {
						rstText.setText("invalid username or password!");
					}
				}else {
					rstText.setText("invalid username or password!");
				}
					
			}catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				try {
					if(ps != null) ps.close();
					if(rs != null) rs.close();
				}catch (Exception fe) {
					fe.printStackTrace();
				}
			}
		});
		
		this.add(rstText, 1, 6);
	}
}

class RegistePane extends FlowPane{
	PreparedStatement ps = null;
	ResultSet rs = null;
	public RegistePane(Stage stage, Scene lastScene) {
		//设置
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		this.setVgap(20);
		this.setHgap(10);
		
		//标题控件
		FlowPane titlePane = new FlowPane();
		titlePane.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("门诊挂号");
		titleLabel.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 20));
		titlePane.getChildren().add(titleLabel);
		this.getChildren().add(titlePane);
		
		//分割线
		this.getChildren().add(new Separator(Orientation.HORIZONTAL));
		
		//输入控件
		GridPane inputPane = new GridPane();
		inputPane.setAlignment(Pos.CENTER);
		inputPane.setPadding(new Insets(15));
		inputPane.setHgap(20);
		inputPane.setVgap(10);
		
		Label ksmcLabel = new Label("科室名称");
		inputPane.add(ksmcLabel, 0, 0);
		TextField ksmcField = new TextField();
		inputPane.add(ksmcField, 1, 0);
		Label ysxmLabel = new Label("医生姓名");
		inputPane.add(ysxmLabel, 2, 0);
		TextField ysxmField = new TextField();
		inputPane.add(ysxmField, 3, 0);
		
		Label hlzbLabel = new Label("号类种别");
		inputPane.add(hlzbLabel, 0, 1);
		TextField hlzbField = new TextField();
		inputPane.add(hlzbField, 1, 1);
		Label hzmcLabel = new Label("号种名称");
		inputPane.add(hzmcLabel, 2, 1);
		TextField hzmcField = new TextField();
		inputPane.add(hzmcField, 3, 1);
		
		Label jkjeLabel = new Label("缴款金额");
		inputPane.add(jkjeLabel, 0, 2);
		TextField jkjeField = new TextField();
		inputPane.add(jkjeField, 1, 2);
		Label yjjeLabel = new Label("应缴金额");
		inputPane.add(yjjeLabel, 2, 2);
		TextField yjjeField = new TextField();
		inputPane.add(yjjeField, 3, 2);
		
		Label zljeLabel = new Label("找零金额");
		inputPane.add(zljeLabel, 0, 3);
		TextField zljeField = new TextField();
		inputPane.add(zljeField, 1, 3);
		Label ghhmLabel = new Label("挂号号码");
		inputPane.add(ghhmLabel, 2, 3);
		TextField ghhmField = new TextField();
		inputPane.add(ghhmField, 3, 3);
		
		this.getChildren().add(inputPane);
		
		//下层按钮
		FlowPane buttonFlowPane = new FlowPane();
		buttonFlowPane.setAlignment(Pos.CENTER);
		buttonFlowPane.setHgap(30);
		
		Button confirmButton = new Button("确定");
		Button clearButton = new Button("清除");
		Button exitButton = new Button("退出");
		
		confirmButton.setOnAction(e -> {
			System.out.println("click confirmButton");
			//TODO
		});
		
		clearButton.setOnAction(e -> {
			ksmcField.clear();
			ysxmField.clear();
			hlzbField.clear();
			hzmcField.clear();
			jkjeField.clear();
			yjjeField.clear();
			zljeField.clear();
			ghhmField.clear();
		});
		
		exitButton.setOnAction(e -> {
			this.setVisible(false);	
			lastScene.getRoot().setVisible(true);
			stage.setScene(lastScene);
		});
		
		buttonFlowPane.getChildren().addAll(confirmButton, clearButton, exitButton);
	
		this.getChildren().add(buttonFlowPane);
	}
}