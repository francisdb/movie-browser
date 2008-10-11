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
package eu.somatik.moviebrowser.module;

import com.google.inject.AbstractModule;
import eu.somatik.moviebrowser.cache.ImageCache;
import eu.somatik.moviebrowser.cache.FileSystemImageCache;
import eu.somatik.moviebrowser.cache.MovieCache;
import eu.somatik.moviebrowser.cache.JPAMovieCache;
import eu.somatik.moviebrowser.cache.XmlMovieCache;
import eu.somatik.moviebrowser.config.Settings;
import eu.somatik.moviebrowser.config.SettingsImpl;
import eu.somatik.moviebrowser.gui.IconLoader;
import eu.somatik.moviebrowser.api.FileSystemScanner;
import eu.somatik.moviebrowser.service.FileSystemScannerImpl;
import eu.somatik.moviebrowser.api.FolderScanner;
import eu.somatik.moviebrowser.service.MovieFinder;
import eu.somatik.moviebrowser.service.MovieNameExtractor;
import eu.somatik.moviebrowser.service.AdvancedFolderScanner;
import eu.somatik.moviebrowser.service.InfoHandler;
import eu.somatik.moviebrowser.service.InfoHandlerImpl;
import eu.somatik.moviebrowser.service.ScoreCalculator;
import eu.somatik.moviebrowser.service.WeightedScoreCalculator;
/**
 * Guice configuration module
 * @author fdb
 */
public class MovieBrowserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MovieFinder.class);
        bind(MovieNameExtractor.class);
        bind(IconLoader.class);


        //bind(MovieCache.class).to(JPAMovieCache.class);
        bind(MovieCache.class).to(XmlMovieCache.class);
        bind(ImageCache.class).to(FileSystemImageCache.class);
        bind(FolderScanner.class).to(AdvancedFolderScanner.class);
        //bind(FolderScanner.class).to(SimpleFolderScanner.class);
        bind(FileSystemScanner.class).to(FileSystemScannerImpl.class);
        bind(Settings.class).to(SettingsImpl.class);
        bind(InfoHandler.class).to(InfoHandlerImpl.class);
        bind(ScoreCalculator.class).to(WeightedScoreCalculator.class);

    }
}
