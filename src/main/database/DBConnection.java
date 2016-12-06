package main.database;

/**
 * Created by Dennis on 6-12-2016.
 */
public class DBConnection {
    private static DBConnection instance = null;

    public static DBConnection getInstance() {
        if(instance == null){
            instance = new DBConnection();
        }
        return instance;
    }

    private String location = "sqlite.db";

    // https://www.youtube.com/watch?v=ARJ94rxphDQ
}
