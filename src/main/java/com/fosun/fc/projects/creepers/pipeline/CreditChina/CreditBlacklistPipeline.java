package com.fosun.fc.projects.creepers.pipeline.CreditChina;

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
import com.fosun.fc.projects.creepers.entity.TCreepersCreditBlackList;
import com.fosun.fc.projects.creepers.pipeline.BasePipeline;
import com.fosun.fc.projects.creepers.service.ICreepersCreditBlackListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@Component
public class CreditBlacklistPipeline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersCreditBlackListService creepersCreditBlackListServiceImpl;

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    public CreditBlacklistPipeline() {
        setPath("/data/webmagic/");
    }

    public CreditBlacklistPipeline(String path) {
        setPath(path);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @SuppressWarnings("unchecked")
    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("======================>>>  CorporateBondsBlacklistPipeline start!");
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        // 增加异常处理
        try {
            // 获取爬虫解析的数据
            List<Map<String, Object>> corpBondsMapList = resultItems
                    .get(CreepersConstant.TableNamesCreditChina.T_CREEPERS_CREDIT_BLACK_LIST.getMapKey());
            String url = resultItems.get(BaseConstant.CURRENT_URL);
            if (!CommonMethodUtils.isEmpty(corpBondsMapList)) {
                // map转entity
                List<TCreepersCreditBlackList> entityList = CommonMethodUtils.mapList(corpBondsMapList,
                        new TCreepersCreditBlackList());
                for (TCreepersCreditBlackList entity : entityList) {
                    CommonMethodUtils.setByDT(entity);
                    if (StringUtils.isNotBlank(entity.getName())) {
                        creepersCreditBlackListServiceImpl.saveOrUpdate(entity);
                    }
                }
                // url从redis缓存中取出后状态是2（失败），如果成功获取数据则将状态改为1（成功）
                if (null != entityList && !entityList.isEmpty()) {
                    creepersTaskListServiceImpl.updateMedicalTaskList(url, "1");
                }
            }
            logger.info("======================>>>  CreditBlacklistPipeline end!");
        } catch (Exception e) {
            logger.error("step:  ======>>  CreditBlacklistPipeline  save FALSE!!!");
            logger.error("write DB error", e);
            param.setErrorInfo("write DB error" + e);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
            logger.error("======================>>>  CreditBlacklistPipeline.Process end!");
        }

    }

}
