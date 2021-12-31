/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
 *
 * @author mikahama
 */
public class CommonTools {
    
    public static void deleteDir(File dirFile) {
    if (dirFile.isDirectory()) {
        File[] dirs = dirFile.listFiles();
        for (File dir: dirs) {
            deleteDir(dir);
        }
    }
    dirFile.delete();
}
    
public static String readToString(String targetURL) throws IOException
{
    URL url = new URL(targetURL);
    BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(url.openStream()));

    StringBuilder stringBuilder = new StringBuilder();

    String inputLine;
    while ((inputLine = bufferedReader.readLine()) != null)
    {
        stringBuilder.append(inputLine);
        stringBuilder.append(System.lineSeparator());
    }

    bufferedReader.close();
    return stringBuilder.toString().trim();
}

    public static void downloadToFile(String url, String filePath, boolean showProgress) throws Exception {
        
        URL urlObject = new URL(url);
        int contentLength = urlObject.openConnection().getContentLength();
        ProgressBar pb = null;
        if(contentLength != -1 && showProgress){
            pb = new ProgressBar("Downloading", contentLength);
            pb.start();
        }else{
            System.out.println("Not showing download progress");
        }
        
        BufferedInputStream in = new BufferedInputStream(urlObject.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
            if (pb != null){
                pb.stepBy(1024);
            }
            
        }
        if (pb != null){
        pb.stop();
        }
        
        fileOutputStream.close();
        in.close();
    }

}
