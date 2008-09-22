/*
 * This file is part of Movie Browser.
 * 
 * Copyright (C) Francis De Brabandere
 * 
 * Movie Browser is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Movie Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.somatik.moviebrowser.cache;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.domain.MovieService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.MovieStatus;
import eu.somatik.moviebrowser.domain.StorableMovieSite;
import eu.somatik.moviebrowser.tools.FileTools;

/**
 *
 * @author francisdb
 */
@Singleton
public class FileSystemImageCache implements ImageCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemImageCache.class);
    private final Settings settings;

    @Inject
    public FileSystemImageCache(final Settings settings) {
        this.settings = settings;
    }

    
    private String imageUrl(MovieInfo info){
        return imgUrl(info, MovieService.IMDB);
    }
    
    private  String imgUrl(MovieInfo info, MovieService service) {
        String val;
        StorableMovieSite site = info.siteFor(service);
        if (site == null) {
            val = null;
        } else {
            val = info.siteFor(service).getImgUrl();
        }
        return val;
    }
    
    /**
     *
     * @param info
     */
    @Override
    public Image loadImg(MovieInfo info) {
        Image image = null;
        // TODO might accept images form other services
        String imgUrl = imageUrl(info);
        if (imgUrl != null) {
            try {
                File file = getCacheFile(imgUrl);
                if (file.exists()) {
                    image = ImageIO.read(file);
                } else {
                    LOGGER.debug("Image not available in local cache: " + imgUrl);
                }
            } catch (IOException ex) {
                LOGGER.error("Could not load image", ex);
            }
            info.setStatus(MovieStatus.LOADED);
        }
        return image;
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
                LOGGER.trace("Saving resource type: {}, modified on: {}", urlC.getContentType(), date );
                cached = getCacheFile(url);
                writeFile(is, cached);
                is.close();
                
                //If users wants album art in movie folder, save there as well. 
                if(settings.getSaveAlbumArt()) {
                    String coverURL = imageUrl(info);
                    System.out.println("COVER URL: " + coverURL);
                    File cover = null;
                    cover = getCacheFile(coverURL);
                    
                    Set<MovieLocation> locations = info.getMovie().getLocations();
                    for (MovieLocation l : locations) {
                        File save = new File(
                                new File(l.getPath()),
                                info.getMovie().getTitle() + "-cover-art.jpg");
                        FileTools.copy(cover, save);
                    }
                }
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
