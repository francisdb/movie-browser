An application that finds imdb, rotten tomatoes, flixter, movieweb, google, port.hu, cinebel, ofdb.de and omdb info by browsing though your movie folders. [Java 6.0+](http://www.java.com) required! See the [wiki](http://code.google.com/p/movie-browser/wiki/HOWTO) for more info.

**How to start the application:**
  * start it online using the webstart link below. You will see a security warning because we need to read the filesystem.
  * download the jar file, right click on it and select run with java
  * download the windows executable and start it

[![](http://www.somatik.be/images/webstart.png)](http://moviebrowser.somatik.be/moviebrowser.jnlp)

![http://movie-browser.googlecode.com/svn/site/moviebrowser-vista.png](http://movie-browser.googlecode.com/svn/site/moviebrowser-vista.png) ![http://movie-browser.googlecode.com/svn/site/moviebrowser.png](http://movie-browser.googlecode.com/svn/site/moviebrowser.png)

## News ##

### 20090217 - Version 0.8.1 released ###
  * Fixed right click menu issue for windows.

### 20090217 - Version 0.8 released ###
_Again the movie cache is incompatible!_

  * Lots and lots of bug fixes.
  * Using new flicklib release
  * Half the download size (got rid of some dependencies)
  * New import dialog
  * Extra movie service
  * Caching loaded web pages

### 20080913 - updates ###
gzsombor joins the team and is working on some improvements:

  * Support for offline storage
  * beter folder scanning
  * more subtitles support
  * xml data storage (instead of in-memory database)

the core movie fetching library has been moved to http://code.google.com/p/flicklib/ so it can be used by other projects


### 20080814 - Version 0.7 released ###
_This version introduces a new database format! The movie cache will be reset!_

  * Major bug fixes.
  * Search bar (filters in real time. searching plot text, genres, titles etc..)
  * Improved progress bar showing number of tasks.
  * Ability to check for new updates within application (**not needed for webstart users!**)
  * Ability to clear the cache.

### 20080813 - Big lists ###
  * Loading big folders might cause the site parser to hang. This issue will fe fixed in the next release (should be out soon)

### 20080807 - Version 0.6 released ###
  * Added Google Movie Info.
  * Added Flixter Movie Info.
  * Global rating now calculated from 5 sources.

  * Color coded movie list by global rating.
  * New User Interface Themes.
  * Added Apple Trailer lookup.
  * Added Watch Sample (opens sample file) and Watch Video (opens movie).
  * Subtitle Crawler, crawls subtitlesource.org and opensubtitles.org for subtitle files.

### 20080730 - Mailing list ###
  * Created mailing list for the project: [movie-browser](http://groups.google.com/group/movie-browser)

### 20080729 - Version 0.5.2 released ###
  * New edit dialog (right click on a movie)
  * Cleaner status updates using icons
  * Lots of bug fixes

### 20080712 - Version 0.5.1 released ###
  * Various small fixes
  * Ravi joined the team!