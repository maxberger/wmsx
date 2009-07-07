/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

/* $Id$ */

package at.ac.uibk.dps.wmsx.util;

import hu.kfki.grid.wmsx.util.Exporter;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link VirtualFile}.
 * 
 * @version $Date$
 */
public class VirtualFileTest {

    private static final int BUF_COUNT = 1024;

    private static final int BUF_SIZE = 4096;

    private static final int TESTFILESIZE = 15;

    private final File realFile;

    private final File tempDir;

    /**
     * Default constructor.
     * 
     * @throws Exception
     *             if the test cannot be prepared..
     */
    public VirtualFileTest() throws Exception {
        this.realFile = this.someTempFile();
        final InputStream is = VirtualFileTest.class.getResourceAsStream("/"
                + "testfile.txt");
        FileUtil.copy(is, this.realFile);

        this.tempDir = File.createTempFile("TestDir", null);
        this.tempDir.delete();
        this.tempDir.mkdir();
        this.tempDir.deleteOnExit();
    }

    /**
     * Tests if the file can be loaded successfully.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void loadTest() throws Exception {
        final VirtualFileImpl f = new VirtualFileImpl(this.realFile);
        final byte[] b = f.getFileContent();
        Assert.assertEquals(b.length, VirtualFileTest.TESTFILESIZE);
    }

    /**
     * Tests Serialization and loading.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void serializationTest() throws Exception {
        final File serial = this.someTempFile();
        final ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(serial));
        final VirtualFileImpl f = new VirtualFileImpl(this.realFile);
        oos.writeObject(f);
        oos.close();

        final ObjectInputStream iis = new ObjectInputStream(
                new FileInputStream(serial));
        final VirtualFileImpl f2 = (VirtualFileImpl) iis.readObject();
        iis.close();
        final byte[] b1 = f.getFileContent();
        final byte[] b2 = f2.getFileContent();
        Assert.assertEquals(b2, b1);
        Assert.assertEquals(f.getName(), this.realFile.getName());
        Assert.assertEquals(f2.getName(), this.realFile.getName());
    }

    private File someTempFile() throws IOException {
        final File serial = File.createTempFile("Test", null);
        serial.deleteOnExit();
        return serial;
    }

    private File aLargeFile() throws Exception {
        final File f = this.someTempFile();
        final FileOutputStream o = new FileOutputStream(f);
        try {
            final byte[] b = new byte[VirtualFileTest.BUF_SIZE];
            for (int i = 0; i < VirtualFileTest.BUF_COUNT; i++) {
                o.write(b);
            }
        } finally {
            o.close();
        }
        return f;
    }

    /**
     * Test for size of serialized objects.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void testSerSize() throws Exception {
        final VirtualFile f = new VirtualFileImpl(this.aLargeFile());
        final File serial = this.someTempFile();
        final ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(serial));
        oos.writeObject(f);
        oos.close();
        Assert.assertTrue(serial.length() > VirtualFileTest.BUF_COUNT
                * VirtualFileTest.BUF_SIZE, serial.length() + " > "
                + VirtualFileTest.BUF_COUNT * VirtualFileTest.BUF_SIZE);
    }

    /**
     * Test for non-existent file.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void testNullFile() throws Exception {
        try {
            new VirtualFileImpl(new File("/blabsafsadkf"));
            Assert.fail();
        } catch (final IOException io) {
            // good!
        }
    }

    /**
     * Test for size of serialized objects, when uploaded to guid.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void testSerSizeGuid() throws Exception {
        // Backends.getInstance().get("Gat").provideCredentials("toosimple",
        // "voce");
        // final VirtualFileImpl f = new VirtualFileImpl(this.aLargeFile());
        // final File serial = this.someTempFile();
        // final ObjectOutputStream oos = new ObjectOutputStream(
        // new FileOutputStream(serial));
        // oos.writeObject(f);
        // oos.close();
        // Assert.assertTrue(serial.length() < VirtualFileTest.BUF_COUNT
        // * VirtualFileTest.BUF_SIZE, "File is to large: "
        // + serial.length());
        //
        // final ObjectInputStream iis = new ObjectInputStream(
        // new FileInputStream(serial));
        // final VirtualFileImpl f2 = (VirtualFileImpl) iis.readObject();
        // iis.close();
        //
        // final byte[] b1 = f.getFileContent();
        // final byte[] b2 = f2.getFileContent();
        // Assert.assertEquals(b2, b1);
        // f.deleteTemp();
        // f2.deleteTemp();
    }

    /**
     * Test for size of serialized objects, when uploaded to guid.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test(dependsOnMethods = { "testSerSize" })
    public void testSerSizeServer() throws Exception {
        FileServerImpl.getInstance().start();
        final VirtualFileImpl f = new VirtualFileImpl(this.aLargeFile());
        final File serial = this.someTempFile();
        final ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(serial));
        oos.writeObject(f);
        oos.close();
        Assert.assertTrue(serial.length() < VirtualFileTest.BUF_COUNT
                * VirtualFileTest.BUF_SIZE, "File is to large: "
                + serial.length());

        final ObjectInputStream iis = new ObjectInputStream(
                new FileInputStream(serial));
        final VirtualFileImpl f2 = (VirtualFileImpl) iis.readObject();
        iis.close();

        final byte[] b1 = f.getFileContent();
        final byte[] b2 = f2.getFileContent();
        Assert.assertEquals(b2, b1);
        f.deleteTemp();
        f2.deleteTemp();
        Exporter.getInstance().unexportAll();
    }

}
