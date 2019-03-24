import javafx.application.Application;

import java.io.Closeable;
import java.sql.*;

import javax.swing.ButtonModel;

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
import javafx.scene.control.cell.*;
import javafx.beans.property.*;
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
	
	//析构函数
	protected void finalize() {
		closeSys();
		System.out.println("system exit!");
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
		Scene scene = new Scene(loginPane, 600, 300);
		
		RegistePane registerPane = new RegistePane(priStage, loginPane, ct);
		registerPane.setVisible(false);
		loginPane.registerPane = registerPane;
		
		DoctorPane doctorPane = new DoctorPane(priStage, loginPane, ct);
		doctorPane.setVisible(false);
		loginPane.doctorPane = doctorPane;
		
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
	RegistePane registerPane = null;
	DoctorPane doctorPane = null;
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
				//String tableName = "dbo.T_BRXX";
				//String bhName = "brbh";
				
				
				//String sql = "select * from ? where ?=?";
				//这样不知道为什么不行，会报lambda@p0错误
				String sql = "select * from dbo.T_BRXX where brbh=?";
				int type = 0; //病人
				if(patientButton.isSelected()) {
					//tableName = "dbo.T_BRXX";
					//bhName = "brbh";
					sql = "select * from dbo.T_BRXX where brbh=?";
					type = 0;
				}else {
					//tableName = "dbo.T_KSYS";
					//bhName = "ysbh";
					sql = "select * from dbo.T_KSYS where ysbh=?";
					type = 1;	//医生
				}
				
				ps = ct.prepareStatement(sql);
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
						//RegistePane registePane = new RegistePane(stage, this.getScene());
						//Scene regScene = new Scene(registePane, 600, 300);
						Scene scene = this.getScene();
						if(type == 0) {
							registerPane.username = userNameField.getText().trim();
							scene.setRoot(registerPane);
							registerPane.setVisible(true);
						}
						if(type == 1) {
							doctorPane.username = userNameField.getText().trim();
							scene.setRoot(doctorPane);
							doctorPane.setVisible(true);
						}
						stage.setScene(scene);
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
					if(rs != null) rs.close();
					if(ps != null) ps.close();
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
	String username = null;
	public RegistePane(Stage stage, LoginPane lastPane, Connection ct) {
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
			Scene scene = this.getScene();
			scene.setRoot(lastPane);
			lastPane.setVisible(true);
			stage.setScene(scene);
		});
		
		buttonFlowPane.getChildren().addAll(confirmButton, clearButton, exitButton);
	
		this.getChildren().add(buttonFlowPane);
	}
}

class DoctorPane extends FlowPane{
	PreparedStatement ps = null;
	ResultSet rs = null;
	String username = null;
	
	private final TableView<Patient> patientTableView = new TableView<Patient>();
	private final ObservableList<Patient> patientDataList = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public DoctorPane(Stage stage, LoginPane lastPane, Connection ct) {
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		this.setVgap(10);
		this.setPadding(new Insets(15));
		
		//按钮模拟菜单选项卡
		FlowPane titlePane = new FlowPane();
		titlePane.setAlignment(Pos.TOP_LEFT);
		titlePane.setHgap(5);
		titlePane.setPadding(new Insets(10));
		
		Button brButton = new Button("病人列表");
		brButton.setOnAction(e -> {
			System.out.println("brbutton clicked");
			//TODO
		});
		
		Button srButton = new Button("收入列表");
		srButton.setOnAction(e -> {
			System.out.println("srButton clicked");
		});
		
		Button exitButton = new Button("退出系统");
		exitButton.setOnAction(e -> {
			this.setVisible(false);
			Scene scene = this.getScene();
			scene.setRoot(lastPane);
			lastPane.setVisible(true);
			stage.setScene(scene);
		});
		
		titlePane.getChildren().addAll(brButton, srButton, exitButton);
		this.getChildren().add(titlePane);
		
		//分割线
		Separator separator = new Separator(Orientation.HORIZONTAL);
		this.getChildren().add(separator);
		
		//tableView 病人列表
			//获取数据
		try {
			String sql = "select dbo.T_GHXX.ghbh,dbo.T_BRXX.brmc,dbo.T_GHXX.rqsj,dbo.T_HZXX.hzmc " + 
					"from dbo.T_GHXX,dbo.T_BRXX,dbo.T_HZXX " + 
					"where dbo.T_GHXX.ysbh = ? and dbo.T_BRXX.brbh = dbo.T_GHXX.brbh and dbo.T_GHXX.hzbh = dbo.T_HZXX.hzbh";
			ps = ct.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()) {
				patientDataList.add(new Patient(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			}catch (Exception fe) {
				fe.printStackTrace();
			}
		}
		
			//构建界面
		TableColumn<Patient, String> ghbhColumn = new TableColumn<>("挂号编号");
		ghbhColumn.setMinWidth(100);
		ghbhColumn.setCellValueFactory(new PropertyValueFactory<>("ghbh"));
		
		TableColumn<Patient, String> brmcColumn = new TableColumn<>("病人名称");
		brmcColumn.setMinWidth(100);
		brmcColumn.setCellValueFactory(new PropertyValueFactory<>("brmc"));
		
		TableColumn<Patient, String> ghrqsjColumn = new TableColumn<>("挂号日期时间");
		ghrqsjColumn.setMinWidth(100);
		ghrqsjColumn.setCellValueFactory(new PropertyValueFactory<>("ghrqsj"));
		
		TableColumn<Patient, String> hlzbColumn = new TableColumn<>("号类种别");
		hlzbColumn.setMinWidth(100);
		hlzbColumn.setCellValueFactory(new PropertyValueFactory<>("hlzb"));
		
		patientTableView.setItems(patientDataList);
		patientTableView.getColumns().addAll(ghbhColumn, brmcColumn, ghrqsjColumn, hlzbColumn);
		
		
		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10,0,0,10));
		vbox.getChildren().addAll(patientTableView);
		
		this.getChildren().add(vbox);
		
	}
	
	
	//病人数据映射
	public static class Patient{
		private final SimpleStringProperty ghbh;
		private final SimpleStringProperty brmc;
        private final SimpleStringProperty ghrqsj;
        private final SimpleStringProperty hlzb;
        
 
        private Patient(String ghbh, String brmc, String ghrqsj, String hlzb) {
            this.ghbh = new SimpleStringProperty(ghbh);
            this.brmc = new SimpleStringProperty(brmc);
            this.ghrqsj = new SimpleStringProperty(ghrqsj);
            this.hlzb = new SimpleStringProperty(hlzb);
        }
 
        public String getghbh() {
            return ghbh.get();
        }
 
        public void setghbh(String fName) {
            ghbh.set(fName);
        }
 
        public String getbrmc() {
            return brmc.get();
        }
 
        public void setbrmc(String fName) {
            brmc.set(fName);
        }
        
        public String getghrqsj() {
            return ghrqsj.get();
        }
 
        public void setghrqsj(String fName) {
            ghrqsj.set(fName);
        }
        
        public String gethlzb() {
            return hlzb.get();
        }
 
        public void sethlzb(String fName) {
            hlzb.set(fName);
        }
	}
}

