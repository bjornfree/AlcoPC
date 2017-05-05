package sample.database;
import org.apache.commons.net.ftp.FTPClient;
import sun.rmi.runtime.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateAlcoDB {
    FTPClient mFTP = new FTPClient();
    final static String ftpServer = "ftp.ellada.com.ua";
    final static String ftpUser = "TEST";
    final static String ftpPassword = "12345678";
    final static String ftpPort = "21";

    private String root = System.getProperty("user.dir");
    FileOutputStream fos = null;

    public void download() {
        try {
            mFTP.connect(ftpServer);
            mFTP.login(ftpUser, ftpPassword);
            mFTP.enterLocalPassiveMode();
            String remoteFileName = "1.txt";
            fos = new FileOutputStream(root + "/Alcoupdate.txt");
            mFTP.retrieveFile(remoteFileName, fos);
            System.out.println("Download from ftp: " + remoteFileName);
            fos.close();
            mFTP.logout();
            mFTP.disconnect();
        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла с фтп: ");
            e.printStackTrace();
        }
    }
}
