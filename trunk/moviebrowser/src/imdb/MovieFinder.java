/*
 * MovieFinder.java
 *
 * Created on January 20, 2007, 1:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package imdb;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.EndTag;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdesktop.http.Response;
import org.jdesktop.http.Session;

/**
 *
 * @author francisdb
 */
public class MovieFinder {
    
    
        private static final String IMDB_URLS[] = {
            "http://www.imdb.com/title/",
            "http://us.imdb.com/title/",
            "http://www.imdb.com/title?",
            "http://us.imdb.com/title?",
            "http://imdb.com/title/"
        };
        
        private static final String TO_REMOVE[] = {
            ".dvdrip",
            ".dvdivx",
            ".divx",
            ".xvid",
            ".limited",
            ".internal",
            ".proper",
            ".dc",
            ".ac3"
        };

    /**
     * Creates a new instance of MovieFinder
     */
    public MovieFinder() {
    }
   
    public void httpclient(){
        // initialize the POST method
        GetMethod get = new GetMethod("http://www.imdb.com/Tsearch?title=idiocracy");
        System.out.println(get.getQueryString());
        
        // execute the POST
        HttpClient client = new HttpClient();
        try{
            int status = client.executeMethod(get);
            String response = get.getResponseBodyAsString();
            get.releaseConnection();
            System.out.println(response);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
//    /**
//     * Runs JTidy on the source string, to produce the dest string.
//     */
//    private static String tidy(String source) {
//        try {
//            org.w3c.tidy.Tidy tidy = new org.w3c.tidy.Tidy();
//            tidy.setXHTML(true);
//            tidy.setShowWarnings(false);
//            tidy.setSmartIndent(true);
//            ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            tidy.parse(in, out);
//            in.close();
//            return out.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return source;
//        }
//    }
    
//    public void testSwingX() throws Exception{
//        Session s = new Session();
//        Response r = s.get("http://www.imdb.com/search");
//        Form form = Forms.getFormByIndex(r,1);
//        System.out.println("FORM "+form.getMethod() + "(" + form.getAction() + ")");
//        if(form != null){
//            form.getInput("s").setValue("tt");
//            form.getInput("q").setValue("idiocracy");
//            for(Input input:form.getInputs()){
//                System.out.println(input.getName()+":"+input.getValue());
//            }
//            
//            
//            r = Forms.submit(form,s);
//            System.out.println(r.getBody());
//        }
//    }
    
//    public void testDom() throws Exception{
//        
//        Session s = new Session();
//        Response r = s.get("http://www.imdb.com/Tsearch?title=idiocracy");
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        String tidyHtml = tidy(r.getBody());
//        System.out.println(tidyHtml);
//        ByteArrayInputStream in = new ByteArrayInputStream(tidyHtml.getBytes());
//        Document doc = builder.parse(in);
//        in.close();
//        
//        XPathFactory factory = XPathFactory.newInstance();
//        XPath xpath = factory.newXPath();
//        XPathExpression e = XPathUtils.compile("//form[2]");
//        Node foundNode = (Node)e.evaluate(doc, XPathConstants.NODE);
//        String href = xpath.evaluate("@action", foundNode);
//        String method = xpath.evaluate("@method", foundNode);
//        System.out.println("FORM "+method + "(" + href + ")");
//    }
    

    
    protected String findNfoUrl(File dir){
        String url = null;
        String urlStart = null;
        System.out.println("looking for nfo in "+dir.getPath());
        for(File file:dir.listFiles()){
            if(file.getName().toLowerCase().endsWith(".nfo")){
                System.out.println("checking nfo: "+file.getName());
                try{
                    FileInputStream fis = new FileInputStream(file);
                    int x= fis.available();
                    byte b[]= new byte[x];
                    fis.read(b);

                    String content = new String(b).toLowerCase();
                    int start = -1;
                    int i = 0;
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
                        System.out.println("IMDB url found: "+url);
                    }

                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        
        return url;
    }
    
    public MovieInfo getMovieInfo(MovieInfo movieInfo) throws UnknownHostException, Exception {
        movieInfo.setStatus(MovieStatus.LOADING_IMDB);
        System.out.println(movieInfo.getDirectory());
        String url = findNfoUrl(movieInfo.getDirectory());
        if(url == null){
            String title = removeCrap(movieInfo.getDirectory().getName());
            String encoded = "";
            try{
                encoded = URLEncoder.encode(title, "UTF-8");
            }catch(UnsupportedEncodingException ex){
                System.out.println(ex.getMessage());                
            }
            url = "http://www.imdb.com/Tsearch?title="+encoded;
            
        }
        
        
        movieInfo.setUrl(url);
        movieInfo.setImdbId(url.replaceAll("[a-zA-Z:/.+=?]","").trim());
        
        Source source = getParsedSource(movieInfo);
    
        String imgUrl = null;
        Element titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
        if(titleElement.getContent().extractText().contains("Title Search")){
            //find the first link
            movieInfo.setUrl(null);
            List linkElements=source.findAllElements(HTMLElementName.A);
            for (Iterator i=linkElements.iterator(); i.hasNext() && movieInfo.getUrl() == null;) {
                Element linkElement=(Element)i.next();
                String href=linkElement.getAttributeValue("href");
                System.out.println(linkElement.extractText()+ " -> " + href);
                if(href != null && href.startsWith("/title/tt")){
                    int questionMarkIndex = href.indexOf('?');
                    if(questionMarkIndex != -1){
                        href=href.substring(0, questionMarkIndex);
                    }
                    movieInfo.setUrl(href);
                    movieInfo.setImdbId(href.replaceAll("[a-zA-Z:/.+=?]","").trim());
                    source = getParsedSource(movieInfo);
                    titleElement = (Element)source.findAllElements(HTMLElementName.TITLE).get(0);
                }
            }
            
        }
        movieInfo.setTitle(titleElement.getContent().extractText());        
        
        List linkElements=source.findAllElements(HTMLElementName.A);
        for (Iterator i=linkElements.iterator(); i.hasNext();) {
            Element linkElement=(Element)i.next();
            
            if ("poster".equals(linkElement.getAttributeValue("name"))){
                
                // A element can contain other tags so need to extract the text from it:
                List imgs=linkElement.getContent().findAllElements(HTMLElementName.IMG);
                Element img = (Element)imgs.get(0);
                imgUrl = img.getAttributeValue("src");
                
                try{
                    URL imageUrl = new URL(imgUrl);
                    Image image = ImageIO.read(imageUrl);
                    movieInfo.setImage(image);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                
            }
            String href=linkElement.getAttributeValue("href");
            if(href != null && href.startsWith("/Sections/Genres/")){
                movieInfo.addGenre(linkElement.getContent().extractText());
            }         
            if(href != null && href.startsWith("/Sections/Languages/")){
                movieInfo.addLanguage(linkElement.getContent().extractText());
            }
            
        }
        
        linkElements=source.findAllElements(HTMLElementName.B);
        for (Iterator i=linkElements.iterator(); i.hasNext();) {
            Element bElement=(Element)i.next();
            if(bElement.getContent().extractText().contains("User Rating:")){
                Element next = source.findNextElement(bElement.getEndTag().getEnd());
                movieInfo.setRating(next.getContent().extractText());
                next = source.findNextElement(next.getEndTag().getEnd());
                movieInfo.setVotes(next.getContent().extractText());
            }
        }
        
        linkElements=source.findAllElements(HTMLElementName.H5);
        for (Iterator i=linkElements.iterator(); i.hasNext();) {
            Element hElement=(Element)i.next();
            if(hElement.getContent().extractText().contains("Plot")){
                int end = hElement.getEnd();
                movieInfo.setPlot(source.subSequence(end, source.findNextStartTag(end).getBegin()).toString().trim());
            }
            if(hElement.getContent().extractText().contains("Runtime")){
                int end = hElement.getEnd();
                EndTag next = source.findNextEndTag(end);
                System.out.println(next);
                String runtime = source.subSequence(end, next.getBegin()).toString().trim();
                movieInfo.setRuntime(runtime);
            }
        }
        
        if(imgUrl == null){
            //System.out.println(source.toString());
            movieInfo.setPlot("Not found");
        }
        
        new Thread(new RottenTomatoesThread(movieInfo)).start();
        
        return movieInfo;
    }
    
    
    public Source getParsedSource(MovieInfo movieInfo) throws Exception{
        Session s = new Session();
        String url = generateImdbUrl(movieInfo);
        System.out.println("Loading "+url);
        Response r = s.get(url);
        //System.out.println("HEADERS: " + Arrays.toString(r.getHeaders()));
        
        //PHPTagTypes.register();
        //PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        //MasonTagTypes.register();
        Source source = null;
        source = new Source(r.getBody());
        source.setLogWriter(new OutputStreamWriter(System.err)); // send log messages to stderr
        source.fullSequentialParse();
        return source;
    }

    
    public static String generateTomatoesUrl(MovieInfo info){
        return "http://www.rottentomatoes.com/alias?type=imdbid&s="+info.getImdbId();
    }
    
    public static String generateImdbUrl(MovieInfo info){
        String id = info.getImdbId();
        if("".equals(id)){
            return info.getUrl();
        }else{
            return "http://www.imdb.com/title/tt"+id+"/";
        }
    }
    
    private String removeCrap(String name){
        String movieName = name.toLowerCase();
        for(String bad:TO_REMOVE){
            movieName = movieName.replaceAll(bad,"");
        }
        
        Calendar calendar = new GregorianCalendar();
        int thisYear = calendar.get(Calendar.YEAR);
        
        //TODO recup the movie year!
        
        for(int i = 1800;i<thisYear;i++){
            movieName = movieName.replaceAll(Integer.toString(i),"");
        }
        int dashPos = movieName.lastIndexOf('-');
        if(dashPos != -1){
            movieName = movieName.substring(0,movieName.lastIndexOf('-'));
        }
        movieName = movieName.replaceAll("\\."," ");
        movieName = movieName.trim();
        return movieName;
    }
    
    
}
