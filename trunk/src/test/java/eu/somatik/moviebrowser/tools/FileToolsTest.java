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
package eu.somatik.moviebrowser.tools;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author francisdb
 */
public class FileToolsTest {


    /**
     * Test of deleteDirectory method, of class FileTools.
     */
    @Test
    public void testDeleteDirectory() {
        File file = new File("target/junit/test/dir");
        file.mkdirs();
        boolean result = FileTools.deleteDirectory(file.getParentFile().getParentFile());
        assertTrue(result);
        assertFalse(file.exists());
    }

    /**
     * Test of renameDir method, of class FileTools.
     */
    @Test
    public void testRenameDir() {
        File file = new File("target/junit/test");
        file.mkdirs();
        File file2 = new File("target/junit2");
        boolean result = FileTools.renameDir(file.getParentFile(), file2);
        assertTrue(result);
        assertTrue(file2.exists());
        assertFalse(file.exists());
        // clean up
        FileTools.deleteDirectory(file2);
    }

    @Test
    public void testCopyStreams() throws FileNotFoundException, IOException{
        File file = new File("target/test.tmp");
        File file2 = new File("target/test2.tmp");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] b = new byte[1024];
        for(int i = 0; i < b.length; i++){
            b[i] = 1;
        }
        for(long l = 0;l<1024L*10L;l++){
            fos.write(b);
        }
        fos.close();
        FileInputStream is = new FileInputStream(file);
        FileOutputStream os = new FileOutputStream(file2);
        long total = FileTools.copy(is, os);
        is.close();
        os.close();
        file.delete();
        file2.delete();
        assertEquals(1024L*10L*b.length, total);
    }
   
    /**
     * Test of close method, of class FileTools.
     */
    @Test
    public void testClose() {
        FileTools.close(null);
        Closeable closeable = new MockCloseable(true);
        FileTools.close(closeable);

        MockCloseable closeable2 = new MockCloseable(false);
        FileTools.close(closeable2);
        assertTrue(closeable2.isClosed());
    }

    private class MockCloseable implements Closeable{
        private final boolean throwError;
        private boolean closed;

        public MockCloseable(final boolean throwError) {
            this.throwError = throwError;
        }

        @Override
        public void close() throws IOException {
            if(throwError){
                throw new IOException("Mock exception");
            }
            this.closed = true;
        }

        public boolean isClosed() {
            return closed;
        }

    }

}