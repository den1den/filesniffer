package main.music;

import de.umass.lastfm.Caller;
import de.umass.lastfm.cache.DefaultExpirationPolicy;
import de.umass.lastfm.cache.ExpirationPolicy;

import java.util.Map;

/**
 * Created by Dennis on 6-12-2016.
 */
public class MusicAttributesChecker {
    public static final String LASTFM_KEY = "c7f0bf7e4009b7aea9dff906d485b5f7";
    public static final String LASTFM_SECRET = "3b231e1cd88614897a456cf9d0b84800";
    public static final String LASTFM_USER = "den_1_den";
    public static final String LASTFM_APP_NAME = "filesniffer";

    static {
        Caller.getInstance().setUserAgent("Java/" + System.getProperty("java.version"));
        Caller.getInstance().setUserAgent("tst");
    }

    static class AdvExpirationPolicy extends DefaultExpirationPolicy {
        ExpirationPolicy defaultPolicy = new DefaultExpirationPolicy();

        @Override
        public long getExpirationTime(String method, Map<String, String> params) {
            method = method.toLowerCase();
            System.out.println("method = " + method);
            if (method.contains("weekly")) {
                if (!method.contains("list"))
                    return params.containsKey("to") && params.containsKey("from") ? Long.MAX_VALUE : cacheRecentWeeklyCharts;
                else
                    return cacheRecentWeeklyCharts;
            }
            if(method.equals("artist.search")){
                throw new UnsupportedOperationException("Not done yet");
            }
            if (ONE_WEEK_METHODS.contains(method)) return ONE_WEEK;
            else return defaultPolicy.getExpirationTime(method, params);
        }
    }
}
