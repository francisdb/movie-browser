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
package eu.somatik.moviebrowser.service.export;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A locator for exporters
 * @author francisdb
 */
@Singleton
class ExporterLocatorImpl implements ExporterLocator {

    private final HashMap<String, Exporter> registry;

    @Inject
    ExporterLocatorImpl() {
        // TODO make this a singleton? or use guice in the plugins as well?
        this.registry = new HashMap<String, Exporter>();

        // this should be done automatically by java
        // http://java.sun.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider
        register(new HTMLExporter());
    }

    @Override
    public void register(Exporter exporter){
        registry.put(exporter.getName(), exporter);
    }

    @Override
    public Iterator<String> list(){
        return registry.keySet().iterator();
    }

    @Override
    public Exporter get(String name){
        return registry.get(name);
    }
    
    

}
