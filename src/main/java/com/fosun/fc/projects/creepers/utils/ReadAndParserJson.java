package com.fosun.fc.projects.creepers.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;

import us.codecraft.webmagic.Request;

public class ReadAndParserJson extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ReadAndParserJson.class);

    public boolean checkFilePath(String filePath) {
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

    public String readFile2String(String fileFullName) {
        StringBuffer content = new StringBuffer();
        if (checkFilePath(fileFullName)) {
            File file = new File(fileFullName);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    content.append(line.replace("\"[", "[").replace("]\"", "]").replaceAll("\\\\", "")).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("文件读取错误!  FilePath:" + fileFullName);
            }
        }
        return content.toString();
    }

    @SuppressWarnings("unchecked")
    public void readFileToReplaceContent(String filePath) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(12);
        File file = new File(filePath);
        String[] fileList = file.list();
        RedisScheduler redisScheduler = new RedisScheduler();
        for (String fileName : fileList) {
            fileName = filePath + "/" + fileName;
            File childFile = new File(fileName);
            String[] childFileList = childFile.list();
            for (String childFileName : childFileList) {
                String dealFile = fileName + "/" + childFileName;
                fixedThreadPool.execute(new Runnable() {
                    public void run() {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dealFile))));
                            String line = "";
                            List<Request> requestList = new ArrayList<Request>();
                            while ((line = reader.readLine()) != null) {
                                JSONArray jsonArr = JSON.parseArray(line);
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                                    Request request = CommonMethodUtils.buildDefaultRequest(MapUtils.EMPTY_MAP, jsonObj.getString("URL"));
                                    request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING, jsonObj.getString("PARAM_MAP"));
                                    requestList.add(request);
                                }
                            }
                            reader.close();
                            redisScheduler.push(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue(), requestList);
                            logger.info("file:{} done!", dealFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                            logger.error("文件读取错误!  FilePath:" + dealFile);
                        }
                    }
                });
            }
        }
        fixedThreadPool.shutdown();
    }

    public String readJsonArray(String fileFullName) {
        JSONArray jsonArr = JSON.parseArray(readFile2String(fileFullName));
        // JSONObject jsonArr = JSON.parseObject(readFile2String(fileFullName));
        logger.info(jsonArr.toJSONString());
        return jsonArr.toJSONString();
    }

    public static void main(String[] args) {
        // readJsonArray("e://2.json");
        new ReadAndParserJson().readFileToReplaceContent("D:/businessRegNo/dealing");
    }
}
