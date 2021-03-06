/*
* (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
* Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import me.tongfei.progressbar.ProgressBar;

/**
 * Shared methods
 * @author mikahama
 */
public class CommonTools {

    /**
     * Deletes a directory and its contents
     * @param dirFile Directory to be deleted
     */
    public static void deleteDir(File dirFile) {
        if (dirFile.isDirectory()) {
            File[] dirs = dirFile.listFiles();
            for (File dir : dirs) {
                deleteDir(dir);
            }
        }
        dirFile.delete();
    }

    /**
     * Downloads a text file and returns it as a String
     * @param targetURL URL of the text file
     * @return The contents of the file as a string
     * @throws IOException Fails if the file cannot be downloaded
     */
    public static String readToString(String targetURL) throws IOException {
        URL url = new URL(targetURL);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(url.openStream()));

        StringBuilder stringBuilder = new StringBuilder();

        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(inputLine);
            stringBuilder.append(System.lineSeparator());
        }

        bufferedReader.close();
        return stringBuilder.toString().trim();
    }

    /**
     * Downloads a file from the internet and saves it to the disk
     * @param url URL of the file
     * @param filePath Path where to save the file
     * @param showProgress true to print out a progress bar
     * @throws Exception May fail if the URL cannot be reached or the file cannot be written
     */
    public static void downloadToFile(String url, String filePath, boolean showProgress) throws Exception {
        ProgressBar pb = null;
        BufferedInputStream in = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL urlObject = new URL(url);
            int contentLength = urlObject.openConnection().getContentLength();

            if (contentLength != -1 && showProgress) {
                pb = new ProgressBar("Downloading", contentLength);
                pb.start();
            } else {
                System.out.println("Not showing download progress");
            }

            in = new BufferedInputStream(urlObject.openStream());
            fileOutputStream = new FileOutputStream(filePath);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                if (pb != null) {
                    pb.stepBy(1024);
                }

            }
        } finally {
            if (pb != null) {
                pb.stop();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

}
