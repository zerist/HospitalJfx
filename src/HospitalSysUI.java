import javafx.application.Application;
import java.io.Closeable;
import java.sql.*;

import javax.script.SimpleScriptContext;

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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
public class HospitalSysUI extends Application{
	public Connection ct = null;
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	
	public void closeSys() {
		try {
			if(rs != null) rs.close();
			if(ps != null) ps.close();
			if(ct != null) ct.close();	
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
		Scene scene = new Scene(loginPane, 750, 500);
		
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
							registerPane.username.setValue(userNameField.getText().trim());
							scene.setRoot(registerPane);
							registerPane.setVisible(true);
						}
						if(type == 1) {
							doctorPane.username.setValue(userNameField.getText().trim());
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
	StringProperty username = new SimpleStringProperty();
	
	//保存combobox的值
	StringProperty ksmcProperty = new SimpleStringProperty();
	StringProperty ysxmProperty = new SimpleStringProperty();
	StringProperty hlzbProperty = new SimpleStringProperty();
	StringProperty hzmcProperty = new SimpleStringProperty();
	
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
		final ComboBox<String> ksmcField = new ComboBox<String>();
		ksmcField.setPromptText("输入科室名称");
		ksmcField.setEditable(true);
		inputPane.add(ksmcField, 1, 0);
		
		Label ysxmLabel = new Label("医生姓名");
		inputPane.add(ysxmLabel, 2, 0);
		final ComboBox<String> ysxmField = new ComboBox<String>();
		ysxmField.setPromptText("输入医生姓名");
		ysxmField.setEditable(true);
		inputPane.add(ysxmField, 3, 0);
		
		Label hlzbLabel = new Label("号类种别");
		inputPane.add(hlzbLabel, 0, 1);
		final ComboBox<String> hlzbField = new ComboBox<String>();
		hlzbField.setPromptText("输入号类种别");
		hlzbField.setEditable(true);
		//hlzbField.getItems().addAll("专家", "普通");
		inputPane.add(hlzbField, 1, 1);
		
		Label hzmcLabel = new Label("号种名称");
		inputPane.add(hzmcLabel, 2, 1);
		final ComboBox<String> hzmcField = new ComboBox<String>();
		hzmcField.setPromptText("输入号种名称");
		hzmcField.setEditable(true);
		inputPane.add(hzmcField, 3, 1);
		
		Label jkjeLabel = new Label("缴款金额");
		inputPane.add(jkjeLabel, 0, 2);
		TextField jkjeField = new TextField();
		jkjeField.setEditable(false);
		inputPane.add(jkjeField, 1, 2);
		Label yjjeLabel = new Label("应缴金额");
		inputPane.add(yjjeLabel, 2, 2);
		TextField yjjeField = new TextField();
		yjjeField.setEditable(false);
		inputPane.add(yjjeField, 3, 2);
		
		Label zljeLabel = new Label("找零金额");
		inputPane.add(zljeLabel, 0, 3);
		TextField zljeField = new TextField();
		zljeField.setEditable(false);
		inputPane.add(zljeField, 1, 3);
		Label ghhmLabel = new Label("挂号号码");
		inputPane.add(ghhmLabel, 2, 3);
		TextField ghhmField = new TextField();
		ghhmField.setEditable(false);
		inputPane.add(ghhmField, 3, 3);
		
		
		username.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				try {
					ps = ct.prepareStatement("select ycje from dbo.T_BRXX where brbh = ?");
					ps.setString(1, username.getValue());
					rs = ps.executeQuery();
					rs.next();
					jkjeField.setText(rs.getString(1));
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		ksmcField.setOnAction(e ->{
			if(ksmcField.getSelectionModel().getSelectedItem() != null) {
				ksmcProperty.set(ksmcField.getSelectionModel().getSelectedItem().toString().trim());
				System.out.println(ksmcProperty.get());
			}
			try {
				String sqlString = "select ksbh from dbo.T_KSXX where ksmc=?";
				ps = ct.prepareStatement(sqlString);
				ps.setString(1, ksmcProperty.get());
				rs = ps.executeQuery();
				if(rs.next()) {
					String ksbhString = rs.getString(1);
					
					//获取医生名称
					ysxmField.getItems().clear();
					ps = ct.prepareStatement("select ysmc from dbo.T_KSYS where ksbh = ?");
					ps.setString(1, ksbhString);
					rs = ps.executeQuery();
					while(rs.next()) {
						ysxmField.getItems().add(rs.getString(1));
					}
				}
				
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		ysxmField.setOnAction(e -> {
			if(ysxmField.getSelectionModel().getSelectedItem() != null) {
				ysxmProperty.set(ysxmField.getSelectionModel().getSelectedItem().toString().trim());
				System.out.println(ysxmProperty.get());
			}
		});
		
		hlzbField.setOnAction(e -> {
			if(hlzbField.getSelectionModel().getSelectedItem() != null) {
				hlzbProperty.set(hlzbField.getSelectionModel().getSelectedItem().toString().trim());
				System.out.println(hlzbProperty.get());
			}
			try {
				String sqlString = "select hzbh from dbo.T_HZXX where sfzj = ?";
				ps = ct.prepareStatement(sqlString);
				if(hlzbProperty.get().trim().equals("专家")) {
					ps.setString(1, "1");
				}else {
					ps.setString(1, "0");
				}
				rs = ps.executeQuery();
				hzmcField.getItems().clear();
				while(rs.next()) {
					String hzbhString = rs.getString(1);
					//System.out.println(111);
					//获取医生名称
					
					ps = ct.prepareStatement("select hzmc from dbo.T_HZXX where hzbh = ?");
					ps.setString(1, hzbhString);
					ResultSet rs2 = ps.executeQuery();
					while(rs2.next()) {
						hzmcField.getItems().add(rs2.getString(1));
					}
				}
				
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		hzmcField.setOnAction(e -> {
			if(hzmcField.getSelectionModel().getSelectedItem() != null) {
				hzmcProperty.set(hzmcField.getSelectionModel().getSelectedItem().toString().trim());
				System.out.println(hzmcProperty.get());
			}
			
			//填充应缴金额,找零金额
			try {
				String sqlString = "select ghfy from dbo.T_HZXX where hzmc = ?";
				ps = ct.prepareStatement(sqlString);
				ps.setString(1, hzmcProperty.get().trim());
				rs = ps.executeQuery();
				while(rs.next()) {
					String tmpString = rs.getString(1);
					yjjeField.setText(tmpString);
					zljeField.setText((Float.parseFloat(jkjeField.getText()) - Float.parseFloat(tmpString))+"");
				}
				
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		//输入数据相关 comboBox
		try {
			//获取科室名称
			ps = ct.prepareStatement("select ksmc from dbo.T_KSXX");
			rs = ps.executeQuery();
			while(rs.next()) {
				ksmcField.getItems().add(rs.getString(1));
			}
			
			//获取医生名称
			ps = ct.prepareStatement("select ysmc from dbo.T_KSYS");
			rs = ps.executeQuery();
			while(rs.next()) {
				ysxmField.getItems().add(rs.getString(1));
			}
			
			//获取号类种别
			ps = ct.prepareStatement("select distinct sfzj from test.dbo.T_HZXX");
			rs = ps.executeQuery();
			while(rs.next()) {
				if(rs.getString(1).trim().equals("1")) {
					hlzbField.getItems().add("专家");
				}else {
					hlzbField.getItems().add("普通");
				}
			}
			//获取号种名称
			ps = ct.prepareStatement("select hzmc from dbo.T_HZXX");
			rs = ps.executeQuery();
			while(rs.next()) {
				hzmcField.getItems().add(rs.getString(1));
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
		
		//TODO 根据表格填写内容动态改变comboBox选项 同时动态修改金额数值
		//TODO 按数据库号码顺序生成挂号号码
		//TODO 按钮快捷键
		
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
			try {
				String hzbhString = null;
				String ysbhString = null;
				String ghfyString = null;
				
				
				String sqlString = "select top 1 ghbh from dbo.T_GHXX order by ghbh desc;";
				ps = ct.prepareStatement(sqlString);
				rs = ps.executeQuery();
				rs.next();
				String newGhbhString = ""+(Integer.parseInt(rs.getString(1))+1);
				while(newGhbhString.length() < 6) {
					newGhbhString = "0" + newGhbhString;
				}
				System.out.println(newGhbhString);
				//TODO	根据其他输入获取T_GHXX所需其他数据，插入数据库
				
				ps = ct.prepareStatement("select hzbh,ghfy,ksbh from dbo.T_HZXX where hzmc=?");
				ps.setString(1, hzmcProperty.get());
				rs = ps.executeQuery();
				if(rs.next()) {
					hzbhString = rs.getString(1);
					ghfyString = rs.getString(2);
				}
				
				ps = ct.prepareStatement("select ysbh from dbo.T_KSYS where ysmc=?");
				ps.setString(1, ysxmProperty.get());
				rs = ps.executeQuery();
				if(rs.next()) {
					ysbhString = rs.getString(1);
				}
				
				//TODO 病人预存金额未修改
				//TODO 挂号人次未修改
				
				if(ghfyString != null && ysbhString != null && hzbhString != null) {
					ps = ct.prepareStatement("insert into dbo.T_GHXX values (?,?,?,?,1,0,?,getdate())");
					ps.setString(1, newGhbhString);
					ps.setString(2, hzbhString);
					ps.setString(3, ysbhString);
					ps.setString(4, username.get());
					ps.setString(5, ghfyString);
					
					ps.executeUpdate();
					ghhmField.setText(newGhbhString);
				}else {
					ghhmField.setText("挂号失败");
				}
				
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		clearButton.setOnAction(e -> {
			ksmcField.setValue(null);
			ysxmField.setValue(null);
			hlzbField.setValue(null);
			hzmcField.setValue(null);
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

class DoctorPane extends BorderPane{
	PreparedStatement ps = null;
	ResultSet rs = null;
	PreparedStatement psStatement = null;
	ResultSet rSet = null;
	VBox vbox = null;
	VBox vbox2 = null;
	StringProperty username = new SimpleStringProperty();
	
	private final TableView<Patient> patientTableView = new TableView<Patient>();
	private final ObservableList<Patient> patientDataList = FXCollections.observableArrayList();
	
	private final TableView<Doctor> doctorTableView = new TableView<DoctorPane.Doctor>();
	private final ObservableList<Doctor> doctorDataList = FXCollections.observableArrayList();
	
	
	@SuppressWarnings("unchecked")
	public DoctorPane(Stage stage, LoginPane lastPane, Connection ct) {
		//this.setOrientation(Orientation.VERTICAL);
		//this.setAlignment(Pos.CENTER);
		//this.setVgap(10);
		this.setPadding(new Insets(15));
		
		//按钮模拟菜单选项卡
		FlowPane titlePane = new FlowPane();
		titlePane.setAlignment(Pos.TOP_LEFT);
		titlePane.setHgap(5);
		titlePane.setPadding(new Insets(10));
		
		Button brButton = new Button("病人列表");
		brButton.setOnAction(e -> {
			//System.out.println("brbutton clicked");
			this.setBottom(vbox);
		});
		
		Button srButton = new Button("收入列表");
		srButton.setOnAction(e -> {
			//System.out.println("srButton clicked");
			this.setBottom(vbox2);
		});
		
		Button exitButton = new Button("退出系统");
		exitButton.setOnAction(e -> {
			this.setVisible(false);
			Scene scene = this.getScene();
			scene.setRoot(lastPane);
			lastPane.setVisible(true);
			stage.setScene(scene);
		});
		
		//TODO 按钮快捷键
		
		titlePane.getChildren().addAll(brButton, srButton, exitButton);
		//this.getChildren().add(titlePane);
		this.setTop(titlePane);
		
		//分割线
		Separator separator = new Separator(Orientation.HORIZONTAL);
		//this.getChildren().add(separator);
		this.setCenter(separator);
		
		//tableView 病人列表
			//获取数据
		username.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				
				try {
					//获取病人列表数据
						//清空之前数据
					patientDataList.clear();
					
					String sql = "select dbo.T_GHXX.ghbh,dbo.T_BRXX.brmc,dbo.T_GHXX.rqsj,dbo.T_HZXX.hzmc " + 
							"from dbo.T_GHXX,dbo.T_BRXX,dbo.T_HZXX " + 
							"where dbo.T_GHXX.ysbh = ? and dbo.T_BRXX.brbh = dbo.T_GHXX.brbh and dbo.T_GHXX.hzbh = dbo.T_HZXX.hzbh";
					ps = ct.prepareStatement(sql);
					ps.setString(1, username.getValue());
					rs = ps.executeQuery();
					while(rs.next()) {
						//System.out.println(rs.getString(1));
						//System.out.println(rs.getString(2));
						//System.out.println(rs.getString(3));
						//System.out.println(rs.getString(4));
						patientDataList.add(new Patient(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
					}
					
					//获取收入列表数据
						//清空之前数据
					doctorDataList.clear();
					
					String sqlString = "select dbo.T_GHXX.ghbh,dbo.T_KSXX.ksmc,dbo.T_GHXX.ysbh,dbo.T_KSYS.ysmc,dbo.T_HZXX.hzmc,dbo.T_GHXX.ghrc,dbo.T_GHXX.ghfy " + 
							"from dbo.T_KSXX,dbo.T_GHXX,dbo.T_KSYS,dbo.T_HZXX " + 
							"where dbo.T_KSYS.ysbh = dbo.T_GHXX.ysbh and dbo.T_KSXX.ksbh = dbo.T_KSYS.ksbh and dbo.T_HZXX.hzbh = dbo.T_GHXX.hzbh";
					ps = ct.prepareStatement(sqlString);
					rs = ps.executeQuery();
					while(rs.next()) {
						doctorDataList.add(new Doctor(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(5)));
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
				
			}
		});
		
		
		//构建病人列表界面
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
		patientTableView.setStyle("-fx-alignment: center");
		patientTableView.getColumns().addAll(ghbhColumn, brmcColumn, ghrqsjColumn, hlzbColumn);
		
		
		vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10,0,0,10));
		vbox.getChildren().addAll(patientTableView);
		
		//默认显示病人列表
		//this.getChildren().add(vbox);
		this.setBottom(vbox);
		
		
		//TODO 按照时间过滤
		//构建收入列表界面
		TableColumn<Doctor, String> ksmcColumn = new TableColumn<>("科室名称");
		ksmcColumn.setMinWidth(70);
		ksmcColumn.setCellValueFactory(new PropertyValueFactory<>("ksmc"));
		
		TableColumn<Doctor, String> ysbhColumn = new TableColumn<>("医生编号");
		ysbhColumn.setMinWidth(70);
		ysbhColumn.setCellValueFactory(new PropertyValueFactory<>("ysbh"));
		
		TableColumn<Doctor, String> ysmcColumn = new TableColumn<>("医生名称");
		ysmcColumn.setMinWidth(70);
		ysmcColumn.setCellValueFactory(new PropertyValueFactory<>("ysmc"));
		
		TableColumn<Doctor, String> hlzb2Column = new TableColumn<>("号种类别");
		hlzb2Column.setMinWidth(70);
		hlzb2Column.setCellValueFactory(new PropertyValueFactory<>("hlzb"));
		
		TableColumn<Doctor, String> ghrcColumn = new TableColumn<>("挂号人次");
		ghrcColumn.setMinWidth(70);
		ghrcColumn.setCellValueFactory(new PropertyValueFactory<>("ghrc"));
		
		TableColumn<Doctor, String> srhjColumn = new TableColumn<>("收入合计");
		srhjColumn.setMinWidth(70);
		srhjColumn.setCellValueFactory(new PropertyValueFactory<>("srhj"));
		
		doctorTableView.setItems(doctorDataList);
		doctorTableView.getColumns().addAll(ksmcColumn, ysbhColumn, ysmcColumn, hlzb2Column, ghrcColumn, srhjColumn);
		
		//时间过滤界面
		final HBox hBox = new HBox();
		hBox.setSpacing(5);
		hBox.setPadding(new Insets(15));
		
		Label beginTimeLabel = new Label("开始时间");
		TextField beginTimeField = new TextField();
		Label endTimeLabel = new Label("结束时间");
		TextField endTimeField = new TextField();
		Button queryButton = new Button("查询");
		
		queryButton.setOnAction(e -> {
			try {
				String sqlString2 = "select dbo.T_GHXX.ghbh,dbo.T_KSXX.ksmc,dbo.T_GHXX.ysbh,dbo.T_KSYS.ysmc,dbo.T_HZXX.hzmc,dbo.T_GHXX.ghrc,dbo.T_GHXX.ghfy " + 
						"from dbo.T_KSXX,dbo.T_GHXX,dbo.T_KSYS,dbo.T_HZXX " + 
						"where dbo.T_KSYS.ysbh = dbo.T_GHXX.ysbh and dbo.T_KSXX.ksbh = dbo.T_KSYS.ksbh and dbo.T_HZXX.hzbh = dbo.T_GHXX.hzbh " + 
						"and dbo.T_GHXX.rqsj > '"+ beginTimeField.getText().trim() +"' and dbo.T_GHXX.rqsj < '"+ endTimeField.getText().trim() +"'";
				
				psStatement = ct.prepareStatement(sqlString2);
				rSet = psStatement.executeQuery();
				
				//清空之前数据
				doctorDataList.clear();
				while(rSet.next()) {
					doctorDataList.add(new Doctor(rSet.getString(1), rSet.getString(2), rSet.getString(3), rSet.getString(4), rSet.getString(5), rSet.getString(5)));
				}
			}catch (Exception fe) {
				fe.printStackTrace();
			}finally {
				try {
					if (rSet != null) rSet.close();
					if (psStatement != null) psStatement.close();
				}catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		
		
		hBox.getChildren().addAll(beginTimeLabel, beginTimeField, endTimeLabel, endTimeField, queryButton);
		
		vbox2 = new VBox();
		vbox2.setSpacing(5);
		vbox2.setPadding(new Insets(10,0,0,10));
		vbox2.getChildren().addAll(hBox,doctorTableView);
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
 
        public String getGhbh() {
            return ghbh.get();
        }
 
        public void setGhbh(String fName) {
            ghbh.set(fName);
        }
 
        public String getBrmc() {
            return brmc.get();
        }
 
        public void setBrmc(String fName) {
            brmc.set(fName);
        }
        
        public String getGhrqsj() {
            return ghrqsj.get();
        }
 
        public void setGhrqsj(String fName) {
            ghrqsj.set(fName);
        }
        
        public String getHlzb() {
            return hlzb.get();
        }
 
        public void setHlzb(String fName) {
            hlzb.set(fName);
        }
	}
	
	//收入数据映射
	public static class Doctor{
		private final SimpleStringProperty ksmc;
		private final SimpleStringProperty ysbh;
		private final SimpleStringProperty ysmc;
		private final SimpleStringProperty hlzb;
		private final SimpleStringProperty ghrc;
		private final SimpleStringProperty srhj;
		
		private Doctor(String ksmc, String ysbh, String ysmc, String hlzb, String ghrc, String srhj) {
			this.ksmc = new SimpleStringProperty(ksmc);
			this.ysbh = new SimpleStringProperty(ysbh);
			this.ysmc = new SimpleStringProperty(ysmc);
			this.hlzb = new SimpleStringProperty(hlzb);
			this.ghrc = new SimpleStringProperty(ghrc);
			this.srhj = new SimpleStringProperty(srhj);
		}
		
		public String getKsmc() {
            return ksmc.get();
        }
 
        public void setKsmc(String fName) {
            ksmc.set(fName);
        }
        
        public String getYsbh() {
        	return ysbh.get();
        }
        public void setYsbh(String fName) {
        	ysbh.set(fName);
        }
        
        public String getYsmc() {
        	return ysmc.get();
        }
        public void setYsmc(String fName) {
        	ysmc.set(fName);
        }
        
        public String getHlzb() {
        	return hlzb.get();
        }
        public void setHlzb(String fName) {
        	hlzb.set(fName);
        }
        
        public String getGhrc() {
        	return ghrc.get();
        }
        public void setGhrc(String fName) {
        	ghrc.set(fName);
        }
        
        public String getSrhj() {
        	return srhj.get();
        }
        public void setSrhj(String fName) {
        	srhj.set(fName);
        }
	}
}

