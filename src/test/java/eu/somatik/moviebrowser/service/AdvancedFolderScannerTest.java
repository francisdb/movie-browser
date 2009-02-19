package eu.somatik.moviebrowser.service;

import eu.somatik.moviebrowser.domain.MovieInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class AdvancedFolderScannerTest {


    private AdvancedFolderScanner scanner;

    @Before
    public void setUp() {
        this.scanner = new AdvancedFolderScanner(new MovieNameExtractor());
    }

    @Test
    public void testStructure1() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure1");
        List<MovieInfo> movies = scanner.scan(folders);
        assertEquals(3, movies.size());
    }

    @Test
    public void testStructure2() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure2");
        List<MovieInfo> movies = scanner.scan(folders);
        assertEquals(3, movies.size());
    }

    @Test
    public void testStructure3() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure3");
        List<MovieInfo> movies = scanner.scan(folders);
        assertEquals(3, movies.size());
    }

}