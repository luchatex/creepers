package com.fosun.fc.projects.creepers.dto;

/**
 *
 * <p>
 * description: T_CREEPERS_TASK_LIST 爬虫任务队列表
 * <p>
 * 
 * @author LiZhanping
 * @since 2016-12-14 10:26:05
 * @see
 */

public class CreepersTaskListDTO extends CreepersBaseDTO {
    private static final long serialVersionUID = -53058612750502214L;
    // 任务类型
    private String taskType;
    // 任务链接
    private String url;
    // 报文交互方式：GET/POST
    private String httpType;
    // 参数说明
    private String paramMap;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpType() {
        return httpType;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public String getParamMap() {
        return paramMap;
    }

    public void setParamMap(String paramMap) {
        this.paramMap = paramMap;
    }

}
