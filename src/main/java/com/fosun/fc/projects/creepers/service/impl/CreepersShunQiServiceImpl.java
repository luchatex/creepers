package com.fosun.fc.projects.creepers.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersCompanyListDao;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersCompanyList;
import com.fosun.fc.projects.creepers.pageprocessor.yellowPage.ShunQiProcessor;
import com.fosun.fc.projects.creepers.pipeline.yellowPage.ShunQiPipline;
import com.fosun.fc.projects.creepers.service.ICreepersShunQiService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 
 * <p>
 * description:顺企网黄页信息 Service
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-10 13:47:01
 * @see
 */
@Service("creepersShunQiServiceImpl")
public class CreepersShunQiServiceImpl implements ICreepersShunQiService {

    @Autowired
    private CreepersCompanyListDao creepersCompanyListDao;

    @Autowired
    private ShunQiProcessor shunQiProcessor;

    @Autowired
    private ShunQiPipline shunQiPipline;

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void saveEntity(TCreepersCompanyList entity) {
        if (StringUtils.isBlank(entity.getName())) {
            return;
        }
        TCreepersCompanyList oldEntity = creepersCompanyListDao.findTop1ByName(entity.getName());
        if (oldEntity != null) {
            entity.setId(oldEntity.getId());
            entity.setVersion(oldEntity.getVersion());
        }
        creepersCompanyListDao.saveAndFlush(entity);
    }

    @Override
    public void processByRequest() {
        int threadNum = 40;
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.COMPANY_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        Spider spider = Spider.create(shunQiProcessor).addPipeline(shunQiPipline)
                .setDownloader(new DungProxyDownloader().setParam(param))
                .thread(threadNum);
        for (int i = 0; i < threadNum; i++) {
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.COMPANY_LIST.getValue());
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
    }

    @Override
    public void saveAndUpdateTag(String name, BigDecimal businessTag) {
        List<TCreepersCompanyList> entityList = creepersCompanyListDao.findByName(name);
        if(entityList.size()<=0){
            return;
        }
        TCreepersCompanyList entity = entityList.get(0);
        if(businessTag.longValue()!=(entity.getBusinessTag().longValue() & businessTag.longValue())){
            entity.setBusinessTag(entity.getBusinessTag().add(businessTag));
        }
        creepersCompanyListDao.save(entity);
    }

}
