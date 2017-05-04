/**
 * <p>
 * Copyright(c) @2016 Fortune Credit Management Co., Ltd.
 * </p>
 */
package com.fosun.fc.projects.creepers.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersMedicalDao;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersMedical;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalGMPProcessor;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalGSPProcessor;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalInstrumentDomesticProcessor;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalInstrumentForeignProcessor;
import com.fosun.fc.projects.creepers.pipeline.CFDA.MedicalPipline;
import com.fosun.fc.projects.creepers.service.ICreepersMedicalService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * <p>
 * description: 医药信息Service
 * </p>
 * 
 * @author MaXin
 * @since 2016-11-22 14:12:20
 * @see
 */
@Service("creepersMedicalServiceImpl")
public class CreepersMedicalServiceImpl extends CreepersBaseServiceImpl implements ICreepersMedicalService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CreepersMedicalDao creepersMedicalDao;

    @Autowired
    private MedicalInstrumentForeignProcessor medicalInstrumentForeignProcessor;

    @Autowired
    private MedicalInstrumentDomesticProcessor medicalInstrumentDomesticProcessor;

    @Autowired
    private MedicalGSPProcessor medicalGSPProcessor;

    @Autowired
    private MedicalGMPProcessor medicalGMPProcessor;

    @Autowired
    private MedicalPipline medicalPipline;
    
    @Override
    public void processByRequest(String taskType) {
        if (StringUtils.isBlank(taskType)) {
            return;
        }
        if (BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue().equals(taskType)) {
            this.processByRequestMedicalInstrumentDomestic();
        } else if (BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue().equals(taskType)) {
            this.processByRequestMedicalInstrumentForeign();
        } else if (BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue().equals(taskType)) {
            this.processByRequestMedicalGMP();
        } else if (BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue().equals(taskType)) {
            this.processByRequestMedicalGSP();
        }
    }

    private void processByRequestMedicalInstrumentDomestic() {
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalInstrumentDomestic start!");
        // 查询任务
        int threadNum = 6;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue());
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        // http://qy1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=26&bcId=118103058617027083838706701567
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalInstrumentDomesticProcessor.INDEX_URL);
        // 初始化Request

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(medicalInstrumentDomesticProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue());
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalInstrumentDomestic end!");

    }

    private void processByRequestMedicalInstrumentForeign() {
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalInstrumentForeign start!");
        // 查询任务
        int threadNum = 6;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        // http://qy1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=27&bcId=118103063506935484150101953610
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalInstrumentForeignProcessor.INDEX_URL);

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(medicalInstrumentForeignProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);

        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();

        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalInstrumentForeign end!");
    }

    private void processByRequestMedicalGMP() {
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalGMP start!");
        // 查询任务
        int threadNum = 6;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue());
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.DOWNLOAD_FILE_URL_1_REGEX, "http://www.sda.gov.cn/directory/web/WS01/images/(.*).xls");
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalGMPProcessor.INDEX_URL);

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(medicalGMPProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);

        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue());
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalGMP end!");
    }

    private void processByRequestMedicalGSP() {
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalGSP start!");
        // 查询任务
        int threadNum = 6;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalGSPProcessor.INDEX_URL);

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(medicalGSPProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersMedicalServiceImpl.processByRequest.processByRequestMedicalGSP end!");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void saveEntity(TCreepersMedical entity) {
        if (StringUtils.isBlank(entity.getKey())) {
            return;
        }
        TCreepersMedical oldEntity = creepersMedicalDao.findTop1ByKeyAndType(entity.getKey(), entity.getType());
        if (oldEntity != null) {
            entity.setId(oldEntity.getId());
            entity.setVersion(oldEntity.getVersion());
        }
        creepersMedicalDao.saveAndFlush(entity);
    }

    @Override
    public List<TCreepersMedical> findListByKey(String key) {
        List<TCreepersMedical> list = new ArrayList<TCreepersMedical>();
        if (StringUtils.isBlank(key)) {
            return list;
        }
        return creepersMedicalDao.findByKey(key);
    }
    
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void saveEntity(List<TCreepersMedical> entityList) {
        for (TCreepersMedical entity : entityList) {
            this.saveEntity(entity);
        }
    }

}
