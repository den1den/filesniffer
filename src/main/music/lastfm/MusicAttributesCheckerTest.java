package main.music.lastfm;//import static org.junit.Assert.*;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.cache.Cache;
import de.umass.lastfm.cache.ExpirationPolicy;
import org.junit.*;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static main.music.lastfm.MusicAttributesChecker.LASTFM_KEY;

/**
 * Created by Dennis on 6-12-2016.
 */
public class MusicAttributesCheckerTest {
    DateFormat format = DateFormat.getDateInstance();

    @Before
    public void setUp() throws Exception {
        MusicAttributesChecker.setUp();
        Caller.getInstance().setCache(new CacheChecker(Caller.getInstance().getCache()));
    }

    @Test
    public void test() throws Exception {
        Collection<Artist> artists = Artist.search("Annagrace", LASTFM_KEY);
        System.out.println("Annagrace: " + artists);
        artists = Artist.search("Rammstein", LASTFM_KEY);
        System.out.println("Rammstein: " + artists);
    }

    private class ExpirationPolicyChecker implements ExpirationPolicy {

        final ExpirationPolicy delegate;

        public ExpirationPolicyChecker(ExpirationPolicy delegate) {
            this.delegate = delegate;
        }

        @Override
        public long getExpirationTime(String method, Map<String, String> params) {
            long expirationTime = delegate.getExpirationTime(method, params);
            System.out.println("getExpirationTime(method = [" + method + "], params = [" + params + "]): " + expirationTime);
            return expirationTime;
        }
    }

    private class CacheChecker extends Cache {
        final Cache delegate;

        CacheChecker(Cache delegate) {
            this.delegate = delegate;
        }

        @Override
        public InputStream load(String cacheEntryName) {
            InputStream i = delegate.load(cacheEntryName);
            System.out.println("load(cacheEntryName = [" + cacheEntryName + "]): " + (!Objects.isNull(i)));
            return i;
        }

        @Override
        public void remove(String cacheEntryName) {
            System.out.println("CacheChecker.remove");
            System.out.println("cacheEntryName = [" + cacheEntryName + "]");
            delegate.remove(cacheEntryName);
        }

        @Override
        public void store(String cacheEntryName, InputStream inputStream, long expirationDate) {
            System.out.println("store(cacheEntryName = [" + cacheEntryName + "], inputStream = [" + inputStream + "], expirationDate = [" + expirationDate + "])");
            delegate.store(cacheEntryName, inputStream, expirationDate);
        }

        @Override
        public boolean contains(String cacheEntryName) {
            boolean contains = delegate.contains(cacheEntryName);
            System.out.println("contains(cacheEntryName = [" + cacheEntryName + "]): " + contains);
            return contains;
        }

        @Override
        public ExpirationPolicy getExpirationPolicy() {
            return new ExpirationPolicyChecker(delegate.getExpirationPolicy());
        }

        @Override
        public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
            delegate.setExpirationPolicy(expirationPolicy);
        }

        @Override
        public boolean isExpired(String cacheEntryName) {
            boolean expired = delegate.isExpired(cacheEntryName);
            System.out.println("isExpired(cacheEntryName = [" + cacheEntryName + "]): " + expired);
            return expired;
        }

        @Override
        public void clear() {
            System.out.println("CacheChecker.clear");
            delegate.clear();
        }
    }
}