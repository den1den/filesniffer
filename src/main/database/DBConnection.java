package main.database;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Dennis on 6-12-2016.
 */
public class DBConnection {
    public static final String DEFAULT_DB_URL_PARAMS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Amsterdam";
    private static DBConnection instance = null;

    public static DBConnection getInstance() {
        if(instance == null){
            new DBConnection();
        }
        return instance;
    }

    public final DSLContext d;

    public DBConnection() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/filesniffer_django"+DEFAULT_DB_URL_PARAMS,
                "filesniffer", "filesniffer")) {
             d = DSL.using(conn, SQLDialect.MYSQL);

            Result<Record> result = d.select().from("finesniffer_file").fetch();
            for (Record r : result) {
                Integer id = (Integer) r.getValue(0);
                String original_filename = (String) r.getValue(1);
                String extendsion = (String) r.getValue(2);
                String file_group_id = (String) r.getValue(3);
                System.out.println("id: " + id + " original_filename: " + original_filename + " extendsion: " + extendsion + " file_group_id: "+file_group_id);
            }
        } catch (SQLException e) {
            throw new Error(e);
        }
    }
}
