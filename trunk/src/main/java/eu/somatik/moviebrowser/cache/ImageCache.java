/*
 * ImageCache.java
 *
 * Created on May 14, 2007, 11:23:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.moviebrowser.cache;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieStatus;

/**
 *
 * @author francisdb
 */
public class ImageCache {
    
    
    private ImageCache() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     *
     * @param info
     */
    public static void loadImg(MovieInfo info){
        String imgUrl = info.getMovie().getImgUrl();
        if(imgUrl != null){
            info.setStatus(MovieStatus.LOADING_IMG);
            try{
                File file = getCacheFile(imgUrl);
                if(file.exists()){
                    Image image = ImageIO.read(file);
                    info.setImage(image);
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
            info.setStatus(MovieStatus.LOADED);
        }
    }
    
    /**
     * @param imgUrl
     * @return the cached image file
     */
    public static File getCacheFile(String imgUrl){
        File cached = null;
        String startAfter = "imdb.com/";
        int startIndex = imgUrl.indexOf(startAfter)+startAfter.length();
        String cacheName = imgUrl.substring(startIndex);
        cacheName = cacheName.replaceAll("/", "_");
        cached = new File(Settings.getImageCacheDir(),cacheName);
        return cached;
    }
    
    /**
     * @param info
     * @return the saved file
     */
    public static File saveImgToCache(MovieInfo info){
        File cached = null;
        FileOutputStream fos = null;
        try{
            URL imgUrl = new URL(info.getMovie().getImgUrl());
            URLConnection urlC = imgUrl.openConnection();
            // Copy resource to local file, use remote file
            // if no local file name specified
            InputStream is = imgUrl.openStream();
            // Print info about resource
            System.out.print("Copying resource (type: " + urlC.getContentType());
            Date date=new Date(urlC.getLastModified());
            System.out.println(", modified on: " + date + ")...");
            
            cached = getCacheFile(info.getMovie().getImgUrl());
            fos = new FileOutputStream(cached);
            int oneChar, count=0;
            while ((oneChar=is.read()) != -1) {
                fos.write(oneChar);
                count++;
            }
            is.close();
            fos.close();
            System.out.println(count + " byte(s) copied");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageCache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return cached;
    }
}
