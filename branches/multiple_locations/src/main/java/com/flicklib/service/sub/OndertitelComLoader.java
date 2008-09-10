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
package com.flicklib.service.sub;

import com.google.inject.Inject;
import com.flicklib.api.SubtitlesLoader;
import com.flicklib.domain.Subtitle;
import com.flicklib.service.SourceLoader;
import java.io.IOException;
import java.util.Set;


/**
 *
 * @author francisdb
 */
public class OndertitelComLoader implements SubtitlesLoader{
    
    private final SourceLoader sourceLoader;

    /**
     * http://www.ondertitel.com
     * @param sourceLoader
     */
    @Inject
    public OndertitelComLoader(SourceLoader sourceLoader) {
        this.sourceLoader = sourceLoader;
    }

    
    
    
    

    @Override
    public Set<Subtitle> search(String localFileName, String imdbId) throws IOException {
        // http://www.ondertitel.com/?type=&trefwoord=the+dark+knight&p=zoek
        
        throw new IOException("not implemented");
    }

    
}
