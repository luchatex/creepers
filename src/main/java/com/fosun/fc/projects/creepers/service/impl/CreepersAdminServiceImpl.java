package com.fosun.fc.projects.creepers.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.modules.mapper.BeanMapper;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersAdminDao;
import com.fosun.fc.projects.creepers.downloader.CreditChinaDungDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersAdminDTO;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdmin;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.AdminProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.AdminPiepline;
import com.fosun.fc.projects.creepers.service.ICreepersAdminService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Service
@Transactional
public class CreepersAdminServiceImpl implements ICreepersAdminService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdminProcessor adminProcessor;
    @Autowired
    private AdminPiepline adminPipeline;
    @Autowired
    private CreepersAdminDao creepersAdminDao;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @SuppressWarnings("unchecked")
    @Override
    public Page<CreepersAdminDTO> findList(Map<String, Object> searchParams, int pageNumber, int pageSize,
            String sortType) {

        PageRequest pageable = buildPageRequest(pageNumber, pageSize, sortType);
        Specification<TCreepersAdmin> spec = (Specification<TCreepersAdmin>) buildSpecification(searchParams);
        Page<TCreepersAdmin> page = creepersAdminDao.findAll(spec, pageable);
        List<TCreepersAdmin> list = page.getContent();
        List<CreepersAdminDTO> dtoList = new ArrayList<CreepersAdminDTO>();
        dtoList = BeanMapper.mapList(list, CreepersAdminDTO.class);
        Page<CreepersAdminDTO> result = new PageImpl<CreepersAdminDTO>(new ArrayList<CreepersAdminDTO>(dtoList),
                pageable, page.getTotalElements());
        return result;
    }

    @Override
    public void processByJob(String jobName) {
        //TODO NOTHING!
    }

    @Override
    public void processByRequest(String taskType) {
        logger.info("=============>>CreepersAdminServiceImpl.processByRequest start!");
        // 查询任务
        int threadNum = 30;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(taskType);
        param.setTaskType(taskType);
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, AdminProcessor.INDEX_URL);

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(adminProcessor).addPipeline(adminPipeline)
                .setDownloader(new CreditChinaDungDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);
        // 初始化Request
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(taskType);
            if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
                String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
                HashMap<String,String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String,String>>() {});
                map.put("t", String.valueOf(System.currentTimeMillis()));
                request = CommonMethodUtils.buildDefaultRequest(map,request);
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersAdminServiceImpl.processByRequest end!");

    }

    public static NameValuePair[] jsonToNameValuePair(String jsonString) {
        JSONArray jsonArray = JSON.parseArray(jsonString);
        NameValuePair[] nameValuePairs = new NameValuePair[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            String name = jsonArray.getJSONObject(i).getString("name");
            String value = jsonArray.getJSONObject(i).getString("value");
            nameValuePairs[i] = new BasicNameValuePair(name, value);
        }
        return nameValuePairs;
    }

    @Override
    public List<TCreepersAdmin> findByTypeAndKey(String type, String key) {

        return creepersAdminDao.findByKeyAndType(key, type);
    }

    @Override
    public void saveEntity(TCreepersAdmin entity) {
        creepersAdminDao.saveAndFlush(entity);
    }

    @Override
    public void saveEntity(List<TCreepersAdmin> entityList) {
            creepersAdminDao.save(entityList);
    }

    @Override
    public void saveOrUpdate(TCreepersAdmin entity) {
        List<TCreepersAdmin> oldEntityList = creepersAdminDao.findByKeyAndType(entity.getKey(),entity.getType());
        if(!CommonMethodUtils.isEmpty(oldEntityList)){
            entity.setId(oldEntityList.get(0).getId());
            entity.setUpdatedDt(new Date());
            entity.setVersion(oldEntityList.get(0).getVersion());
        }
        creepersAdminDao.saveAndFlush(entity);
    }
    
    @Override
    public void saveOrUpdate(List<TCreepersAdmin> entityList) {
        for(TCreepersAdmin entity:entityList){
            saveOrUpdate(entity);
        }
    }
}
