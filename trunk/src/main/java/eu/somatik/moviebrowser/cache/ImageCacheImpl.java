/*
 * ImageCacheImpl.java
 *
 * Created on May 14, 2007, 11:23:30 PM
 *
 */
package eu.somatik.moviebrowser.cache;

import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.moviebrowser.config.Settings;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.imageio.ImageIO;

import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.service.InfoHandler;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author francisdb
 */
@Singleton
public class ImageCacheImpl implements ImageCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCacheImpl.class);
    private final Settings settings;
    private final InfoHandler infoHandler;

    @Inject
    public ImageCacheImpl(final Settings settings, final InfoHandler infoHandler) {
        this.settings = settings;
        this.infoHandler = infoHandler;
        // TODO refactor to be a real image cache, cached images should be saved inhere and not in the MovieInfo 
    }

    
    private String imageUrl(MovieInfo info){
        return infoHandler.imgUrl(info, MovieService.IMDB);
    }
    
    /**
     *
     * @param info
     */
    @Override
    public void loadImg(MovieInfo info) {
        StorableMovie movie = info.getMovieFile().getMovie();
        // TODO might accept images form other services
        String imgUrl = imageUrl(info);
        if (imgUrl != null) {
            Image image = null;
            try {
                File file = getCacheFile(imgUrl);
                if (file.exists()) {
                    image = ImageIO.read(file);
                } else {
                    LOGGER.debug("Image not available in local cache: " + imgUrl);
                }
                info.setImage(image);
            } catch (IOException ex) {
                LOGGER.error("Could not load image", ex);
            }
            info.setStatus(MovieStatus.LOADED);
        }
    }

    /**
     * @param imgUrl
     * @return the cached image file
     */
    private File getCacheFile(String imgUrl) {
        File cached = null;
        String startAfter = "imdb.com/";
        int startIndex = imgUrl.indexOf(startAfter) + startAfter.length();
        String cacheName = imgUrl.substring(startIndex);
        cacheName = cacheName.replaceAll("/", "_");
        cached = new File(settings.getImageCacheDir(), cacheName);
        return cached;
    }

    @Override
    public void removeImgFromCache(MovieInfo info) {
        String url = imageUrl(info);
        if (url != null) {
            File file = new File(url);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * @param info 
     * @return the saved file
     */
    @Override
    public File saveImgToCache(MovieInfo info) {
        File cached = null;
        String url = imageUrl(info);
        if (url != null) {
            InputStream is = null;
            try {
                URL imgUrl = new URL(url);
                URLConnection urlC = imgUrl.openConnection();
                // Copy resource to local file, use remote file
                // if no local file name specified
                is = new BufferedInputStream(imgUrl.openStream());
                // Print info about resource
                Date date = new Date(urlC.getLastModified());
                LOGGER.info("Saving resource (type: " + urlC.getContentType() + ", modified on: " + date + ")...");
                cached = getCacheFile(url);
                writeFile(is, cached);
                is.close();
            } catch (MalformedURLException ex) {
                LOGGER.error("Could not save image '" + url + "'", ex);
            } catch (IOException ex) {
                LOGGER.error("Could not save image '" + url + "'", ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        LOGGER.error("Could not close input stream", ex);
                    }
                }
            }
        }
        return cached;
    }
    // TODO try nio
    //            // Create channel on the source
//        FileChannel srcChannel = new FileInputStream("srcFilename").getChannel();
//    
//        // Create channel on the destination
//        FileChannel dstChannel = new FileOutputStream("dstFilename").getChannel();
//    
//        // Copy file contents from source to destination
//        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
//    
//        // Close the channels
//        srcChannel.close();
//        dstChannel.close();
    private void writeFile(InputStream inStream, File file) throws IOException {
        final int bufferSize = 1024;
        OutputStream fout = null;
        try {
            fout = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[bufferSize];
            int readCount = 0;
            while ((readCount = inStream.read(buffer)) != -1) {
                if (readCount < bufferSize) {
                    fout.write(buffer, 0, readCount);
                } else {
                    fout.write(buffer);
                }
            }
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close FileOutputStream", ex);
                }
            }
        }
    }
}
