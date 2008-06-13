package eu.somatik.moviebrowser.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author francisdb
 *
 */
public class FileSystemScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemScanner.class);

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
    public File findSample(File folder){
    	File sample = null;
    	for(File file:folder.listFiles()){
    		if("sample".equals(file.getName().toLowerCase())){
    			for(File sampleFolderFile:file.listFiles()){
    				if(sampleFolderFile.getName().toLowerCase().endsWith(".avi")){
    					sample = sampleFolderFile;
    				}
    			}
    		}
    	}
    	return sample;
    }
    
    /**
     * Finds the NFO file and looks for the imdb url inside it
     * @param dir
     * @return the nfo URL or null
     */
    public String findNfoUrl(File dir){
        String url = null;
        LOGGER.debug("looking for nfo in "+dir.getPath());
        for(File file:dir.listFiles()){
            if(file.getName().toLowerCase().endsWith(".nfo")){
                LOGGER.debug("checking nfo: "+file.getName());
                url = findImdbUrl(file); 
            }
        }
        
        return url;
    }
    
    private String findImdbUrl(File nfoFile){
    	String url = null;
    	try{
            FileInputStream fis = new FileInputStream(nfoFile);
            int x= fis.available();
            byte b[]= new byte[x];
            fis.read(b);
            
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
        }
        return url;
    }
    
}
