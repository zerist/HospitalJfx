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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
	
	//��������
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
		//���ñ���
		priStage.setTitle("Hospital System");
		
		//�������ݿ�
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
		//loginPane ����
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25));
		
		//�ؼ�
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
				//������֪��Ϊʲô���У��ᱨlambda@p0����
				String sql = "select * from dbo.T_BRXX where brbh=?";
				int type = 0; //����
				if(patientButton.isSelected()) {
					//tableName = "dbo.T_BRXX";
					//bhName = "brbh";
					sql = "select * from dbo.T_BRXX where brbh=?";
					type = 0;
				}else {
					//tableName = "dbo.T_KSYS";
					//bhName = "ysbh";
					sql = "select * from dbo.T_KSYS where ysbh=?";
					type = 1;	//ҽ��
				}
				
				ps = ct.prepareStatement(sql);
				ps.setString(1, userNameField.getText());
				rs = ps.executeQuery();
				
				
				if(rs.next()) {
					String pwd = rs.getString("dlkl");
					
					//System.out.println(userNameField.getText());
					//System.out.println(passwdField.getText());
					//System.out.println(pwd.length());
					//pwd����Ϊ���пո�
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
	public RegistePane(Stage stage, LoginPane lastPane, Connection ct) {
		//����
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		this.setVgap(20);
		this.setHgap(10);
		
		//����ؼ�
		FlowPane titlePane = new FlowPane();
		titlePane.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("����Һ�");
		titleLabel.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 20));
		titlePane.getChildren().add(titleLabel);
		this.getChildren().add(titlePane);
		
		//�ָ���
		this.getChildren().add(new Separator(Orientation.HORIZONTAL));
		
		//����ؼ�
		GridPane inputPane = new GridPane();
		inputPane.setAlignment(Pos.CENTER);
		inputPane.setPadding(new Insets(15));
		inputPane.setHgap(20);
		inputPane.setVgap(10);
		
		Label ksmcLabel = new Label("��������");
		inputPane.add(ksmcLabel, 0, 0);
		ComboBox<String> ksmcField = new ComboBox<String>();
		ksmcField.setPromptText("�����������");
		ksmcField.setEditable(true);
		
		inputPane.add(ksmcField, 1, 0);
		Label ysxmLabel = new Label("ҽ������");
		inputPane.add(ysxmLabel, 2, 0);
		ComboBox<String> ysxmField = new ComboBox<String>();
		ysxmField.setPromptText("����ҽ������");
		ysxmField.setEditable(true);
		inputPane.add(ysxmField, 3, 0);
		
		Label hlzbLabel = new Label("�����ֱ�");
		inputPane.add(hlzbLabel, 0, 1);
		ComboBox<String> hlzbField = new ComboBox<String>();
		hlzbField.setPromptText("��������ֱ�");
		hlzbField.setEditable(true);
		hlzbField.getItems().addAll("ר��", "��ͨ");
		inputPane.add(hlzbField, 1, 1);
		Label hzmcLabel = new Label("��������");
		inputPane.add(hzmcLabel, 2, 1);
		ComboBox<String> hzmcField = new ComboBox<String>();
		hzmcField.setPromptText("�����������");
		hzmcField.setEditable(true);
		inputPane.add(hzmcField, 3, 1);
		
		Label jkjeLabel = new Label("�ɿ���");
		inputPane.add(jkjeLabel, 0, 2);
		TextField jkjeField = new TextField();
		jkjeField.setEditable(false);
		inputPane.add(jkjeField, 1, 2);
		Label yjjeLabel = new Label("Ӧ�ɽ��");
		inputPane.add(yjjeLabel, 2, 2);
		TextField yjjeField = new TextField();
		yjjeField.setEditable(false);
		inputPane.add(yjjeField, 3, 2);
		
		Label zljeLabel = new Label("������");
		inputPane.add(zljeLabel, 0, 3);
		TextField zljeField = new TextField();
		zljeField.setEditable(false);
		inputPane.add(zljeField, 1, 3);
		Label ghhmLabel = new Label("�Һź���");
		inputPane.add(ghhmLabel, 2, 3);
		TextField ghhmField = new TextField();
		ghhmField.setEditable(false);
		inputPane.add(ghhmField, 3, 3);
		
		//����������� comboBox
		try {
			//��ȡ��������
			ps = ct.prepareStatement("select ksmc from dbo.T_KSXX");
			rs = ps.executeQuery();
			while(rs.next()) {
				ksmcField.getItems().add(rs.getString(1));
			}
			
			//��ȡҽ������
			ps = ct.prepareStatement("select ysmc from dbo.T_KSYS");
			rs = ps.executeQuery();
			while(rs.next()) {
				ysxmField.getItems().add(rs.getString(1));
			}

			//��ȡ��������
			ps = ct.prepareStatement("select hzmc from dbo.T_HZXX");
			rs = ps.executeQuery();
			while(rs.next()) {
				hzmcField.getItems().add(rs.getString(1));
			}
			
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
		
		//TODO ���ݱ����д���ݶ�̬�ı�comboBoxѡ�� ͬʱ��̬�޸Ľ����ֵ
		//TODO �����ݿ����˳�����ɹҺź���
		
		this.getChildren().add(inputPane);
		
		//�²㰴ť
		FlowPane buttonFlowPane = new FlowPane();
		buttonFlowPane.setAlignment(Pos.CENTER);
		buttonFlowPane.setHgap(30);
		
		Button confirmButton = new Button("ȷ��");
		Button clearButton = new Button("���");
		Button exitButton = new Button("�˳�");
		
		confirmButton.setOnAction(e -> {
			System.out.println("click confirmButton");
			//TODO
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
		
		//��ťģ��˵�ѡ�
		FlowPane titlePane = new FlowPane();
		titlePane.setAlignment(Pos.TOP_LEFT);
		titlePane.setHgap(5);
		titlePane.setPadding(new Insets(10));
		
		Button brButton = new Button("�����б�");
		brButton.setOnAction(e -> {
			//System.out.println("brbutton clicked");
			this.setBottom(patientTableView);
		});
		
		Button srButton = new Button("�����б�");
		srButton.setOnAction(e -> {
			//System.out.println("srButton clicked");
			this.setBottom(doctorTableView);
		});
		
		Button exitButton = new Button("�˳�ϵͳ");
		exitButton.setOnAction(e -> {
			this.setVisible(false);
			Scene scene = this.getScene();
			scene.setRoot(lastPane);
			lastPane.setVisible(true);
			stage.setScene(scene);
		});
		
		titlePane.getChildren().addAll(brButton, srButton, exitButton);
		//this.getChildren().add(titlePane);
		this.setTop(titlePane);
		
		//�ָ���
		Separator separator = new Separator(Orientation.HORIZONTAL);
		//this.getChildren().add(separator);
		this.setCenter(separator);
		
		//tableView �����б�
			//��ȡ����
		username.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				
				try {
					//��ȡ�����б�����
						//���֮ǰ����
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
					
					//��ȡ�����б�����
						//���֮ǰ����
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
		
		
		//���������б����
		TableColumn<Patient, String> ghbhColumn = new TableColumn<>("�Һű��");
		ghbhColumn.setMinWidth(100);
		ghbhColumn.setCellValueFactory(new PropertyValueFactory<>("ghbh"));
		
		TableColumn<Patient, String> brmcColumn = new TableColumn<>("��������");
		brmcColumn.setMinWidth(100);
		brmcColumn.setCellValueFactory(new PropertyValueFactory<>("brmc"));
		
		TableColumn<Patient, String> ghrqsjColumn = new TableColumn<>("�Һ�����ʱ��");
		ghrqsjColumn.setMinWidth(100);
		ghrqsjColumn.setCellValueFactory(new PropertyValueFactory<>("ghrqsj"));
		
		TableColumn<Patient, String> hlzbColumn = new TableColumn<>("�����ֱ�");
		hlzbColumn.setMinWidth(100);
		hlzbColumn.setCellValueFactory(new PropertyValueFactory<>("hlzb"));
		
		patientTableView.setItems(patientDataList);
		patientTableView.getColumns().addAll(ghbhColumn, brmcColumn, ghrqsjColumn, hlzbColumn);
		
		
		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10,0,0,10));
		vbox.getChildren().addAll(patientTableView);
		
		//Ĭ����ʾ�����б�
		//this.getChildren().add(vbox);
		this.setBottom(vbox);
		
		
		//���������б����
		TableColumn<Doctor, String> ksmcColumn = new TableColumn<>("��������");
		ksmcColumn.setMinWidth(70);
		ksmcColumn.setCellValueFactory(new PropertyValueFactory<>("ksmc"));
		
		TableColumn<Doctor, String> ysbhColumn = new TableColumn<>("ҽ�����");
		ysbhColumn.setMinWidth(70);
		ysbhColumn.setCellValueFactory(new PropertyValueFactory<>("ysbh"));
		
		TableColumn<Doctor, String> ysmcColumn = new TableColumn<>("ҽ������");
		ysmcColumn.setMinWidth(70);
		ysmcColumn.setCellValueFactory(new PropertyValueFactory<>("ysmc"));
		
		TableColumn<Doctor, String> hlzb2Column = new TableColumn<>("�������");
		hlzb2Column.setMinWidth(70);
		hlzb2Column.setCellValueFactory(new PropertyValueFactory<>("hlzb"));
		
		TableColumn<Doctor, String> ghrcColumn = new TableColumn<>("�Һ��˴�");
		ghrcColumn.setMinWidth(70);
		ghrcColumn.setCellValueFactory(new PropertyValueFactory<>("ghrc"));
		
		TableColumn<Doctor, String> srhjColumn = new TableColumn<>("����ϼ�");
		srhjColumn.setMinWidth(70);
		srhjColumn.setCellValueFactory(new PropertyValueFactory<>("srhj"));
		
		doctorTableView.setItems(doctorDataList);
		doctorTableView.getColumns().addAll(ksmcColumn, ysbhColumn, ysmcColumn, hlzb2Column, ghrcColumn, srhjColumn);
		
		
		final VBox vbox2 = new VBox();
		vbox2.setSpacing(5);
		vbox2.setPadding(new Insets(10,0,0,10));
		vbox2.getChildren().addAll(doctorTableView);
	}
	
	
	//��������ӳ��
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
	
	//��������ӳ��
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

