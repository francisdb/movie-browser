This should get you going on the project

# Introduction #

The project is maven based so everybody should be able to just check out the code and get going. The user interfaces are edited using Netbeans, so don't mess with the marked parts that are netbeans-generated.

# What you need #
  * Java JDK 6+
  * Maven 2
  * Subversion
  * Netbeans (or some other IDE if you won't be working on the gui)
  * A checkout of flicklib http://code.google.com/p/flicklib/


# Used frameworks #

These will be downloaded by maven during your first build.

  * Guice (dependency injection)
  * XStream (storage to xml)
  * Flicklib and dependencies (http connections)
  * Jericho (html parsing)
  * SwingX (gui components)
  * SLF4J/Logback (logging)

# Domain Model #

  * StorableMovie, it contains:
    * simple properties like title, director, plot, etc
    * multiple languages
    * multiple genres
    * multiple group of files (FileGroup)
  * every FileGroup objects correspond to one type of materialization of the movie. I mean it contains a set of files, which collectively represents the movie. For example, it can be :
    1. one ISO file
    1. one AVI and one SRT file
    1. multiple AVI and SRT files
    1. multiple rar files in one directory
    1. multiple rar files in two directory (which are named cd1 and cd2 currently)
> > so it contains:
    * info about the language of the audio (currently it's just one field)
    * language of the subtitle, if exists
    * release type: 1 CD, 2 CD, or DVD (of course we can extend it)
    * list of files (type: Set

&lt;StorableMovieFile&gt;

)
    * list of locations, where this particular group can be found (type: Set

&lt;MovieLocation&gt;

)

  * MovieLocation object contains the information where the files are stored.
    * path : a full filesystem path, for example /mnt/external\_drive/films/SuperFilm
    * label : user defined label, to label the file system, where it's resides, for example it can be 'My big, white external drive'. Currently there are no possibilty to customize this value on the ui.

  * every StorableMovieFile describes one file, so it contains:
    * it's name, without path, for example : 'MySuperFilm.ISO'
    * it's size
    * it's type: VIDEO\_CONTENT, SUBTITLE, NFO, COMPRESSED