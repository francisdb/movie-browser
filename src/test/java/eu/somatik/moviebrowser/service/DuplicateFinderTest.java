package eu.somatik.moviebrowser.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flicklib.folderscanner.MovieFileType;

import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.domain.FileGroup;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

public class DuplicateFinderTest {

    private static MockDatabase mdb;
    private static File rootDir;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        mdb = new MockDatabase();
        rootDir = new File("target/test-classes/structure3");
        
        {
            FileGroup fg = new FileGroup();
            fg.addFile(new StorableMovieFile("spaceballs.avi", 0 , MovieFileType.VIDEO_CONTENT));
            fg.addLocation(new MovieLocation("/some-imaginary/path", "cloud"));
            StorableMovie movie = new StorableMovie();
            movie.setTitle("Spaceballs");
            movie.addFileGroup(fg);
            mdb.insertOrUpdate(movie);
        }
        
        {
            FileGroup fg = new FileGroup();
            fg.addFile(new StorableMovieFile("titanic.iso", 0 , MovieFileType.VIDEO_CONTENT));
            fg.addLocation(new MovieLocation(new File(rootDir, "romance").getAbsolutePath(), "test-server"));
            StorableMovie movie = new StorableMovie();
            movie.setTitle("Titanic");
            movie.addFileGroup(fg);
            mdb.insertOrUpdate(movie);
        }
    }

    private FolderScanner scanner;
    private DuplicateFinder duplicateFinder;
    
    @Before
    public void setUp() {
        this.scanner = new FolderScannerImpl();
        duplicateFinder = new DuplicateFinder(mdb);
    }

    @Test
    public void testDuplicateDetection() {
        Set<String> folders = new HashSet<String>();
        folders.add("target/test-classes/structure3");
        List<MovieInfo> movies = scanner.scan(folders, null);

        {
            assertEquals(4, movies.size());
            Map<String, MovieInfo> map = AdvancedFolderScannerTest.map(movies);
    
            assertNotNull("love and more", map.get("love and more"));
            assertNotNull("titanic", map.get("titanic"));
            assertNotNull("spaceballs", map.get("spaceballs"));
            assertNotNull("startrek", map.get("startrek"));
        }
        List<MovieInfo> filtered = duplicateFinder.filter(movies);
        {
            // check for proper filter count
            assertEquals(2, filtered.size());
            Map<String, MovieInfo> map = AdvancedFolderScannerTest.map(filtered);
            assertNull("titanic", map.get("titanic"));
            assertNull("spaceballs", map.get("spaceballs"));
            assertNotNull("love and more", map.get("love and more"));
            assertNotNull("startrek", map.get("startrek"));
        }

        {
            // check for Spaceballs has another new location
            
            StorableMovie movieByTitle = mdb.findMovieByTitle("Spaceballs");
            assertNotNull("SpaceBalls found", movieByTitle);
            assertEquals("1 filegroup", 1, movieByTitle.getGroups().size());
            FileGroup fileGroup = movieByTitle.getUniqueFileGroup();
            assertEquals("found at 2 location", 2, fileGroup.getLocations().size());
            assertNotNull("at some imaginary path", fileGroup.getMovieLocationIfExists("/some-imaginary/path"));
            assertNotNull("at the target path", fileGroup.getMovieLocationIfExists(new File(rootDir,"scifi").getAbsolutePath()));
        }
        {
            // check for Titanic has just one location
            
            StorableMovie movieByTitle = mdb.findMovieByTitle("Titanic");
            assertNotNull("Titanic found", movieByTitle);
            assertEquals("1 filegroup", 1, movieByTitle.getGroups().size());
            FileGroup fileGroup = movieByTitle.getUniqueFileGroup();
            assertEquals("found at 1 location", 1, fileGroup.getLocations().size());
            assertNotNull("at the target path", fileGroup.getMovieLocationIfExists(new File(rootDir,"romance").getAbsolutePath()));
        }
    }
    
}
