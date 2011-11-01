package eu.somatik.moviebrowser.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flicklib.folderscanner.AdvancedFolderScanner;
import com.flicklib.folderscanner.AsyncMonitor;
import com.flicklib.folderscanner.FileGroup;
import com.flicklib.folderscanner.FileLocation;
import com.flicklib.folderscanner.FileMeta;
import com.flicklib.folderscanner.Scanner;

import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.domain.Language;
import eu.somatik.moviebrowser.domain.MovieInfo;
import eu.somatik.moviebrowser.domain.MovieLocation;
import eu.somatik.moviebrowser.domain.StorableMovie;
import eu.somatik.moviebrowser.domain.StorableMovieFile;

public class FolderScannerImpl implements FolderScanner {

    Map<String, Language> localeMap = new HashMap();
    final static Logger LOG = LoggerFactory.getLogger(FolderScannerImpl.class);

    Scanner scanner;
    DefaultFileSystemManager manager;

    public FolderScannerImpl() {
        this(new AdvancedFolderScanner());
    }

    public FolderScannerImpl(Scanner advancedFolderScanner) {
        this.scanner = advancedFolderScanner;
        this.manager = new DefaultFileSystemManager();
        try {
            this.manager.addProvider("file", new DefaultLocalFileProvider());
            this.manager.init();
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
        this.localeMap.put("en", Language.ENGLISH);
        this.localeMap.put("hu", Language.HUNGARIAN);
        this.localeMap.put("nl", Language.DUTCH);
    }

    @Override
    public List<MovieInfo> scan(Set<String> folders, AsyncMonitor monitor) {
        Set<FileObject> roots = new HashSet<FileObject>();
        final URI rootUri = new java.io.File("").toURI();
        for (String folder : folders) {
            try {
                String path = rootUri.resolve(folder).toString();
                FileObject file = manager.resolveFile(path);
                roots.add(file);
            } catch (FileSystemException e) {
                LOG.warn("error : " + e.getMessage(), e);
            }
        }

        List<FileGroup> scanResult = scanner.scan(roots, monitor);
        List<MovieInfo> result = new ArrayList<MovieInfo>();
        for (FileGroup f : scanResult) {
            StorableMovie movie = new StorableMovie();
            movie.setTitle(f.getTitle());
            eu.somatik.moviebrowser.domain.FileGroup fg = new eu.somatik.moviebrowser.domain.FileGroup();
            fg.setAudio(localeMap.get(f.getAudioLanguage().getLanguage()));
            for (FileLocation fl : f.getLocations()) {
                fg.addLocation(new MovieLocation(fl.getLabel(), fl.getPath().toString()));
            }
            for (FileMeta fm : f.getFiles()) {
                fg.addFile(new StorableMovieFile(fm.getName(), fm.getSize(), fm.getType()));
            }
            movie.getGroups().add(fg);
            result.add(new MovieInfo(movie));
        }
        return result;
    }

}
