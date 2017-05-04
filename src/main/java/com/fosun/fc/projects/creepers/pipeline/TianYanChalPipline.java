package com.fosun.fc.projects.creepers.pipeline;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersTycBaseInfo;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTianYanChaBaseInfoService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 
 * <p>
 * 天眼查工商基础信息入库
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-8 23:42:38
 * @see
 */
@Component("tianYanChalPipline")
public class TianYanChalPipline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Autowired
    private ICreepersTianYanChaBaseInfoService creepersTianYanChaBaseInfoServiceImpl;

    public TianYanChalPipline() {
        setPath("/data/webmagic/");
    }

    public TianYanChalPipline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("step:  ======>>  entry TianYanChalPipline process!");
        // 数据入库
        Map<String, String> resultMap = resultItems.get(CreepersConstant.TableNamesOthers.T_CREEPERS_TYC_BASE_INFO.getMapKey());
        String currentUrl = resultItems.get(BaseConstant.CURRENT_URL);
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        if (param == null) {
            return;
        }
        param.setErrorPath(getClass().toString());
        try {
            if (StringUtils.isNotBlank(currentUrl)) {
                if (resultMap != null && !resultMap.isEmpty()) {
                    TCreepersTycBaseInfo entity = new TCreepersTycBaseInfo();
                    CommonMethodUtils.mapTransEntity(resultMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    logger.info("step:  ======>>  creepersTianYanChaBaseInfoServiceImpl.saveEntity START!");
                    creepersTianYanChaBaseInfoServiceImpl.saveEntity(entity);
                    logger.info("step:  ======>>  creepersTianYanChaBaseInfoServiceImpl.saveEntity SUCCEED!");

                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateMedicalTaskList START!");
                    creepersTaskListServiceImpl.updateMedicalTaskList(currentUrl, BaseConstant.TaskListFlag.COMPLETED.getValue());
                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateMedicalTaskList SUCCEED!");
                }
            } else {
                logger.info("step:  ======>>  entity is null, can't update task_list.flag to COMPLETED！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("step:  ======>>  TianYanChalPipline  save FALSE!!!");
            logger.error("write DB error", e.getCause().getClass() + e.getCause().getMessage());
            param.setErrorInfo("write DB error" + e.getCause().getClass() + e.getCause().getMessage());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
        }
        logger.info("step:  ======>>  exit TianYanChalPipline process!");
    }
}
