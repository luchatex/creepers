package com.fosun.fc.projects.creepers.pipeline.yellowPage;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersCompanyList;
import com.fosun.fc.projects.creepers.pipeline.BasePipeline;
import com.fosun.fc.projects.creepers.service.ICreepersShunQiService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 
 * <p>
 * 顺企网入库
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-10 13:34:16
 * @see
 */
@Component("shunQiPipline")
public class ShunQiPipline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Autowired
    private ICreepersShunQiService creepersShunQiServiceImpl;

    public ShunQiPipline() {
        setPath("/data/webmagic/");
    }

    public ShunQiPipline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("step:  ======>>  entry ShunQiPipline process!");
        // 数据入库
        Map<String, String> resultMap = resultItems.get(BaseConstant.RESULT_DATA_KEY);
        String currentUrl = resultItems.get(BaseConstant.CURRENT_URL);
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        if (param == null) {
            return;
        }
        param.setErrorPath(getClass().toString());
        try {
            if (resultMap != null && !resultMap.isEmpty()) {
                TCreepersCompanyList entity = new TCreepersCompanyList();
                CommonMethodUtils.mapTransEntity(resultMap, entity);
                CommonMethodUtils.setByDT(entity);
                logger.info("step:  ======>>  creepersShunQiServiceImpl.saveEntity START!");
                creepersShunQiServiceImpl.saveEntity(entity);
                logger.info("step:  ======>>  creepersShunQiServiceImpl.saveEntity SUCCEED!");

                if (StringUtils.isNotBlank(currentUrl)) {
                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateMedicalTaskList START!");
                    creepersTaskListServiceImpl.updateMedicalTaskList(currentUrl, BaseConstant.TaskListFlag.COMPLETED.getValue());
                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateMedicalTaskList SUCCEED!");
                } else {
                    logger.info("step:  ======>>  entity key is null, can't update task_list.flag to COMPLETED！！！");
                }
            } else {
                logger.info("step: ======>> input resultList is empty! Then update status to false!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("step:  ======>>  ShunQiPipline save FALSE!!!");
            logger.error("write DB error", e.getCause().getClass() + e.getCause().getMessage());
            param.setErrorInfo("write DB error" + e.getCause().getClass() + e.getCause().getMessage());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
        }
        logger.info("step:  ======>>  exit ShunQiPipline process!");
    }
}
