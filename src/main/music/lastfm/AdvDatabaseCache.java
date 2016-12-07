package main.music.lastfm;

import de.umass.lastfm.cache.Cache;
import de.umass.lastfm.cache.DatabaseCache;
import de.umass.lastfm.cache.ExpirationPolicy;
import main.database.DBConnection;

import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Dennis on 6-12-2016.
 */
public class AdvDatabaseCache extends Cache {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/filesniffer"+ DBConnection.DEFAULT_DB_URL_PARAMS;
    private static final String DATABASE_USER = "filesniffer";
    private static final String DATABASE_PASSWORD = DATABASE_USER;
    private static final String DATABASE_TABLE = DATABASE_USER;

    private final DatabaseCache deligate;
    private static Cache instance = new AdvDatabaseCache();

    private AdvDatabaseCache() {
        try {
            deligate = new DatabaseCache(DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD), DATABASE_TABLE);
        } catch (SQLException e) {
            throw new Error(e);
        }
        ExpirationPolicy policy = new AdvExpirationPolicy();
        setExpirationPolicy(policy);
        deligate.setExpirationPolicy(policy);
    }

    static Cache getInstance() {
        return instance;
    }

    public boolean contains(String cacheEntryName) {
        return deligate.contains(cacheEntryName);
    }

    public InputStream load(String cacheEntryName) {
        return deligate.load(cacheEntryName);
    }

    public void remove(String cacheEntryName) {
        deligate.remove(cacheEntryName);
    }

    public void store(String cacheEntryName, InputStream inputStream, long expirationDate) {
        deligate.store(cacheEntryName, inputStream, expirationDate);
    }

    public boolean isExpired(String cacheEntryName) {
        return deligate.isExpired(cacheEntryName);
    }

    public void clear() {
        deligate.clear();
    }
}
