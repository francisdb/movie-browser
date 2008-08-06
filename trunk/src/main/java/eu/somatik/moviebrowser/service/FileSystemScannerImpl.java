package eu.somatik.moviebrowser.service;

import com.google.inject.Singleton;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author francisdb
 *
 */
@Singleton
public class FileSystemScannerImpl implements FileSystemScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemScannerImpl.class);

    private static final String IMDB_URLS[] = {
        "http://www.imdb.com/title/",
        "http://us.imdb.com/title/",
        "http://www.imdb.com/title?",
        "http://us.imdb.com/title?",
        "http://imdb.com/title/"
    };
	 
    
    /**
     * Locates she sample file and returns it
     * @param folder
     * @return the sample file or null if not found 
     */
    @Override
    public File findSample(File folder){
    	File sample = null;
        if(folder.isDirectory()){
            for(File file:folder.listFiles()){
                    if("sample".equals(file.getName().toLowerCase())){
                            for(File sampleFolderFile:file.listFiles()){
                                    if(sampleFolderFile.getName().toLowerCase().endsWith(".avi")){
                                            sample = sampleFolderFile;
                                    }
                            }
                    }
            }
        }
    	return sample;
    }
    
    /**
     * Locates parent directory name and returns it
     * @param folder
     * @return the parent dir name or null if not found 
     */
    @Override
    public File findParentDirectory(File folder) {
        File name = null;
        name = folder.getParentFile();
        return name;
    }
    
    /**
     * Finds the NFO file and looks for the imdb url inside it
     * @param dir
     * @return the nfo URL or null
     */
    @Override
    public String findNfoImdbUrl(File dir){
        String url = null;
        if(dir.isDirectory()){
            for(File file:dir.listFiles()){
                if(file.getName().toLowerCase().endsWith(".nfo")){
                    LOGGER.debug("checking nfo: "+file.getName());
                    url = findImdbUrl(file); 
                }
            }
        }
        return url;
    }
    
    private String findImdbUrl(File nfoFile){
    	String url = null;
        DataInputStream dis = null;
        FileInputStream fis = null;
    	try{
            fis = new FileInputStream(nfoFile);
            dis = new DataInputStream(fis);
            int x= fis.available();
            byte b[]= new byte[x];
            dis.readFully(b);
            
            String content = new String(b).toLowerCase();
            int start = -1;
            int i = 0;
            String urlStart = null;
            while(start == -1 && i < IMDB_URLS.length){
                urlStart = IMDB_URLS[i];
                //System.out.println("looking for "+urlStart);
                start = content.indexOf(urlStart);
                i++;
            }
            
            if(start != -1){
                i = start + urlStart.length();
                int end = -1;
                char character;
                while(i < content.length()-1 && end == -1){
                    character = content.charAt(i);
                    //System.out.println((int)character);
                    if(character == '\n' || character == '\t' || character == '\n' || character == (char)32){
                        end = i;
                    }
                    i++;
                }
                
                //if the url is the end of the file
                if(i == content.length()-1){
                    end = content.length()-1;
                }
                
                url = content.substring(start,end);
                LOGGER.info("IMDB url found: "+url);
            }
            
        }catch(IOException ex){
            LOGGER.error("Could not find IMDB url", ex);
        }finally{
            if(dis != null){
                try {
                    dis.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close nfo file", ex);
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close nfo file", ex);
                }
            }
        }
        return url;
    }
    
}
