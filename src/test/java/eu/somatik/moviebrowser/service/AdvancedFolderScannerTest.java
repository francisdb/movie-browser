package eu.somatik.moviebrowser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.domain.MovieInfo;

/**
 *
 * @author francisdb
 */
public class AdvancedFolderScannerTest {


    private FolderScanner scanner;

    @Before
    public void setUp() {
        this.scanner = new FolderScannerImpl();
    }

    @Test
    public void testStructure1() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure1");
        List<MovieInfo> movies = scanner.scan(folders, null);
        assertEquals(3, movies.size());
        Map<String, MovieInfo> map = map(movies);
        assertNotNull("hackers", map.get("hackers"));
        assertNotNull("pulp fiction", map.get("pulp fiction"));
        assertNotNull("lost 2008", map.get("lost 2008"));
    }

    @Test
    public void testStructure2() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure2");
        List<MovieInfo> movies = scanner.scan(folders, null);
        assertEquals(3, movies.size());
        Map<String, MovieInfo> map = map(movies);
        assertNotNull("dumb and dumber", map.get("dumb and dumber"));
        assertNotNull("forest gump", map.get("forest gump"));
        assertNotNull("lost in translation", map.get("lost in translation"));
    }

    @Test
    public void testStructure3() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure3");
        List<MovieInfo> movies = scanner.scan(folders, null);
        assertEquals(4, movies.size());
        Map<String, MovieInfo> map = map(movies);
        assertNotNull("love and more", map.get("love and more"));
        assertNotNull("titanic", map.get("titanic"));
        assertNotNull("spaceballs", map.get("spaceballs"));
        assertNotNull("startrek", map.get("startrek"));
    }
    
    public static Map<String,MovieInfo> map(List<MovieInfo> movies) {
        Map<String,MovieInfo> result = new HashMap<String,MovieInfo>();
        for (MovieInfo movie:movies) {
            result.put(movie.getMovie().getTitle(), movie);
        }
        return result;
    }

}