package com.fosun.fc.projects.creepers.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersTycBaseInfoDao;
import com.fosun.fc.projects.creepers.downloader.TianYanChaSeleniumDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersTycBaseInfo;
import com.fosun.fc.projects.creepers.pageprocessor.TianYanChaProcessor;
import com.fosun.fc.projects.creepers.pipeline.HtmlToJsonFilePipline;
import com.fosun.fc.projects.creepers.pipeline.TianYanChalPipline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTianYanChaBaseInfoService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 
 *<p>
 *description:天眼查工商基础信息Service
 *</p>
 * @author MaXin
 * @since 2017-2-9 00:18:05
 * @see
 */
@Service("creepersTianYanChaBaseInfoServiceImpl")
public class CreepersTianYanChaBaseInfoServiceImpl implements ICreepersTianYanChaBaseInfoService {

    @Autowired
    private CreepersTycBaseInfoDao creepersTycBaseInfoDao;
    
    @Autowired(required = true)
    private TianYanChaProcessor tianYanChaProcessor;

    @Autowired(required = true)
    private TianYanChalPipline tianYanChalPipline;
    
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Autowired(required = true)
    private TianYanChaSeleniumDownloader tianYanChaSeleniumDownloader;
    
    @Override
    public void saveEntity(TCreepersTycBaseInfo entity) {
        if (StringUtils.isBlank(entity.getName())) {
            return;
        }
        TCreepersTycBaseInfo oldEntity = creepersTycBaseInfoDao.findTop1ByName(entity.getName());
        if (oldEntity != null) {
            entity.setId(oldEntity.getId());
            entity.setVersion(oldEntity.getVersion());
        }
        creepersTycBaseInfoDao.saveAndFlush(entity);
    }

    @Override
    public void processByRequest() {
    	int threadNum = 1;
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.TYC_BASE_INFO.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue());
        Spider.create(tianYanChaProcessor).addPipeline(tianYanChalPipline)
                .addPipeline(new HtmlToJsonFilePipline())
                .setDownloader(tianYanChaSeleniumDownloader.setParam(param))
                .addRequest(request)
                .thread(threadNum).run();

    }

    @Override
    public List<TCreepersTycBaseInfo> findListByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
