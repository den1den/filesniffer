package main.database;//import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by Dennis on 7-12-2016.
 */
public class DBConnectionTest {
    @Test
    public void getInstance() throws Exception {
        DBConnection i = DBConnection.getInstance();
    }

}