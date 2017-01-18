package main.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Dennis on 6-12-2016.
 */
public class DBConnection {
    private static final String DATABASE_HOST = "localhost";
    private static final int DATABASE_PORT = 3306;
    private static final String DATABASE_USER = "filesniffer";
    private static final String DATABASE_PASSWORD = DATABASE_USER;
    private static final String DATABASE_NAME = DATABASE_USER;
    public static final String DATABASE_LASTFM_TABLE = "last_fm";

    static ComboPooledDataSource comboPooledDataSource() {
        try {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
            cpds.setJdbcUrl("jdbc:mysql://" + DATABASE_HOST + ":" + String.valueOf(DATABASE_PORT) + "/" + DATABASE_NAME);

            Properties connectionProps = new Properties();
            connectionProps.put("user", DATABASE_USER);
            connectionProps.put("password", DATABASE_PASSWORD);
            connectionProps.put("useUnicode", "true");
            connectionProps.put("useJDBCCompliantTimezoneShift", "true");
            connectionProps.put("useLegacyDatetimeCode", "false");
            connectionProps.put("serverTimezone", "Europe/Amsterdam");
            connectionProps.put("useSSL", "false");
            cpds.setProperties(connectionProps);

            cpds.setMinPoolSize(1);
            cpds.setAcquireIncrement(1);
            cpds.setMaxPoolSize(5);

            return cpds;
        } catch (PropertyVetoException e) {
            throw new Error(e);
        }
    }

    private static ComboPooledDataSource dataSource= null;

    public static Connection getConnection() {
        if(dataSource == null){
            dataSource = comboPooledDataSource();
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    void depr(){
        Result<Record> result = null;//d.select().from("finesniffer_file").fetch();
        for (Record r : result) {
            Integer id = (Integer) r.getValue(0);
            String original_filename = (String) r.getValue(1);
            String extendsion = (String) r.getValue(2);
            String file_group_id = (String) r.getValue(3);
            System.out.println("id: " + id + " original_filename: " + original_filename + " extendsion: " + extendsion + " file_group_id: "+file_group_id);
        }
    }
}
