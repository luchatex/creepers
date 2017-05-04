package com.fosun.fc.projects.creepers.pipeline.CreditChina;

import java.util.ArrayList;
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
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdmin;
import com.fosun.fc.projects.creepers.pipeline.BasePipeline;
import com.fosun.fc.projects.creepers.service.ICreepersAdminService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 
 * <p>
 * 医药类data统一入库
 * </p>
 * 
 * @author MaXin
 * @since 2016-11-22 13:52:12
 * @see
 */
@Component
public class AdminPiepline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICreepersAdminService creepersAdminServiceImpl;
    
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    public AdminPiepline() {
        setPath("/data/webmagic/");
    }

    public AdminPiepline(String path) {
        setPath(path);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("step:  ======>>  entry AdminPiepline process!");
        // 数据入库
        List<Map<String, String>> resultList = resultItems.get(BaseConstant.RESULT_DATA_KEY);
        String url = resultItems.get(BaseConstant.CURRENT_URL);
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        param.setErrorPath(getClass().toString());
        try {
            if (!CommonMethodUtils.isEmpty(resultList)) {
                List<TCreepersAdmin> entityList = new ArrayList<TCreepersAdmin>();
                for (Map<String, String> resultMap : resultList) {
                    TCreepersAdmin entity = new TCreepersAdmin();
                    CommonMethodUtils.mapTransEntity(resultMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    entityList.add(entity);
                }
                logger.info("step:  ======>>  creepersAdminServiceImpl.saveEntity START!");
                creepersAdminServiceImpl.saveEntity(entityList);
                logger.info("step:  ======>>  creepersAdminServiceImpl.saveEntity SUCCEED!");
                if (entityList.size() > 0 && entityList.get(0) != null && StringUtils.isNotBlank(entityList.get(0).getKey())) {
                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateFlagByTaskTypeAndParamMap START!");
                    creepersTaskListServiceImpl.updateMedicalTaskList(url, "1");
                    logger.info("step:  ======>>  creepersTaskListServiceImpl.updateFlagByTaskTypeAndParamMap SUCCEED!");
                }else {
                    logger.info("step:  ======>>  entity key is null, can't update task_list.flag to COMPLETED！！！");
                }
            } else {
                logger.info("step:  ======>>  input resultList is empty! Then update status to false!");
                param.setErrorInfo("input resultList is empty! Then update status to false!");
                logger.info("step:  ======>>  creepersExceptionHandleServiceImpl START!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("step:  ======>>  AdminPipline  save FALSE!!!");
            logger.error("write DB error", e.getCause().getClass() + e.getCause().getMessage());
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("write DB error" + errorInfo);
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
        }
        logger.info("step:  ======>>  exit AdminPipline process!");
    }
}
