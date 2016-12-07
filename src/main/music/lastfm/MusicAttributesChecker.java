package main.music.lastfm;

import de.umass.lastfm.Caller;

/**
 * Created by Dennis on 6-12-2016.
 */
public class MusicAttributesChecker {

    public static final String LASTFM_KEY = "c7f0bf7e4009b7aea9dff906d485b5f7";
    public static final String LASTFM_SECRET = "3b231e1cd88614897a456cf9d0b84800";
    public static final String LASTFM_USER = "den_1_den";
    public static final String LASTFM_APP_NAME = "filesniffer";

    public static void setUp() {
        Caller.getInstance().setUserAgent("Java/" + System.getProperty("java.version"));
        Caller.getInstance().setUserAgent("tst");
        Caller.getInstance().setCache(AdvDatabaseCache.getInstance());
        de.umass.lastfm.cache.Cache.setHashCacheEntryNames(false);
        System.out.println("MusicAttributesChecker.setUp");
    }

    static {
        setUp();
    }
}
