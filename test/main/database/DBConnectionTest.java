package main.database;//import static org.junit.Assert.*;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Dennis on 7-12-2016.
 */
public class DBConnectionTest {
    @Test
    public void getInstance() throws Exception {
        Connection connection = DBConnection.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("show tables");
            while (rs.next()) {
            }
            rs.close();
        } finally {
            if (statement != null) statement.close();
        }
    }

}