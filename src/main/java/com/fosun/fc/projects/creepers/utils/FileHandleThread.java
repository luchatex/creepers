package com.fosun.fc.projects.creepers.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHandleThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(FileHandleThread.class);
    
    String fileName;
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        run();
    }

    public FileHandleThread(String name){
        this.fileName = name;
        start();
    }
    
    @Override
    public void run() {
        StringBuffer content = new StringBuffer();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line.replace("\"[", "[").replace("]\"", "]").replaceAll("\\\\", "")).append("\n");
            }
            reader.close();
            
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            writer.write(content.toString());
            writer.flush();
            writer.close();
            logger.info("file:{} done!",fileName);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件读取错误!  FilePath:" + fileName);
        }

        super.run();
    }
    
    public static void main(String[] args) {
        new FileHandleThread("e://2.json");

    }

}
