package com.fosun.fc.projects.creepers.pipeline.baidu;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersBusinessTag;
import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;
import com.fosun.fc.projects.creepers.pipeline.BasePipeline;
import com.fosun.fc.projects.creepers.service.ICreepersBusinessTagService;
import com.fosun.fc.projects.creepers.service.ICreepersDishonestBaiduService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTotalNameListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@Component
public class DishonestBaiduPipeline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersDishonestBaiduService creepersDishonestBaiduServiceImpl;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;
    @Autowired
    private ICreepersBusinessTagService creepersBusinessTagServiceImpl;
    @Autowired
    private ICreepersTotalNameListService creepersTotalNameListServiceImpl;
    
    public DishonestBaiduPipeline() {
        setPath("/data/webmagic/");
    }

    public DishonestBaiduPipeline(String path) {
        setPath(path);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("======================>>>  DishonestBaiduPipeline.Process start!");
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        String currentUrl= resultItems.get(BaseConstant.CURRENT_URL);
        // 增加异常处理
        try {
            // 获取爬虫解析的数据
            List<Map<String, Object>> mapList = resultItems
                    .get(CreepersConstant.TableNamesOthers.T_CREEPERS_DISHONEST_BAIDU.getMapKey());
            // 判断resultList是否为空，不为空更新数据并更新list表的状态为成功，为空更新list表的状态为失败并处理异常
            if (!CommonMethodUtils.isEmpty(mapList)) {
                // map转entity
                for (Map<String, Object> map : mapList) {
                    TCreepersDishonestBaidu entity = new TCreepersDishonestBaidu();
                    CommonMethodUtils.mapTransEntity(map, entity);
                    CommonMethodUtils.setByDT(entity);
                    creepersDishonestBaiduServiceImpl.saveEntity(entity);
                }
            logger.info("======================>>>  DishonestBaiduPipeline.Process end!");
            }
            if (StringUtils.isNotBlank(currentUrl)) {
                creepersTaskListServiceImpl.updateMedicalTaskList(currentUrl, BaseConstant.TaskListFlag.COMPLETED.getValue());
            }
            if(currentUrl.contains("&iname=")){
                TCreepersBusinessTag businessTag = creepersBusinessTagServiceImpl.findTop1ByTaskType(param.getTaskType());
//                creepersCompanyListServiceImpl.saveAndUpdateTag(currentUrl.substring(currentUrl.lastIndexOf("&iname=")+7), businessTag.getBusinessTag());
                creepersTotalNameListServiceImpl.saveAndUpdateTag(currentUrl.substring(currentUrl.lastIndexOf("&iname=")+7), businessTag.getBusinessTag());
            }
        } catch (Exception e) {
            logger.error("step:  ======>>  DishonestBaiduPipeline  save FALSE!!!");
            logger.error("write DB error", e);
            param.setErrorInfo("write DB error" + e);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
            logger.error("======================>>>  DishonestBaiduPipeline.Process end!");
        }
    }
}
