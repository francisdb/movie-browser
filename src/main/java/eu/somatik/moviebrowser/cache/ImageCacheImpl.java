/*
 * ImageCacheImpl.java
 *
 * Created on May 14, 2007, 11:23:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.moviebrowser.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.Movie;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;
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

    @Inject
    public ImageCacheImpl() {
        // TODO refactor to be a real image cache, cached images should be saved inhere and not in the MovieInfo
        
    }

    /**
     *
     * @param info
     */
    @Override
    public void loadImg(MovieInfo info) {
        String imgUrl = info.getMovie().getImgUrl();
        if (imgUrl != null) {
            info.setStatus(MovieStatus.LOADING_IMG);
            Image image = null;
            try {
                File file = getCacheFile(imgUrl);
                if (file.exists()) {
                    image = ImageIO.read(file);
                }else{
                    LOGGER.debug("Image not available in local cache: "+imgUrl);
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
        cached = new File(Settings.getImageCacheDir(), cacheName);
        return cached;
    }

    @Override
    public void removeImgFromCache(Movie movie) {
        if (movie.getImgUrl() != null) {
            File file = new File(movie.getImgUrl());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * @param movie 
     * @return the saved file
     */
    @Override
    public File saveImgToCache(Movie movie) {
        File cached = null;
        if(movie.getImgUrl() != null){
            InputStream is = null;
            try {
                URL imgUrl = new URL(movie.getImgUrl());
                URLConnection urlC = imgUrl.openConnection();
                // Copy resource to local file, use remote file
                // if no local file name specified
                is = new BufferedInputStream(imgUrl.openStream());
                // Print info about resource
                Date date = new Date(urlC.getLastModified());
                LOGGER.info("Saving resource (type: " + urlC.getContentType() + ", modified on: " + date + ")...");
                cached = getCacheFile(movie.getImgUrl());
                writeFile(is, cached);
                is.close();
            } catch (MalformedURLException ex) {
                LOGGER.error("Could not save image '"+movie.getImgUrl()+"'", ex);
            } catch (IOException ex) {
                LOGGER.error("Could not save image '"+movie.getImgUrl()+"'", ex);
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
