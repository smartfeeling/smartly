/*
 *  Copyright 2006 Hannes Wallnoefer <hannes@helma.at>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.smartly.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A <code>ClassLoader</code> used for application class loading. This class
 * provides constructors and methods to add jar directories to the classpath.
 * <p/>
 * <p>Note that class lookups are cached, so adding a jar file after a class has
 * already been looked up won't make the classes available.
 */
public class SmartlyClassLoader extends URLClassLoader {

    public SmartlyClassLoader() {
        this(new URL[0]);
    }

    public SmartlyClassLoader(final URL[] urls) {
        this(urls, SmartlyClassLoader.class.getClassLoader());
    }

    public SmartlyClassLoader(final File home, final String[] classpath)
            throws MalformedURLException {
        this(new URL[0]);
        for (final String item : classpath) {
            if (item.endsWith(File.separator + "**") || item.endsWith("/**")) {
                final File dir = getAbsoluteFile(home, item.substring(0, item.length() - 2));
                this.addClasspathWildcard(dir, true);
            } else if (item.endsWith(File.separator + "*") || item.endsWith("/*")) {
                final File dir = getAbsoluteFile(home, item.substring(0, item.length() - 1));
                this.addClasspathWildcard(dir, false);
            } else {
                final File file = getAbsoluteFile(home, item);
                super.addURL(new URL("file:" + file));
            }
        }
    }

    public SmartlyClassLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    // --------------------------------------------------------------------
    //               p r o t e c t e d
    // --------------------------------------------------------------------

    protected void addClasspathWildcard(final File dir, final boolean recursive)
            throws MalformedURLException {
        if (!dir.exists()) {
            throw new IllegalArgumentException("Directory '" + dir + "' does not exist");
        } else if (!dir.isDirectory()) {
            throw new IllegalArgumentException("'" + dir + "' is not a directory");
        }
        final File[] files = dir.listFiles();
        assert files != null;
        for (final File file : files) {
            if (recursive && file.isDirectory()) {
                this.addClasspathWildcard(file, true);
            } else {
                final String name = file.getName().toLowerCase();
                if (file.isFile() && (name.endsWith(".jar") || (name.endsWith(".zip")))) {
                    super.addURL(new URL("file:" + file));
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C - p r i v a t e
    // --------------------------------------------------------------------

    private static File getAbsoluteFile(final File home, final String path) {
        final File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(home, path);
    }

}
