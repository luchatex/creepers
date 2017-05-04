package com.fosun.fc.projects.creepers.getResource;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateCreditRegNoURL extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(GenerateCreditRegNoURL.class);

    public static boolean checkFilePath(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            if (file.exists()) {
                file.mkdirs();
                logger.info("文件路径不存在，已创建路径。");
            } else {
                logger.info("路径存在。");
            }
            return true;
        } else {
            if (file.exists()) {
                logger.info("文件存在");
                return true;
            } else {
                logger.info("文件不存在");
                return false;
            }
        }
    }

    public static void generateUrlJsonFile(int start, int times) {
        int end = start + 200;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        for (int i = start; i < end; i++) {
            List<String> regansCodeList = new GenCodeTest().getOrgansList();
            String organsCode = regansCodeList.get(i);
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    GenCodeTest genCodeTest = new GenCodeTest();
                    genCodeTest.combineCode(organsCode, times * 10000);
                }
            });
        }
        fixedThreadPool.shutdown();
    }

    public static void main(String[] args) {
        GenerateCreditRegNoURL.generateUrlJsonFile(0, 0);

    }
}
