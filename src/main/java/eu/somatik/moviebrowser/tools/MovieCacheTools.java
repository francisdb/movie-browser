package eu.somatik.moviebrowser.tools;

import com.flicklib.module.FlicklibModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.XmlMovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.module.MovieBrowserModule;

public class MovieCacheTools {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MovieBrowserModule(), new FlicklibModule());
        Settings settings = injector.getInstance(Settings.class);
        
        MovieCache cache = injector.getInstance(MovieCache.class);

        XmlMovieCache xml = new XmlMovieCache(settings);
        xml.startup();
        for (StorableMovie m : cache.list()) {
            m.setId(null);
            xml.insertOrUpdate(m);
        }
        xml.shutdown();
        
    }

}
