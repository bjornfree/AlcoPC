package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Data;
import sample.database.DBHelper;
import sample.database.UpdateAlcoDB;
import sample.decoder.PDF417decoder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Controller {
    public Button Update;
    private String root = System.getProperty("user.dir");
    private String myLogs;
    DBHelper dbHelper = new DBHelper();
    private String LOG_TAG = myLogs;
    @FXML
    TextField enterCode = new TextField();

    @FXML
    Text markCounter = new Text();
    @FXML
    Button Save;
    @FXML
    Button Clear;
    @FXML
    Text qrCounter = new Text();
    @FXML
    private TableView<Data> alcoTable = new TableView<>();
    @FXML
    TableColumn alcocode = new TableColumn(DBHelper.KEY_ALCOCODE);
    @FXML
    TableColumn alcoCounter = new TableColumn(DBHelper.KEY_COUNTER);


    @FXML

    private void initialize() {
        dbHelper.create();
        showCounter();
        initData();
        todo();
        onClickTable();
    }

    private void initData() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                    "sa", "");
            Statement st = null;
            st = conn.createStatement();

            String SQL = "SELECT * FROM " + DBHelper.TABLE_NAME_ALCOCODE;
            ResultSet rs = st.executeQuery(SQL);

            ObservableList alcoData = FXCollections.observableArrayList();

            while (rs.next()) {
                alcoData.add(new Data(rs.getString(DBHelper.KEY_ALCOCODE), rs.getInt(DBHelper.KEY_COUNTER)));
            }

            alcoTable.setItems(alcoData);
            alcoTable.getColumns().clear();
            alcoTable.getColumns().addAll(alcocode, alcoCounter);
            alcocode.setCellValueFactory(new PropertyValueFactory<Data, String>(DBHelper.KEY_ALCOCODE));
            alcoCounter.setCellValueFactory(new PropertyValueFactory<Data, String>(DBHelper.KEY_COUNTER));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void todo() {
        enterCode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String enterCod = enterCode.getText();
                    enterCode.clear();
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                                "sa", "");
                        Statement st = null;
                        st = conn.createStatement();

                        if (enterCod.length() == 68) {
                            try {
                                ResultSet resMark = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_MARK);
                                if (resMark.next()) {
                                    do {
                                        String markContains = resMark.getString(DBHelper.KEY_MARK);
                                        if (markContains.equals(enterCod)) {

                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Внимание!");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Марка уже была просканирована!");
                                            alert.showAndWait();

                                            System.out.println("Марка уже просканирована: " + enterCod);
                                            return;
                                        }
                                    } while (resMark.next());
                                }
                                st.executeUpdate("INSERT INTO " + DBHelper.TABLE_NAME_MARK + " (" + DBHelper.KEY_MARK + ") VALUES('" + enterCod + "');");
                                System.out.println("Добавлена марка:" + enterCod);
                                PDF417decoder pdf417decoder = new PDF417decoder(enterCod);
                                String result = pdf417decoder.extractCode();
                                showCounter();

                                ResultSet resAlcocode = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_ALCOCODE);
                                if (resAlcocode.next()) {
                                    do {
                                        String getAlcocode = resAlcocode.getString(DBHelper.KEY_ALCOCODE);
                                        if (getAlcocode.contains(result)) {
                                            int getCounter = resAlcocode.getInt(DBHelper.KEY_COUNTER);
                                            int getIdvalue = resAlcocode.getInt(DBHelper.KEY_ID);
                                            int lastCounter = (getCounter + 1);
                                            st.executeUpdate("UPDATE " + DBHelper.TABLE_NAME_ALCOCODE + " SET " + DBHelper.KEY_COUNTER + " = " + lastCounter + " WHERE " + DBHelper.KEY_ID + " = " + getIdvalue + ";");
                                            System.out.println("Обновлен счетчик алкокода: " + getAlcocode + " = " + (getCounter + 1));
                                            initData();
                                            return;
                                        }
                                    } while (resAlcocode.next());
                                }
                                st.executeUpdate("INSERT INTO " + DBHelper.TABLE_NAME_ALCOCODE + " (" + DBHelper.KEY_ALCOCODE + " , " + DBHelper.KEY_COUNTER + ") VALUES ('" + result + "','1');");
                                System.out.println("Добавлен алкокод: " + result);
                                initData();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else if (enterCod.length() > 31 && enterCod.length() < 35) {
                            try {
                                ResultSet resQR = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_QR);
                                if (resQR.next()) {
                                    do {
                                        String qrContains = resQR.getString(DBHelper.KEY_QR);
                                        if (qrContains.equals(enterCod)) {

                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Внимание!");
                                            alert.setHeaderText(null);
                                            alert.setContentText("QR уже был просканирован!");
                                            alert.showAndWait();

                                            System.out.println("QR уже просканирован: " + enterCod);
                                            return;
                                        }
                                    } while (resQR.next());
                                }
                                st.executeUpdate("INSERT INTO " + DBHelper.TABLE_NAME_QR + " (" + DBHelper.KEY_QR + ") VALUES('" + enterCod + "');");
                                System.out.println("Добавлен QR:" + enterCod);
                                showCounter();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Неверный формат марки");

                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Внимание!");
                            alert.setHeaderText(null);
                            alert.setContentText("Неверный формат марки!");
                            alert.show();
                        }

                    } catch (Exception e) {
                        System.out.println("blyad");
                    }
                }
            }
        });
    }

    @FXML
    public void showCounter() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                    "sa", "");
            Statement st = null;
            st = conn.createStatement();
            ResultSet resMark = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_MARK);
            int q = 0;
            while (resMark.next()) {
                q++;
            }
            markCounter.setText("Марки: " + String.valueOf(q));

            ResultSet resQR = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_QR);
            int w = 0;
            while (resQR.next()) {
                w++;
            }
            qrCounter.setText("QR: " + String.valueOf(w));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnClear() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        dbHelper.delete();
        dbHelper.create();
        initData();
        showCounter();
    }

    public void btnSave() {
        try {
            List<String> data = new ArrayList<>();
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(null);

            Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                    "sa", "");
            Statement st = null;
            st = conn.createStatement();
            ResultSet resMark = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_MARK);

            if (resMark.next()) {
                do {
                    data.add(resMark.getString(DBHelper.KEY_MARK));
                } while (resMark.next());
            }

            ResultSet resQr = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_QR);

            if (resQr.next()) {
                do {
                    data.add(resQr.getString(DBHelper.KEY_QR));
                } while (resQr.next());
            }
            try {
                FileWriter writer = new FileWriter(file);
                for (String list : data) {
                    writer.write(list + "\n");
                }
                writer.flush();
                writer.close();
            } catch (Exception e) {
                System.out.println("Ошибка записи файла : " + e);
            }
            System.out.println("Файл сохранен  " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnUpdDB(ActionEvent actionEvent) {
        UpdateAlcoDB updateAlcoDB = new UpdateAlcoDB();
        String line;
        String[] lines;
        updateAlcoDB.download();
        dbHelper.rebase();
        try {
            File file = new File(root, "/Alcoupdate.txt");
            Scanner scan = new Scanner(file, "Cp1251");
            while (scan.hasNext()) {
                line = scan.nextLine();
                lines = line.split(";");
                try {
                    Class.forName("org.h2.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                            "sa", "");
                    Statement st = null;
                    st = conn.createStatement();
                    st.executeUpdate("INSERT INTO " + DBHelper.TABLE_NAME_ALCOBASE + " (" + DBHelper.KEY_ALCOCODE + " , " + DBHelper.KEY_MAKER + " , " + DBHelper.KEY_GROUP + " , " + DBHelper.KEY_NAME + " , " + ") VALUES ('" + lines[0] + "','" + lines[1] + "','" + lines[2] + "','" + lines[3] + "'" + ");");
                } catch (Exception e) {
                    System.out.println("Ошибка " + e);
                }
            }
        } catch (Exception e) {
        }
    }

    public void onClickTable(){
        alcoTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Data>() {
            @Override
            public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
                if (alcoTable.getSelectionModel().getSelectedItem() != null) {
                    String selectedItem = alcoTable.getSelectionModel().getSelectedItem().getAlcocode();

                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                                "sa", "");
                        Statement st = null;
                        st = conn.createStatement();
                        ResultSet res = st.executeQuery("SELECT * FROM " + DBHelper.TABLE_NAME_ALCOBASE);

                        if(res.next()){
                            do {
                                String markContains = res.getString(DBHelper.KEY_ALCOCODE);
                                if (markContains.equals(selectedItem)) {



                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Информация по алкокоду: " + res.getString(DBHelper.KEY_ALCOCODE));
                                    alert.setHeaderText(null);
                                    alert.setContentText("Алкокод: " + res.getString(DBHelper.KEY_ALCOCODE) + "\n" + "Наименование: " + res.getString(DBHelper.KEY_NAME) + "\n" + "Производитель: " + res.getString(DBHelper.KEY_MAKER) + "\n" +"Группа: " + res.getString(DBHelper.KEY_GROUP));
                                    alert.showAndWait();


                                    System.out.println("Alcocod: " + res.getString(DBHelper.KEY_ALCOCODE));
                                    System.out.println("Производитель: " + res.getString(DBHelper.KEY_MAKER));
                                    System.out.println("Группа: " + res.getString(DBHelper.KEY_GROUP));
                                    System.out.println("Наименование: " + res.getString(DBHelper.KEY_NAME));
                                }
                            }while (res.next());
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    };
                }
            }
        });
    }

}