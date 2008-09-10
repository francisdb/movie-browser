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
package com.flicklib.domain;

/**
 *
 * @author francisdb
 */
public class Subtitle {

    private String fileName;
    private String fileUrl;
    private String language;
    private String noCd;
    private String type;
    private String subSource;

    public Subtitle() {
        this.fileName = "";
        this.language = "";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNoCd() {
        return noCd;
    }

    public void setNoCd(String noCd) {
        this.noCd = noCd;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subtitle other = (Subtitle) obj;
        if (this.fileName != other.fileName && (this.fileName == null || !this.fileName.equals(other.fileName))) {
            return false;
        }
        if (this.fileUrl != other.fileUrl && (this.fileUrl == null || !this.fileUrl.equals(other.fileUrl))) {
            return false;
        }
        if (this.language != other.language && (this.language == null || !this.language.equals(other.language))) {
            return false;
        }
        if (this.noCd != other.noCd && (this.noCd == null || !this.noCd.equals(other.noCd))) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        if (this.subSource != other.subSource && (this.subSource == null || !this.subSource.equals(other.subSource))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 19 * hash + (this.fileUrl != null ? this.fileUrl.hashCode() : 0);
        hash = 19 * hash + (this.language != null ? this.language.hashCode() : 0);
        hash = 19 * hash + (this.noCd != null ? this.noCd.hashCode() : 0);
        hash = 19 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 19 * hash + (this.subSource != null ? this.subSource.hashCode() : 0);
        return hash;
    }
    
    
    
}
