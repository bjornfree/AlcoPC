package sample.database;
import java.sql.*;

public class DBHelper {
    public static final String TABLE_NAME_MARK = "marks";
    public static final String TABLE_NAME_ALCOCODE = "alcocode";
    public static final String TABLE_NAME_QR = "qrcode";


    public static final String KEY_ID = "_id";
    public static final String KEY_MARK = "mark";
    public static final String KEY_ALCOCODE = "alcocode";
    public static final String KEY_COUNTER = "counter";
    public static final String KEY_QR = "counter";


    private String root = System.getProperty("user.dir");

    public void create() {
        try {
            Class.forName("org.h2.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                    "sa", "");
            Statement st = null;
            st = conn.createStatement();
            try {
                st.executeQuery("SELECT * FROM " + TABLE_NAME_MARK);
                System.out.println("Таблица существует: " + TABLE_NAME_MARK);

            } catch (SQLException exc) {
                System.out.println("Таблица создана: " + TABLE_NAME_MARK);
                st.execute("create table " + TABLE_NAME_MARK + " ("
                        + " " + KEY_ID + " integer primary key auto_increment,"
                        + " " + KEY_MARK + " VARCHAR(68) UNIQUE);");
            }
            try {
                st.executeQuery("SELECT * FROM " + TABLE_NAME_ALCOCODE);
                System.out.println("Таблица существует: " + TABLE_NAME_ALCOCODE);

            } catch (SQLException exc) {
                System.out.println("Таблица создана: " + TABLE_NAME_ALCOCODE);
                st.execute("create table " + TABLE_NAME_ALCOCODE + " ("
                        + " " + KEY_ID + " integer primary key auto_increment,"
                        + " " + KEY_ALCOCODE + " VARCHAR ,"
                        + " " + KEY_COUNTER + " integer" + ");");
            }
            try {
                st.executeQuery("SELECT * FROM " + TABLE_NAME_QR);
                System.out.println("Таблица существует: " + TABLE_NAME_QR);
                System.out.println("-----------------------------");
            } catch (SQLException exc) {
                System.out.println("Таблица создана: " + TABLE_NAME_QR);
                System.out.println("-----------------------------");
                st.execute("create table " + TABLE_NAME_QR + " ("
                        + " " + KEY_ID + " integer primary key auto_increment,"
                        + " " + KEY_QR + " VARCHAR UNIQUE);");
            }
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            Class.forName("org.h2.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:h2:file:" + root + "/test",
                    "sa", "");
            Statement st = null;
            st = conn.createStatement();

            try{
                st.execute("DROP TABLE " + TABLE_NAME_ALCOCODE);
                System.out.println("Таблица удалена: "+ TABLE_NAME_ALCOCODE );
            } catch (Exception e){
                System.out.println("Таблица не удалена: "+ TABLE_NAME_ALCOCODE);
            }
            try{
                st.execute("DROP TABLE " + TABLE_NAME_MARK);
                System.out.println("Таблица удалена: "+ TABLE_NAME_MARK);
            }catch (Exception e){
                System.out.println("Таблица не удалена: "+ TABLE_NAME_MARK);
            }
            try{
                st.execute("DROP TABLE " + TABLE_NAME_QR);
                System.out.println("Таблица удалена: "+ TABLE_NAME_QR);
                System.out.println("-----------------------------");
            } catch (Exception e){
                System.out.println("Таблица не удалена: "+ TABLE_NAME_QR);
            }
            st.close();
        } catch (Exception e){
            System.out.println("Операция delete не отработала");
        }
    }
}