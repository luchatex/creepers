package com.fosun.fc.projects.creepers.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersCompanyListDao;
import com.fosun.fc.projects.creepers.dao.CreepersTaskListDao;
import com.fosun.fc.projects.creepers.dao.CreepersTempShixinDao;
import com.fosun.fc.projects.creepers.dao.CreepersTotalNameListDao;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskList;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;
import com.fosun.fc.projects.creepers.entity.TCreepersTempShixin;
import com.fosun.fc.projects.creepers.exception.CreeperException;
import com.fosun.fc.projects.creepers.monitor.EchoAnnotation;
import com.fosun.fc.projects.creepers.redis.service.IRedisScheduleService;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskTemplateService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;
import com.google.common.collect.Lists;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.HttpConstant;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CreepersTaskListServiceImpl implements ICreepersTaskListService {

    @Autowired
    private CreepersTaskListDao creepersTaskListDao;

    @Autowired
    protected RedisScheduler redisSchedul;

    @Autowired
    private IRedisScheduleService redisScheduleServiceImpl;

    @Autowired
    private CreepersCompanyListDao creepersCompanyListDao;

    @Autowired
    private CreepersTotalNameListDao creepersTotalNameListDao;

    @Autowired
    private CreepersTempShixinDao creepersTempShixinDao;

    @Autowired
    private ICreepersTaskTemplateService creepersTaskTemplateServiceImpl;

    @Override
    public void initTaskList(TCreepersTaskTemplate taskTemplate, String startIndex, String endIndex, String session) {
        int startIdx = StringUtils.isBlank(startIndex) ? 1 : Integer.valueOf(startIndex);
        int endIdx = StringUtils.isBlank(endIndex) ? 1 : Integer.valueOf(endIndex);
        int totalReq;
        int annualReq;
        int storedReq = 0;
        List<String> sqlList = new ArrayList<String>();
        if (BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue().equals(taskTemplate.getTaskType())
                || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue()
                        .equals(taskTemplate.getTaskType())
                || BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue().equals(taskTemplate.getTaskType())
                || BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue().equals(taskTemplate.getTaskType())) {
            int lastIndex = taskTemplate.getUrl().lastIndexOf("&Id") == -1 ? taskTemplate.getUrl().length()
                    : taskTemplate.getUrl().lastIndexOf("&Id");
            String indexUrl = taskTemplate.getUrl().substring(0, lastIndex);
            int maxIndex;
            try {
                maxIndex = creepersTaskListDao
                        .selectUrlMaxIndexByTaskTypeAndKeyWordAndKeyWordLen(taskTemplate.getTaskType(), "id=", 3) + 1;
            } catch (Exception e1) {
                maxIndex = 0;
            }
            // 规则暂定为前端下标最大值大于数据库下标最大值时,才进行url初始化处理
            if (endIdx >= maxIndex) {
                startIdx = Integer.max(startIdx, maxIndex);
                totalReq = endIdx - startIdx + 1;
                annualReq = totalReq / 100;
                try {
                    for (int i = startIdx; i <= endIdx; i++) {
                        String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','" + indexUrl
                                + "&Id=" + i + "','" + taskTemplate.getHttpType() + "','" + taskTemplate.getParamMap()
                                + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)";
                        sqlList.add(sql);
                        storedReq += 1;
                        if (totalReq < 100) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        } else {
                            if (i % annualReq == 0 || i == endIdx) {
                                JdbcUtil.execute(sqlList);
                                sqlList.clear();
                                EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                            }
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        } else if (BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue().equals(taskTemplate.getTaskType())
                || BaseConstant.TaskListType.ADMIN_SACTION_LIST.getValue().equals(taskTemplate.getTaskType())) {
            int maxIndex;
            try {
                maxIndex = creepersTaskListDao
                        .selectUrlMaxIndexByTaskTypeAndKeyWordAndKeyWordLen(taskTemplate.getTaskType(), "page=", 5) + 1;
            } catch (Exception e1) {
                maxIndex = 0;
            }
            if (endIdx >= maxIndex) {
                startIdx = Integer.max(startIdx, maxIndex);
                totalReq = endIdx - startIdx + 1;
                annualReq = totalReq / 100;
                try {
                    for (int i = startIdx; i <= endIdx; i++) {
                        String paramMap = "";
                        if (StringUtils.isNotBlank(taskTemplate.getParamMap())) {
                            Map<String, Object> map = JSON.parseObject(taskTemplate.getParamMap());
                            map.put("page", i + "");
                            paramMap = JSON.toJSONString(map);
                        }
                        String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                + taskTemplate.getUrl() + i + "','" + taskTemplate.getHttpType() + "','" + paramMap
                                + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)";
                        sqlList.add(sql);
                        storedReq += 1;
                        if (totalReq < 100) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        } else {
                            if (i % annualReq == 0 || i == endIdx) {
                                JdbcUtil.execute(sqlList);
                                sqlList.clear();
                                EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                            }
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            } else {
                EchoAnnotation.echo(session, "1|1");
            }

        } else if (BaseConstant.TaskListType.COMPANY_LIST.getValue().equals(taskTemplate.getTaskType())) {
            totalReq = endIdx - startIdx + 1;
            annualReq = totalReq / 100;
            try {
                for (int i = startIdx; i <= endIdx; i++) {
                    String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                            + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                            + taskTemplate.getUrl() + i + ".htm','" + taskTemplate.getHttpType() + "','','"
                            + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)";
                    sqlList.add(sql);
                    storedReq += 1;
                    if (totalReq < 100) {
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                    } else {
                        if (i % annualReq == 0 || i == endIdx) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } else if (BaseConstant.TaskListType.DISHONEST_BAIDU_LIST.getValue().equals(taskTemplate.getTaskType())) {
            int maxIndex;
            try {
                maxIndex = creepersTaskListDao.selectUrlMaxIndexByTaskTypeAndKeyWordAndKeyWordLen(
                        taskTemplate.getTaskType(), "creditinfo/", 11) + 1;
            } catch (Exception e1) {
                maxIndex = 0;
            }
            if (endIdx >= maxIndex) {
                int step = 50;
                startIdx = Integer.max(startIdx, maxIndex);
                totalReq = endIdx / step - startIdx / step + 1;
                annualReq = totalReq / 100;
                try {
                    for (int i = startIdx / step; i <= endIdx / step; i++) {
                        String paramMap = "";
                        if (StringUtils.isNotBlank(taskTemplate.getParamMap())) {
                            Map<String, Object> map = JSON.parseObject(taskTemplate.getParamMap());
                            paramMap = JSON.toJSONString(map);
                        }
                        String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                + taskTemplate.getUrl() + i * step + "','" + taskTemplate.getHttpType() + "','"
                                + paramMap + "','" + taskTemplate.getMemo()
                                + "','0','0','admin',sysdate,'admin',sysdate)";
                        sqlList.add(sql);
                        storedReq += 1;
                        if (totalReq < 100) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        } else {
                            if (i % annualReq == 0 || i == endIdx) {
                                JdbcUtil.execute(sqlList);
                                sqlList.clear();
                                EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                            }
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        } else if (BaseConstant.TaskListType.ALL_CHINA_DATA.getValue().equals(taskTemplate.getTaskType())) {
            totalReq = endIdx - startIdx + 1;
            annualReq = totalReq / 100;
            try {
                for (int i = startIdx; i <= endIdx; i++) {
                    String paramMap = "";
                    if (StringUtils.isNotBlank(taskTemplate.getParamMap())) {
                        Map<String, Object> map = JSON.parseObject(taskTemplate.getParamMap());
                        map.put("page", i);
                        paramMap = JSON.toJSONString(map);
                    }
                    String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                            + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                            + taskTemplate.getUrl() + i + "','" + taskTemplate.getHttpType() + "','" + paramMap
                            + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)";
                    sqlList.add(sql);
                    storedReq += 1;
                    if (totalReq < 100) {
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                    } else {
                        if (i % annualReq == 0 || i == endIdx) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } else if (BaseConstant.TaskListType.DISHONEST_BAIDU_LIST_BY_NAME_LIST.getValue()
                .equals(taskTemplate.getTaskType())) {
            endIdx = creepersCompanyListDao.findMaxId();
            totalReq = creepersCompanyListDao.countByIdGreaterThanAndIdLessThanEqual(startIdx, endIdx);
            if (totalReq > 0) {
                try {
                    if (totalReq < 1000) {
                        List<String> names = creepersCompanyListDao.findByIdGreaterThanAndIdLessThanEqual(startIdx,
                                endIdx);
                        for (String name : names) {
                            String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                    + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                    + taskTemplate.getUrl() + name + "','" + taskTemplate.getHttpType() + "','"
                                    + taskTemplate.getParamMap() + "','" + taskTemplate.getMemo()
                                    + "','0','0','admin',sysdate,'admin',sysdate)";
                            sqlList.add(sql);
                            storedReq += 1;
                        }
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                    } else {
                        annualReq = (int) Math.ceil((double) (endIdx - startIdx) / 100);
                        for (int i = 1; i <= 100; i++) {
                            List<String> names = creepersCompanyListDao.findByIdGreaterThanAndIdLessThanEqual(
                                    startIdx + (i - 1) * annualReq, i == 100 ? endIdx : startIdx + i * annualReq);
                            for (String name : names) {
                                StringBuilder sql = new StringBuilder();
                                if (name.contains("CO.") || name.contains("Co.") || name.contains("co.")
                                        || name.contains("co,") || name.contains("CO .,") || name.contains("Inc.")
                                        || name.contains("Ltd.") || name.contains("LTD.") || name.contains("LTD.")
                                        || name.contains("LIMITED") || name.contains("'") || name.contains(".com")
                                        || name.contains("办事处") || name.contains("个体经营")) {
                                } else {
                                    sql.append(
                                            "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                                    + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType()
                                                    + "','" + taskTemplate.getUrl() + name + "','"
                                                    + taskTemplate.getHttpType() + "','" + taskTemplate.getParamMap()
                                                    + "','" + taskTemplate.getMemo()
                                                    + "','0','0','admin',sysdate,'admin',sysdate)");
                                }
                                if (StringUtils.isNoneBlank(sql.toString()))
                                    sqlList.add(sql.toString());
                                storedReq += 1;
                            }
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                taskTemplate.setMemo(endIdx + "");
                creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        } else if (BaseConstant.TaskListType.DISHONEST_BAIDU_LIST_BY_TOTAL_NAME_LIST.getValue()
                .equals(taskTemplate.getTaskType())) {
            endIdx = creepersTotalNameListDao.findMaxId();
            totalReq = creepersTotalNameListDao.countByIdGreaterThanAndIdLessThanEqual(startIdx, endIdx);
            if (totalReq > 0) {
                try {
                    if (totalReq < 1000) {
                        List<String> names = creepersTotalNameListDao.findByIdGreaterThanEqualAndIdLessThanEqual(startIdx,
                                endIdx);
                        for (String name : names) {
                            String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                    + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                    + taskTemplate.getUrl() + name + "','" + taskTemplate.getHttpType() + "','"
                                    + taskTemplate.getParamMap() + "','" + taskTemplate.getMemo()
                                    + "','0','0','admin',sysdate,'admin',sysdate)";
                            sqlList.add(sql);
                            storedReq += 1;
                        }
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                    } else {
                        annualReq = (int) Math.ceil((double) (endIdx - startIdx) / 100);
                        for (int i = 1; i <= 100; i++) {
                            List<String> names = creepersTotalNameListDao.findByIdGreaterThanEqualAndIdLessThanEqual(
                                    i == 1 ? startIdx:startIdx + (i - 1) * annualReq + 1, i == 100 ? endIdx : startIdx + i * annualReq);
                            for (String name : names) {
                                StringBuilder sql = new StringBuilder();
                                sql.append(
                                        "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType()
                                                + "','" + taskTemplate.getUrl() + name + "','"
                                                + taskTemplate.getHttpType() + "','" + taskTemplate.getParamMap()
                                                + "','" + taskTemplate.getMemo()
                                                + "','0','0','admin',sysdate,'admin',sysdate)");
                                if (StringUtils.isNoneBlank(sql.toString()))
                                    sqlList.add(sql.toString());
                                storedReq += 1;
                            }
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                taskTemplate.setMemo(endIdx + "");
                creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        } else if (BaseConstant.TaskListType.TEMP_SHIXIN_LIST.getValue().equals(taskTemplate.getTaskType())) {
            endIdx = creepersTempShixinDao.findMaxId();
            totalReq = creepersTempShixinDao.countByIdGreaterThanAndIdLessThanEqual(startIdx, endIdx);
            if (totalReq > 0) {
                try {
                    if (totalReq < 1000) {
                        List<TCreepersTempShixin> entityList = creepersTempShixinDao
                                .findByIdGreaterThanAndIdLessThanEqual(startIdx, endIdx);
                        for (TCreepersTempShixin entity : entityList) {
                            StringBuilder sql = new StringBuilder();
                            sql.append(
                                    "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                            + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                            + taskTemplate.getUrl() + entity.getCardNo() + "&iname="
                                            + entity.getCardName() + "','" + taskTemplate.getHttpType() + "','"
                                            + taskTemplate.getParamMap() + "','" + taskTemplate.getMemo()
                                            + "','0','0','admin',sysdate,'admin',sysdate)");
                            sqlList.add(sql.toString());
                            storedReq += 1;
                        }
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                    } else {
                        annualReq = (int) Math.ceil((double) (endIdx - startIdx) / 100);
                        for (int i = 1; i <= 100; i++) {
                            List<TCreepersTempShixin> entityList = creepersTempShixinDao
                                    .findByIdGreaterThanAndIdLessThanEqual(startIdx + (i - 1) * annualReq,
                                            i == 100 ? endIdx : startIdx + i * annualReq);
                            for (TCreepersTempShixin entity : entityList) {
                                StringBuilder sql = new StringBuilder();
                                sql.append(
                                        "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType()
                                                + "','" + taskTemplate.getUrl() + entity.getCardNo() + "&iname="
                                                + entity.getCardName() + "','" + taskTemplate.getHttpType() + "','"
                                                + taskTemplate.getParamMap() + "','" + taskTemplate.getMemo()
                                                + "','0','0','admin',sysdate,'admin',sysdate)");
                                if (StringUtils.isNoneBlank(sql.toString()))
                                    sqlList.add(sql.toString());
                                storedReq += 1;
                            }
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                taskTemplate.setMemo(endIdx + "");
                creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        }else {
            int maxIndex;
            try {
                maxIndex = creepersTaskListDao.selectUrlMaxIndexByTaskTypeAndKeyWordAndKeyWordLen(
                        taskTemplate.getTaskType(), "creditinfo/", 11) + 1;
            } catch (Exception e1) {
                maxIndex = 0;
            }
            if (endIdx >= maxIndex) {
                startIdx = Integer.max(startIdx, maxIndex);
                totalReq = endIdx - startIdx + 1;
                annualReq = totalReq / 100;
                try {
                    for (int i = startIdx; i <= endIdx; i++) {
                        String paramMap = "";
                        if (StringUtils.isNotBlank(taskTemplate.getParamMap())) {
                            Map<String, Object> map = JSON.parseObject(taskTemplate.getParamMap());
                            paramMap = JSON.toJSONString(map);
                        }
                        String sql = "insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','"
                                + taskTemplate.getUrl() + i + "','" + taskTemplate.getHttpType() + "','" + paramMap
                                + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)";
                        sqlList.add(sql);
                        storedReq += 1;
                        if (totalReq < 100) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                            EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                        } else {
                            if (i % annualReq == 0 || i == endIdx) {
                                JdbcUtil.execute(sqlList);
                                sqlList.clear();
                                EchoAnnotation.echo(session, storedReq + "|" + totalReq);
                            }
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            } else {
                EchoAnnotation.echo(session, "1|1");
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initTaskList(TCreepersTaskTemplate taskTemplate, long startIndex, long endIndex, String session){
        List<String> sqlList = new ArrayList<String>();
        //生成分页url
        if (!taskTemplate.getTaskType().contains("by_total_name_list")) {
            //入库url的总数
            long total = endIndex-startIndex+1;
            //入库的次数
            long times = 100;
            //单次入库的个数
            long step = (long)Math.ceil((double)total/times);
            long stored = 0;
            try {
                for (long i = startIndex; i <= endIndex; i++) {
                    String url = !taskTemplate.getUrl().contains(":?")?taskTemplate.getUrl()+i:taskTemplate.getUrl().replace(":?", i+"");
                    
                    String param = !taskTemplate.getParamMap().contains(":?")?taskTemplate.getParamMap():taskTemplate.getParamMap().replace(":?", i+"");
                    StringBuilder sql = new StringBuilder();
                    sql.append("insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                            + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','" + url + "','" + taskTemplate.getHttpType() + "','" + param
                            + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)");
                    sqlList.add(sql.toString());
                    stored += 1;
                    if (total < 100) {
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                    } else {
                        if (i % step == 0 || i == endIndex) {
                            JdbcUtil.execute(sqlList);
                            sqlList.clear();
                        }
                    }
                    EchoAnnotation.echo(session, stored + "|" + total);
                }
                taskTemplate.setMemo(endIndex + "");
                creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            if(endIndex<=0)
                endIndex = creepersTotalNameListDao.findMaxId();
            long total = creepersTotalNameListDao.countByIdGreaterThanAndIdLessThanEqual(startIndex, endIndex);
            long stored = 0;
            try {
                if (total < 500000) {
                    List<String> names = creepersTotalNameListDao.findByIdGreaterThanEqualAndIdLessThanEqual(startIndex,
                            endIndex);
                    for (String name : names) {
                        String url = !taskTemplate.getUrl().contains(":?")?taskTemplate.getUrl():taskTemplate.getUrl().replace(":?", name);
                        String param = !taskTemplate.getParamMap().contains(":?")?taskTemplate.getParamMap():taskTemplate.getParamMap().replace(":?", name);
                        StringBuilder sql = new StringBuilder();
                        sql.append("insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','" + url + "','" + taskTemplate.getHttpType() + "','" + param
                                + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)");
                        sqlList.add(sql.toString());
                        stored += 1;
                    }
                    JdbcUtil.execute(sqlList);
                    sqlList.clear();
                    taskTemplate.setMemo(endIndex + "");
                    creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
                    EchoAnnotation.echo(session, stored + "|" + total);
                } else {
                    //入库的次数
                    long times = 100;
                    //单次入库的个数
                    long step = (long)Math.ceil((double)endIndex/times);
                    for (int i = 1; i <= 100; i++) {
                        List<String> names = creepersTotalNameListDao.findByIdGreaterThanEqualAndIdLessThanEqual(
                                i==1?startIndex:startIndex + (i - 1) * step + 1, i == 100 ? endIndex : startIndex + i * step);
                        for (String name : names) {
                            String url = taskTemplate.getUrl().contains(":?")?taskTemplate.getUrl().replace(":?", name):taskTemplate.getUrl();
                            String param = taskTemplate.getParamMap().contains(":?")?taskTemplate.getParamMap().replace(":?", name):taskTemplate.getParamMap();
                            StringBuilder sql = new StringBuilder();
                            sql.append("insert into t_creepers_task_list(id,task_type,url,http_type,param_map,memo,flag,version,created_by,created_dt,updated_by,updated_dt)"
                                    + "values(seq_creepers_list.nextval,'" + taskTemplate.getTaskType() + "','" + url + "','" + taskTemplate.getHttpType() + "','" + param
                                    + "','" + taskTemplate.getMemo() + "','0','0','admin',sysdate,'admin',sysdate)");
                            sqlList.add(sql.toString());
                            stored += 1;
                        }
                        JdbcUtil.execute(sqlList);
                        sqlList.clear();
                        EchoAnnotation.echo(session, stored + "|" + total);
                    }
                    taskTemplate.setMemo(endIndex + "");
                    creepersTaskTemplateServiceImpl.saveOrUpdateEntity(taskTemplate);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEntity(TCreepersTaskList entity) {
        creepersTaskListDao.save(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEntity(List<TCreepersTaskList> entityList) {
        creepersTaskListDao.save(entityList);
    }

    @Override
    public void updateMedicalTaskList(String url, String flag) {
        TCreepersTaskList entity = this.findTop1ByUrl(url);
        if (entity != null) {
            entity.setFlag(flag);
            entity.setUpdatedDt(new Date());
            creepersTaskListDao.saveAndFlush(entity);
        }
    }

    @Override
    public List<Object[]> countByTaskType(String taskType) {

        return creepersTaskListDao.countByTaskType(taskType);
    }

    @Override
    public Long countByTaskTypeAndFlag(String taskType, String flag) {

        return creepersTaskListDao.countByTaskTypeAndFlag(taskType, flag);
    }

    @Override
    public List<TCreepersTaskList> findByTaskTypeAndFlag(String taskType, String flag) {

        return creepersTaskListDao.findByTaskTypeAndFlag(taskType, flag);
    }

    @Override
    public List<TCreepersTaskList> findByTaskTypeAndFlagNot(String taskType, String flag) {

        return creepersTaskListDao.findByTaskTypeAndFlagNot(taskType, flag);
    }

    @Override
    public List<TCreepersTaskList> findByTaskTypeAndFlagIn(String taskType, Collection<String> flagList) {

        return creepersTaskListDao.findByTaskTypeAndFlagIn(taskType, flagList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFlagByUrl(String url, String flag) {

        creepersTaskListDao.updateFlagByUrl(url, flag);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
    @Override
    public void pushRequest(List<TCreepersTaskList> resultList, String processType, long step) {

        if (resultList == null || resultList.size() < 1) {
            return;
        }

        List<Request> requestList = new ArrayList<Request>();
        String taskType = StringUtils.EMPTY;
        for (int i = resultList.size()-1; i>=0; i--) {
            TCreepersTaskList entity = resultList.remove(i);
            if (entity != null) {
                taskType = entity.getTaskType();
                if (StringUtils.EMPTY.equals(taskType)) {
                    continue;
                }
                String httpType = entity.getHttpType();
                Request request = null;
                if (HttpConstant.Method.POST.equalsIgnoreCase(httpType)) {
                    request = CommonMethodUtils.buildDefaultRequest(MapUtils.EMPTY_MAP, entity.getUrl());
                    request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING, entity.getParamMap());
                } else if (HttpConstant.Method.GET.equalsIgnoreCase(httpType)) {
                    request = CommonMethodUtils.buildGetRequestCarryMap(entity.getUrl());
                } else {
                    throw new CreeperException("CreepersTaskListServiceImpl.pushRequest方法中的entity的httptype不能为null");
                }
                requestList.add(request);
            }
        }
        redisSchedul.push(taskType, requestList);
        this.updateMedicalTaskListByProcessType(taskType, processType, BaseConstant.TaskListFlag.PROCESSING.getValue(),
                step);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TCreepersTaskList> findTop100000ByTaskType(String taskListType) {
        return creepersTaskListDao.findTop100000ByTaskType(taskListType);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TCreepersTaskList> findTop100000ByTaskTypeAndFlagNotOrderById(String taskListType, String flag) {
        return creepersTaskListDao.findTop100000ByTaskTypeAndFlagNotOrderByIdAsc(taskListType, flag);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public Request popRequest(String taskListType) {
        Request request = redisSchedul.pop(taskListType);
        if (request != null) {
            updateMedicalTaskList(request.getUrl(), BaseConstant.TaskListFlag.FAIL.getValue());
        }
        return request;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public CreepersLoginParamDTO popParam(String taskListType) {
        return redisSchedul.popParam(taskListType);
    }
    
    @Override
    public void saveAndFlush(TCreepersTaskList entity) {
        TCreepersTaskList oldEntity = creepersTaskListDao.findTop1ByUrl(entity.getUrl());
        if (oldEntity != null) {
            entity.setId(oldEntity.getId());
            entity.setVersion(oldEntity.getVersion());
        }
        creepersTaskListDao.saveAndFlush(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public TCreepersTaskList findTop1ByUrl(String url) {
        return creepersTaskListDao.findTop1ByUrl(url);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteByTaskType(String taskType) {
        creepersTaskListDao.deleteByTaskType(taskType);
    }

    @Override
    public void updateMedicalTaskListByProcessType(String taskType, String processType, String flag, long step) {
        if (BaseConstant.ProcessByRedisType.WHOLE_DATA.getValue().equalsIgnoreCase(processType)) {
            updateTaskListFlagByTaskTypeAndFlagNot(taskType, BaseConstant.TaskListFlag.PROCESSING.getValue(), flag,
                    step);
        } else if (BaseConstant.ProcessByRedisType.WHOLE_DATA_MAX_10W.getValue().equalsIgnoreCase(processType)) {
            updateTop100000TaskListFlagByTaskTypeAndFlagNotOrderById(taskType,
                    BaseConstant.TaskListFlag.PROCESSING.getValue(), flag);
        } else if (BaseConstant.ProcessByRedisType.BREAK_POINT_DATA.getValue().equalsIgnoreCase(processType)) {
            this.updateTaskListFlagByTaskTypeAndFlagIn(taskType, Lists.newArrayList("0", "2"), flag, step);
        }

    }

    @Override
    public void updateTaskListFlagByTaskTypeAndFlagIn(String taskType, List<String> flagList, String newFlag,
            long step) {
        creepersTaskListDao.updateTaskListFlagByTaskTypeAndFlagIn(taskType, flagList, newFlag, step);
    }

    @Override
    public void updateTop100000TaskListFlagByTaskTypeAndFlagNotOrderById(String taskType, String flag, String newFlag) {
        creepersTaskListDao.updateTop100000TaskListFlagByTaskTypeAndFlagNotOrderById(taskType, flag, newFlag);
    }

    @Override
    public void updateTaskListFlagByTaskTypeAndFlagNot(String taskType, String flag, String newFlag, long step) {
        creepersTaskListDao.updateTaskListFlagByTaskTypeAndFlagNot(taskType, flag, newFlag, step);
    }

    @Override
    public void clearRedisCache(String taskType) {

        redisScheduleServiceImpl.delete(taskType);
        creepersTaskListDao.updateTaskListFlagByTaskTypeAndFlag(taskType, "3", "0");
    }
}
