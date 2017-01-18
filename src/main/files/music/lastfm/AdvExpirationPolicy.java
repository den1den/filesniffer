package main.files.music.lastfm;

import de.umass.lastfm.cache.DefaultExpirationPolicy;
import de.umass.lastfm.cache.ExpirationPolicy;

import java.util.Map;

/**
 * Created by Dennis on 6-12-2016.
 */
public class AdvExpirationPolicy extends DefaultExpirationPolicy {
    private final ExpirationPolicy defaultPolicy = new DefaultExpirationPolicy();

    /**
     * One week in milliseconds
     */
    public static final long THREE_WEEKS = ONE_WEEK * 3;

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
        if (method.equals("artist.search")) {
            return THREE_WEEKS;
        }
        if (ONE_WEEK_METHODS.contains(method)) return ONE_WEEK;
        else return defaultPolicy.getExpirationTime(method, params);
    }
}
