/*
 * * Copyright 2018 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reflxction.commandaliaser.utils;

import net.minecraftforge.fml.common.FMLLog;
import net.reflxction.commandaliaser.CommandAliaser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.core.helpers.Strings;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * A class with different utility methods for file management
 */
public class FileUtil {

    /**
     * Copies and saves an embedded resource in the mod JAR
     *  @param resourcePath Path to the resource
     *
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveResource(String resourcePath) {
        if (Strings.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath may not be null");
        }
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        Validate.notNull(resourcePath, "The embedded resource '%s' cannot be found", resourcePath);
        File outFile = new File(CommandAliaser.DATA_DIRECTORY, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(CommandAliaser.DATA_DIRECTORY, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if ((!outFile.exists()) && in != null) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }  //FMLLog.warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");

        } catch (IOException ex) {
            FMLLog.severe("Could not save " + outFile.getName() + " to " + outFile);
            ex.printStackTrace();
        }
    }

    /**
     * Returns an {@link InputStream} of the given file name, or {@code null} if not found
     *
     * @param filename Name of the file
     * @return The input stream of the file
     */
    private static InputStream getResource(String filename) {
        Validate.notNull(filename, "filename may not be null");

        try {
            URL url = CommandAliaser.class.getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Creates a directory forcibly.
     *
     * @param file The directory to create
     */
    public static void forceMkdir(File file) {
        try {
            FileUtils.forceMkdir(file);
        } catch (IOException e) {
            FMLLog.severe("Failed to create directory " + file.getName());
            e.printStackTrace();
        }
    }

    public static FileConfiguration loadFile(String path) {
        FileConfiguration config = null;
        try {
            File file = new File(CommandAliaser.DATA_DIRECTORY, path);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                saveResource(path);
            }
            config = new YamlConfiguration();
            config.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return config;
    }

}
