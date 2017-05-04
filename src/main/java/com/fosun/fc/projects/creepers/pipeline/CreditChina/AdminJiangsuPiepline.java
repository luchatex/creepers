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

import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;
import com.fosun.fc.projects.creepers.pipeline.BasePipeline;
import com.fosun.fc.projects.creepers.service.ICreepersAdminJiangsuService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.code.BaseCode;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 
 * <p>
 * 信用江苏行政处罚
 * </p>
 * 
 * @author Luxin
 * @since 2017-2-28 18:38:12
 * @see
 */
@Component
public class AdminJiangsuPiepline extends BasePipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String TYPE_AREA = "江苏";

    //公司名
    private final static String COMPANYNAME="companyName";

    @Autowired
    private ICreepersAdminJiangsuService creepersAdminJiangsuServiceImpl;

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    public AdminJiangsuPiepline() {
        setPath("/data/webmagic/");
    }

    public AdminJiangsuPiepline(String path) {
        setPath(path);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("step:  ======>>  entry AdminZhejiangPiepline process!");
        // 数据入库
        List<Map<String, String>> resultList = resultItems.get(BaseConstant.RESULT_DATA_KEY);
        String url = resultItems.get(BaseConstant.CURRENT_URL);
        CreepersParamDTO param = resultItems.get(BaseConstant.PARAM_DTO_KEY);
        param.setErrorPath(getClass().toString());
        try {
            if (!CommonMethodUtils.isEmpty(resultList)) {
                List<TCreepersAdminBeijing> entityList = new ArrayList<TCreepersAdminBeijing>();
                for (Map<String, String> resultMap : resultList) {
                	TCreepersAdminBeijing entity = new TCreepersAdminBeijing();
                    entity.setMemo(BaseCode.encryptMD5To16(resultMap.toString().getBytes()).toString());
                    entity.setKey(resultMap.get(COMPANYNAME));
                    entity.setContent(JSONObject.toJSONString(resultMap));
                    java.util.Date date = new java.util.Date();
                    entity.setUpdatedBy("admin");
                    entity.setCreatedBy("admin");
                    entity.setUpdatedDt(date);
                    entity.setCreatedDt(date);
                    entityList.add(entity);
                    entity.setType(TYPE_AREA);
                }
                logger.info("step:  ======>>  creepersAdminJiangsuServiceImpl.saveEntity START!");
                //保存
                creepersAdminJiangsuServiceImpl.saveOrUpdate(entityList);
                logger.info("step:  ======>>  creepersAdminJiangsuServiceImpl.saveEntity SUCCEED!");
                if (entityList.size() > 0 && entityList.get(0) != null && StringUtils.isNotBlank(entityList.get(0).getKey())) {
                    creepersTaskListServiceImpl.updateMedicalTaskList(url, "1");
                }
            } else {
                logger.info("step:  ======>>  input resultList is empty! Then update status to false!");
                param.setErrorInfo("input resultList is empty! Then update status to false!");
                logger.info("step:  ======>>  creepersExceptionHandleServiceImpl START!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("step:  ======>>  AdminJiangsuPipline  save FALSE!!!");
            logger.error("write DB error", e.getCause().getClass() + e.getCause().getMessage());
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("write DB error" + errorInfo);
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
        }
        logger.info("step:  ======>>  exit AdminJiangsuPipline process!");
    }
}
