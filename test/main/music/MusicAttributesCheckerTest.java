package main.music;//import static org.junit.Assert.*;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.cache.ExpirationPolicy;
import de.umass.lastfm.cache.FileSystemCache;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static main.music.MusicAttributesChecker.LASTFM_KEY;

/**
 * Created by Dennis on 6-12-2016.
 */
public class MusicAttributesCheckerTest {
    DateFormat format = DateFormat.getDateInstance();

    @Before
    public void setUp() throws Exception {
        Caller.getInstance().setCache(new CacheChecker());
    }

    @Test
    public void test() throws Exception {
        Collection<Artist> artists = Artist.search("Annagrace", LASTFM_KEY);
        System.out.println("artists = " + artists);
    }

    private class CacheChecker extends FileSystemCache {
        @Override
        public InputStream load(String cacheEntryName) {
            InputStream i = super.load(cacheEntryName);
            System.out.println("load(cacheEntryName = [" + cacheEntryName + "]): (i == null) = [" + (Objects.isNull(i)) + ']');
            return i;
        }

        @Override
        public void store(String cacheEntryName, InputStream inputStream, long expirationDate) {
            System.out.println("store(cacheEntryName = [" + cacheEntryName + "], inputStream = [" + inputStream + "], expirationDate = [" + expirationDate + "])");
            super.store(cacheEntryName, inputStream, expirationDate);
        }

        @Override
        public boolean contains(String cacheEntryName) {
            boolean contains = super.contains(cacheEntryName);
            System.out.println("contains(cacheEntryName = [" + cacheEntryName + "]): " + contains);
            return contains;
        }

        @Override
        public ExpirationPolicy getExpirationPolicy() {
            return (method, params) -> {
                long expirationTime = CacheChecker.super.getExpirationPolicy().getExpirationTime(method, params);
                System.out.println("getExpirationTime(method = [" + method + "], params = [" + params + "]): " + expirationTime);
                return expirationTime;
            };
        }

        @Override
        public boolean isExpired(String cacheEntryName) {
            boolean expired = super.isExpired(cacheEntryName);
            System.out.println("isExpired(cacheEntryName = [" + cacheEntryName + "]): " + expired);
            return expired;
        }
    }
}