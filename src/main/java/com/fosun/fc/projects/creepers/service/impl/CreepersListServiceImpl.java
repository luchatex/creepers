/**
 * <p>
 * Copyright(c) @2016 Fortune Credit Management Co., Ltd.
 * </p>
 */
package com.fosun.fc.projects.creepers.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.modules.mapper.BeanMapper;
import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.modules.utils.JsonResult.JSON_RESULT_TYPE;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.dao.CreepersDishonestBaiduDao;
import com.fosun.fc.projects.creepers.dao.CreepersListCourtDao;
import com.fosun.fc.projects.creepers.dao.CreepersListCreditDao;
import com.fosun.fc.projects.creepers.dao.CreepersListFundDao;
import com.fosun.fc.projects.creepers.dao.CreepersListInsuranceDao;
import com.fosun.fc.projects.creepers.dao.CreepersListJudgementDao;
import com.fosun.fc.projects.creepers.dao.CreepersListPatentDao;
import com.fosun.fc.projects.creepers.dao.CreepersListShixinDao;
import com.fosun.fc.projects.creepers.dao.CreepersListTourGuideDao;
import com.fosun.fc.projects.creepers.dao.CreepersListTradeMarkDao;
import com.fosun.fc.projects.creepers.dao.CreepersTaskListDao;
import com.fosun.fc.projects.creepers.dao.CreepersTourBlackListDao;
import com.fosun.fc.projects.creepers.downloader.HttpClientUtil;
import com.fosun.fc.projects.creepers.dto.CreepersGeneralDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListCourtDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListCreditDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListFundDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListInsuranceDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListJudgementDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListPatentDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListShixinDTO;
import com.fosun.fc.projects.creepers.dto.CreepersListTourGuideDTO;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.dto.CreepersTaskListDTO;
import com.fosun.fc.projects.creepers.dto.CreepersTourBlackListDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdmin;
import com.fosun.fc.projects.creepers.entity.TCreepersCourtAnnounce;
import com.fosun.fc.projects.creepers.entity.TCreepersCourtCorpBonds;
import com.fosun.fc.projects.creepers.entity.TCreepersCourtDisInfo;
import com.fosun.fc.projects.creepers.entity.TCreepersCourtDishonest;
import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;
import com.fosun.fc.projects.creepers.entity.TCreepersJudgement;
import com.fosun.fc.projects.creepers.entity.TCreepersListCourt;
import com.fosun.fc.projects.creepers.entity.TCreepersListCredit;
import com.fosun.fc.projects.creepers.entity.TCreepersListFund;
import com.fosun.fc.projects.creepers.entity.TCreepersListInsurance;
import com.fosun.fc.projects.creepers.entity.TCreepersListJudgement;
import com.fosun.fc.projects.creepers.entity.TCreepersListPatent;
import com.fosun.fc.projects.creepers.entity.TCreepersListShixin;
import com.fosun.fc.projects.creepers.entity.TCreepersListTourGuide;
import com.fosun.fc.projects.creepers.entity.TCreepersListTradeMark;
import com.fosun.fc.projects.creepers.entity.TCreepersMedical;
import com.fosun.fc.projects.creepers.entity.TCreepersPatent;
import com.fosun.fc.projects.creepers.entity.TCreepersSaction;
import com.fosun.fc.projects.creepers.entity.TCreepersShixin;
import com.fosun.fc.projects.creepers.entity.TCreepersShixinAll;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskList;
import com.fosun.fc.projects.creepers.entity.TCreepersTaxEvasion;
import com.fosun.fc.projects.creepers.entity.TCreepersTaxEvasionDetail;
import com.fosun.fc.projects.creepers.entity.TCreepersTourBlackList;
import com.fosun.fc.projects.creepers.entity.TCreepersTradeMark;
import com.fosun.fc.projects.creepers.monitor.EchoAnnotation;
import com.fosun.fc.projects.creepers.redis.service.IRedisPubService;
import com.fosun.fc.projects.creepers.redis.service.IRedisScheduleService;
import com.fosun.fc.projects.creepers.service.ICreepersAdminService;
import com.fosun.fc.projects.creepers.service.ICreepersCourtAnnounceService;
import com.fosun.fc.projects.creepers.service.ICreepersCourtCorpBondsService;
import com.fosun.fc.projects.creepers.service.ICreepersCourtDisInfoService;
import com.fosun.fc.projects.creepers.service.ICreepersCourtDishonestyService;
import com.fosun.fc.projects.creepers.service.ICreepersCreditReferenceService;
import com.fosun.fc.projects.creepers.service.ICreepersDishonestBaiduService;
import com.fosun.fc.projects.creepers.service.ICreepersExceptionHandleService;
import com.fosun.fc.projects.creepers.service.ICreepersFundService;
import com.fosun.fc.projects.creepers.service.ICreepersInsuranceService;
import com.fosun.fc.projects.creepers.service.ICreepersJudgementService;
import com.fosun.fc.projects.creepers.service.ICreepersListCreditService;
import com.fosun.fc.projects.creepers.service.ICreepersListService;
import com.fosun.fc.projects.creepers.service.ICreepersMedicalService;
import com.fosun.fc.projects.creepers.service.ICreepersPatentService;
import com.fosun.fc.projects.creepers.service.ICreepersSactionService;
import com.fosun.fc.projects.creepers.service.ICreepersShixinAllService;
import com.fosun.fc.projects.creepers.service.ICreepersShixinService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaxEvasionDetailService;
import com.fosun.fc.projects.creepers.service.ICreepersTaxEvasionService;
import com.fosun.fc.projects.creepers.service.ICreepersTouristBlackListService;
import com.fosun.fc.projects.creepers.service.ICreepersTouristGuideService;
import com.fosun.fc.projects.creepers.service.ICreepersTradeMarkService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JsonValidator;
import com.fosun.fc.projects.creepers.utils.TranslateMethodUtil;
import com.google.common.collect.Lists;

/**
 * <p>
 * description: 查询所有的任务序列表，并返回查询结果。
 * </p>
 * 
 * @author MaXin
 * @since 2016年8月8日16:43:50
 * @see
 */
@Component("creepersListServiceImpl")
@Transactional
public class CreepersListServiceImpl implements ICreepersListService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CreepersListCourtDao creepersListCourtDao;

    @Autowired
    private CreepersListCreditDao creepersListCreditDao;

    @Autowired
    private ICreepersListCreditService creepersListCreditServiceImpl;

    @Autowired
    private CreepersListJudgementDao creepersListJudgementDao;

    @Autowired
    private CreepersListPatentDao creepersListPatentDao;

    @Autowired
    private CreepersListTradeMarkDao creepersListTradeMarkDao;

    @Autowired
    private CreepersListShixinDao creepersListShixinDao;

    @Autowired
    private CreepersListInsuranceDao creepersListInsuranceDao;

    @Autowired
    private CreepersListFundDao creepersListFundDao;

    @Autowired
    private CreepersListTourGuideDao creepersListTourGuideDao;

    @Autowired
    private CreepersTourBlackListDao creepersTourBlackListDao;

    @Autowired
    private CreepersTaskListDao creepersTaskListDao;

    @Autowired
    private CreepersDishonestBaiduDao creepersDishonestBaiduDao;

    @Autowired
    private IRedisPubService redisPubService;

    @Autowired
    private IRedisScheduleService redisScheduleServiceImpl;

    @Autowired
    private ICreepersJudgementService creepersJudgementServiceImpl;

    @Autowired
    private ICreepersCourtAnnounceService creepersCourtAnnounceServiceImpl;

    @Autowired
    private ICreepersPatentService creepersPatentServiceImpl;

    @Autowired
    private ICreepersTradeMarkService creepersTradeMarkServiceImpl;

    @Autowired
    private ICreepersShixinService creepersShixinServiceImpl;

    @Autowired
    private ICreepersFundService creepersFundServiceImpl;

    @Autowired
    private ICreepersInsuranceService creepersInsuranceServiceImpl;

    @Autowired
    private ICreepersCreditReferenceService creepersCreditReferenceServiceImpl;

    @Autowired
    private ICreepersCourtCorpBondsService creepersCourtCorpBondsServiceImpl;

    @Autowired
    private ICreepersCourtDishonestyService creepersCourtDishonestyServiceImpl;

    @Autowired
    private ICreepersCourtDisInfoService creepersCourtDisInfoServiceImpl;

    @Autowired
    private ICreepersSactionService creepersSactionServiceImpl;

    @Autowired
    private ICreepersShixinAllService creepersShixinAllServiceImpl;

    @Autowired
    private ICreepersTaxEvasionService creepersTaxEvasionServiceImpl;

    @Autowired
    private ICreepersTaxEvasionDetailService creepersTaxEvasionDetailServiceImpl;

    @Autowired
    private ICreepersTouristGuideService creepersTouristGuideServiceImpl;

    @Autowired
    private ICreepersTouristBlackListService creepersTouristBlackListServiceImpl;

    @Autowired
    private ICreepersMedicalService creepersMedicalServiceImpl;

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Autowired
    private ICreepersAdminService creepersAdminServiceImpl;

    @Autowired
    private ICreepersDishonestBaiduService creepersDishonestBaiduServiceImpl;

    @Autowired
    private ICreepersExceptionHandleService creepersExceptionHandleServiceImpl;

    /**
     * <p>
     * description: restful接口：查询请求。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JsonResult processByMerName(String requestType, String merName) {
        JsonResult jsonResult = null;
        Map map = new HashMap();
        logger.info("Entry CreepersListServiceImpl.processByMerName!");
        logger.info("======>>>  requestType:{}", requestType);
        logger.info("======>>>  merName:{}", merName);
        String message = "";
        // 判断该次查询，是否会触发爬取操作。消息体格式：请求类型|企业名称
        if (processFlag(requestType, merName)) {
            // 数据库中存在记录，则直接查询DB并返回结果
            Map resultList = this.queryInfoByMerName(requestType, merName);
            // 封装返回消息
            map.put("result", resultList);
            if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TOUR_BLACK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TOUR_AGENCY_BLACK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.COURT_CORP_BONDS_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.COURT_DISHONESTY_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SACTION_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SHI_XIN_ALL_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TAX_EVASION_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.ADMIN_SACTION_LIST.getValue().equals(requestType)) {
                if (CommonMethodUtils.isEmpty(resultList)) {
                    message = merName + "："
                            + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                            + "不存在此关键字！";
                } else {
                    message = "获取" + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                            + "信息成功！";
                }
            } else {
                message = "爬取信息类型:[" + requestType + "]异常，请联系爬虫组同事进行维护。";
            }
            jsonResult = new JsonResult(JSON_RESULT_TYPE.success, message, map);
        } else {
            // 不存在，启动爬虫爬取数据
            redisPubService.sendMsg(requestType, merName);
            // 封装返回消息
            if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.BUSI_INFO_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)) {
                message = BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                        + "已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
            } else {
                message = "爬取信息类型:[" + requestType + "]异常，请联系爬虫组同事进行维护。";
            }
            jsonResult = new JsonResult(JSON_RESULT_TYPE.failure, message, map);
        }
        return jsonResult;
    }

    /**
     * 
     * <p>
     * description: restful接口：查询请求-param
     * </p>
     * 
     * @param params
     * @author MaXin
     * @throws Exception 
     * @see 2016-8-11 17:53:48
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public JsonResult<Map<String, Object>> processByParam(CreepersLoginParamDTO param) throws Exception {
        JsonResult<Map<String, Object>> jsonResult = null;
        Map<String, Object> map = new HashMap<String, Object>();
        String requestType = param.getTaskType();
        logger.info("Entry CreepersListServiceImpl.processByParam!");
        // 消息体格式：请求类型|企业名称
        if (processFlag(param)) {
            // 数据库中存在记录直接查询DB并返回结果
            map.put("result", this.queryInfoByParam(param));
            String message = "";
            if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
                message = "获取" + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese() + "成功！";
            } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)) {
                message = "获取人行个人征信报告成功！";
                // ========================================================
                // 因个别原因导致接口字段外置，后期接口统一规范后，会将外放内容去掉！！！
                // 这个是不能容忍的扯淡需求！！！！！！！！
                // ========================================================
                // 注意！！！！！这块内容删除前，这个注释不能删！！！ by MaXin 2016-11-2 16:38:10
                // ========================================================
                Map<String, Object> result = (Map<String, Object>) map.get("result");
                CreepersGeneralDTO generalDto = (CreepersGeneralDTO) result
                        .get(CreepersConstant.TableNamesCreditReference.T_CREEPERS_GENERAL.getMapKey());
                result.putAll(BeanMapper.toMap(generalDto));
                CreepersListCreditDTO taskListDto = (CreepersListCreditDTO) result
                        .get(CreepersConstant.TableNamesListName.T_CREEPERS_LIST_CREDIT.getMapKey());
                result.put(CreepersConstant.TCreepersListCreditColumn.IMAGE_URL.getValue(), taskListDto.getImageUrl());
                // ========================================================
            } else {
                message = "爬取信息类型:[" + requestType + "]异常，请联系爬虫组同事进行维护。";
            }
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
        } else {
            String message = "";
            String mqMessage = "";
            if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
                message = BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                        + "已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
            } else {
                message = "爬取信息类型:[" + requestType + "]异常，请联系爬虫组同事进行维护。";
            }
            mqMessage = JSONObject.toJSONString(param, BaseConstant.features);
            // 数据库中没记录，调用消息层发起请求
            redisPubService.sendMsg(requestType, mqMessage);
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
        }
        return jsonResult;

    }

    /**
     * <p>
     * description: 判断该次查询，是否会触发爬取操作。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings("rawtypes")
    private Boolean processFlag(String requestType, String uniqueKey) {
        Boolean flag = false;
        List taskList = queryListByKey(requestType, uniqueKey);
        if (!CommonMethodUtils.isEmpty(taskList)) {
            String result = "";
            if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)) {
                TCreepersListCourt entity = (TCreepersListCourt) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)) {
                TCreepersListJudgement entity = (TCreepersListJudgement) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)) {
                TCreepersListPatent entity = (TCreepersListPatent) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)) {
                TCreepersListTradeMark entity = (TCreepersListTradeMark) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)) {
                TCreepersListShixin entity = (TCreepersListShixin) taskList.get(0);
                result = entity.getFlag();
            }
            if (BaseConstant.TaskListStatus.SUCCEED.getValue().equals(result)) {
                flag = true;
            }
        } else {
            // 全量类型不查询List表，默认直接返回成功。
            if (BaseConstant.TaskListType.TOUR_BLACK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TOUR_AGENCY_BLACK_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.COURT_CORP_BONDS_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.COURT_DISHONESTY_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SACTION_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.SHI_XIN_ALL_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.TAX_EVASION_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.ADMIN_SACTION_LIST.getValue().equals(requestType)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * <p>
     * description: 判断该次查询，是否会触发爬取操作。
     * </p>
     * 
     * @param param
     */
    @SuppressWarnings("rawtypes")
    private Boolean processFlag(CreepersLoginParamDTO param) {
        String requestType = param.getTaskType();
        Boolean flag = false;
        List taskList = queryListByKey(param);
        if (!CommonMethodUtils.isEmpty(taskList)) {
            String result = "";
            if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)) {
                TCreepersListInsurance entity = (TCreepersListInsurance) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)) {
                TCreepersListFund entity = (TCreepersListFund) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)) {
                TCreepersListCredit entity = (TCreepersListCredit) taskList.get(0);
                result = entity.getFlag();
            } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
                TCreepersListTourGuide entity = (TCreepersListTourGuide) taskList.get(0);
                result = entity.getFlag();
            }
            if (BaseConstant.TaskListStatus.SUCCEED.getValue().equals(result)) {
                flag = true;
            }
        } else {
            // 对于全量的业务，不存在CreepersList表，所以都走这里，通过job启动任务。
            // 此处逻辑不要轻易修改，后果很严重！！！
            // flag = true;
        }
        return flag;
    }

    /**
     * <p>
     * description: 普通类-->查询爬取任务列表请求-uniqueKey(name)。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param uniqueKey
     *            企业名称
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List queryListByKey(String requestType, String uniqueKey) {
        logger.info("Entry CreepersListServiceImpl.queryListByKey!");
        logger.info("requestType:{}", requestType);
        logger.info("key's value:{}", uniqueKey);
        List resultList = new ArrayList<>();
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)) {
            return creepersListCourtDao.queryListByMerName(uniqueKey);
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)) {
            return creepersListJudgementDao.queryListByMerName(uniqueKey);
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)) {
            return creepersListPatentDao.queryListByMerName(uniqueKey);
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)) {
            return creepersListTradeMarkDao.queryListByMerName(uniqueKey);
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)) {
            return creepersListShixinDao.queryListByMerName(uniqueKey);
        } else {
            return resultList;
        }
    }

    /**
     * <p>
     * description: 登录类-->查询爬去任务列表请求。
     * </p>
     * 
     * @param param
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List queryListByKey(CreepersLoginParamDTO param) {
        logger.info("Entry CreepersListServiceImpl.queryListByKey!");
        String requestType = param.getTaskType();
        String uniqueKey = param.getLoginName();
        logger.info("requestType:{}", requestType);
        logger.info("key's value:{}", uniqueKey);
        List resultList = new ArrayList<>();
        if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)) {
            return creepersListFundDao.findByUserCode(param.getLoginName());
        } else if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)) {
            return creepersListInsuranceDao.queryListByUserCode(param.getLoginName());
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)) {
            return creepersListCreditDao.findByUserCode(uniqueKey);
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
            if (StringUtils
                    .isNotBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                return creepersListTourGuideDao.findByGuideNoAndCardNoAndCertNo(
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()));

            } else if (StringUtils
                    .isNotBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()))
                    && StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                return creepersListTourGuideDao.findByGuideNoAndCardNo(
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()));

            } else if ((StringUtils
                    .isBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    || StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue())))
                    && StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                return creepersListTourGuideDao.findByGuideNoOrCertNo(
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()));

            } else if ((StringUtils
                    .isBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    || StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue())))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                return creepersListTourGuideDao.findByGuideNoAndCertNoOrCardNoAndCertNo(
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()),
                        param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()));

            } else {
                return resultList;
            }
        } else {
            return resultList;
        }
    }

    /**
     * <p>
     * description: 普通类-->查询详细信息请求。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map queryInfoByMerName(String requestType, String merName) {
        Map resultMap = new HashMap<>();
        try {
            if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)) {
                List<TCreepersCourtAnnounce> list = creepersCourtAnnounceServiceImpl.findByMerName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_COURT_ANNOUNCE.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)) {
                List<TCreepersJudgement> list = creepersJudgementServiceImpl.findByMerName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_JUDGEMENT.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)) {
                List<TCreepersPatent> list = creepersPatentServiceImpl.findByMerName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_PATENT.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)) {
                List<TCreepersTradeMark> list = creepersTradeMarkServiceImpl.findByMerName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_TRADE_MARK.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)) {
                List<TCreepersShixin> list = creepersShixinServiceImpl.findListByMerName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_SHIXIN.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.TOUR_BLACK_LIST.getValue().equals(requestType)) {
                List<TCreepersTourBlackList> list = creepersTouristBlackListServiceImpl.findListByGuideNo(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_TOUR_BLACK_LIST.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.TOUR_AGENCY_BLACK_LIST.getValue().equals(requestType)) {
                List<TCreepersTourBlackList> list = creepersTouristBlackListServiceImpl
                        .findListByAgentNameAndType(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_TOUR_BLACK_LIST.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.COURT_CORP_BONDS_LIST.getValue().equals(requestType)) {
                List<TCreepersCourtCorpBonds> list = creepersCourtCorpBondsServiceImpl.findListByName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_COURT_CORP_BONDS.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.COURT_DISHONESTY_LIST.getValue().equals(requestType)) {
                List<TCreepersCourtDishonest> list = creepersCourtDishonestyServiceImpl.findListByName(merName);
                List<TCreepersCourtDisInfo> list2 = creepersCourtDisInfoServiceImpl.findListByName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_COURT_DISHONEST.getMapKey(),
                            CommonMethodUtils.entityList(list));
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_COURT_DIS_INFO.getMapKey(),
                            CommonMethodUtils.entityList(list2));
                }
            } else if (BaseConstant.TaskListType.SACTION_LIST.getValue().equals(requestType)) {
                List<TCreepersSaction> list = creepersSactionServiceImpl.findListByName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_SACTION.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.ADMIN_SACTION_LIST.getValue().equals(requestType)) {
                List<TCreepersAdmin> list = creepersAdminServiceImpl.findByTypeAndKey(requestType, merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    List<String> contentList = new ArrayList<String>();
                    for (TCreepersAdmin entity : list) {
                        contentList.add(entity.getContent());
                    }
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_ADMIN.getMapKey(), contentList);
                }
            } else if (BaseConstant.TaskListType.SHI_XIN_ALL_LIST.getValue().equals(requestType)) {
                List<TCreepersShixinAll> list = creepersShixinAllServiceImpl.findListByName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_SHIXIN_ALL.getMapKey(),
                            CommonMethodUtils.entityList(list));
                }
            } else if (BaseConstant.TaskListType.TAX_EVASION_LIST.getValue().equals(requestType)) {
                List<TCreepersTaxEvasion> list = creepersTaxEvasionServiceImpl.findListByName(merName);
                List<TCreepersTaxEvasionDetail> list2 = creepersTaxEvasionDetailServiceImpl.findListByName(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_TAX_EVASION.getMapKey(),
                            CommonMethodUtils.entityList(list));
                    resultMap.put(CreepersConstant.TableNamesCreditChina.T_CREEPERS_TAX_EVASION_DETAIL.getMapKey(),
                            CommonMethodUtils.entityList(list2));
                }
            } else if (BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue().equals(requestType)
                    || BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue().equals(requestType)) {
                List<TCreepersMedical> list = creepersMedicalServiceImpl.findListByKey(merName);
                if (!CommonMethodUtils.isEmpty(list)) {
                    resultMap.put(requestType, TranslateMethodUtil.jsonArrayKeyC2P(list.get(0).getContent()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("step:  ======>>  CreepersListServiceImpl queryInfoByMerName FALSE!!!");
            logger.error("Search Data Or BeanList to Map Exception!\n" + e.getMessage());
            CreepersParamDTO param = new CreepersParamDTO();
            param.setTaskType(requestType);
            param.putSearchKeyWord(merName);
            param.setErrorPath(getClass().toString());
            param.setErrorInfo("Search Data Or BeanList to Map Exception!\n" + e.getMessage());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
        }
        return resultMap;
    }

    /**
     * <p>
     * description: 登录类-->查询详细信息请求。
     * </p>
     * 
     * @param param
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes" })
    private Map queryInfoByParam(CreepersLoginParamDTO param) throws Exception {
        String requestType = param.getTaskType();
        String userCode = param.getLoginName();
        Map map = new HashMap();
        if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)) {
            map = creepersInsuranceServiceImpl.findListByCertNoForMap(userCode);
        } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)) {
            map = creepersFundServiceImpl.findByLoginNameForMapForRest(userCode);
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)) {
            map = creepersCreditReferenceServiceImpl.findByLoginNameForMap(param);
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
            map = creepersTouristGuideServiceImpl.findByParamForMap(param);
        }
        return map;
    }

    /**
     * <p>
     * description: Controller:分页查询请求。
     * </p>
     * 
     * @param searchParams,pageNumber,pageSize,sortType,taskType
     * @author LiZhanPing
     * @see
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Page<?> findList(Map<String, Object> searchParams, int pageNumber, int pageSize, String sortType,
            String taskType) throws Exception {
        PageRequest pageable = buildPageRequest(pageNumber, pageSize, sortType);
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListCourt> spec = (Specification<TCreepersListCourt>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListCourt> entityPage = creepersListCourtDao.findAll(spec, pageable);
            List<TCreepersListCourt> list = entityPage.getContent();
            List<CreepersListCourtDTO> dtoList = new ArrayList<CreepersListCourtDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListCourtDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListCourtDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListJudgement> spec = (Specification<TCreepersListJudgement>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListJudgement> entityPage = creepersListJudgementDao.findAll(spec, pageable);
            List<TCreepersListJudgement> list = entityPage.getContent();
            List<CreepersListJudgementDTO> dtoList = new ArrayList<CreepersListJudgementDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListJudgementDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListJudgementDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListPatent> spec = (Specification<TCreepersListPatent>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListPatent> entityPage = creepersListPatentDao.findAll(spec, pageable);
            List<TCreepersListPatent> list = entityPage.getContent();
            List<CreepersListPatentDTO> dtoList = new ArrayList<CreepersListPatentDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListPatentDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListPatentDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListTradeMark> spec = (Specification<TCreepersListTradeMark>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListTradeMark> entityPage = creepersListTradeMarkDao.findAll(spec, pageable);
            List<TCreepersListTradeMark> list = entityPage.getContent();
            List<CreepersListPatentDTO> dtoList = new ArrayList<CreepersListPatentDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListPatentDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListPatentDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListShixin> spec = (Specification<TCreepersListShixin>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListShixin> entityPage = creepersListShixinDao.findAll(spec, pageable);
            List<TCreepersListShixin> list = entityPage.getContent();
            List<CreepersListShixinDTO> dtoList = new ArrayList<CreepersListShixinDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListShixinDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListShixinDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListInsurance> spec = (Specification<TCreepersListInsurance>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListInsurance> entityPage = creepersListInsuranceDao.findAll(spec, pageable);
            List<TCreepersListInsurance> list = entityPage.getContent();
            List<CreepersListInsuranceDTO> dtoList = new ArrayList<CreepersListInsuranceDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListInsuranceDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListInsuranceDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListFund> spec = (Specification<TCreepersListFund>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListFund> entityPage = creepersListFundDao.findAll(spec, pageable);
            List<TCreepersListFund> list = entityPage.getContent();
            List<CreepersListFundDTO> dtoList = new ArrayList<CreepersListFundDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListFundDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListFundDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListCredit> spec = (Specification<TCreepersListCredit>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListCredit> entityPage = creepersListCreditDao.findAll(spec, pageable);
            List<TCreepersListCredit> list = entityPage.getContent();
            List<CreepersListCreditDTO> dtoList = new ArrayList<CreepersListCreditDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListCreditDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListCreditDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersListTourGuide> spec = (Specification<TCreepersListTourGuide>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersListTourGuide> entityPage = creepersListTourGuideDao.findAll(spec, pageable);
            List<TCreepersListTourGuide> list = entityPage.getContent();
            List<CreepersListTourGuideDTO> dtoList = new ArrayList<CreepersListTourGuideDTO>();
            dtoList = BeanMapper.mapList(list, CreepersListTourGuideDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersListTourGuideDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else if (BaseConstant.TaskListType.TOUR_BLACK_LIST.getValue().equalsIgnoreCase(taskType)) {
            Specification<TCreepersTourBlackList> spec = (Specification<TCreepersTourBlackList>) CommonMethodUtils
                    .buildSpecification(searchParams, "Dt");
            Page<TCreepersTourBlackList> entityPage = creepersTourBlackListDao.findAll(spec, pageable);
            List<TCreepersTourBlackList> list = entityPage.getContent();
            List<CreepersTourBlackListDTO> dtoList = new ArrayList<CreepersTourBlackListDTO>();
            dtoList = BeanMapper.mapList(list, CreepersTourBlackListDTO.class);
            Page<?> dtoPage = new PageImpl<CreepersTourBlackListDTO>(dtoList, pageable, entityPage.getTotalElements());
            return dtoPage;
        } else {
            List dtoList = new ArrayList();
            Page<?> dtoPage = new PageImpl<>(dtoList, pageable, 0L);
            return dtoPage;
        }
    }

    /**
     * <p>
     * description: Controller:普通类-->添加爬取任务请求。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings({ "rawtypes" })
    public JsonResult addTaskByMerName(String requestType, String merName) {
        logger.info("Entry CreepersListServiceImpl.addTaskByMerName!");
        logger.info("======>>>  requestType:{}", requestType);
        logger.info("======>>>  merName:{}", merName);
        // 消息体格式：请求类型|企业名称
        // 调用消息层发起请求
        redisPubService.sendMsg(requestType, merName);
        String message = "";
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(requestType)) {
            message = "法院公告信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(requestType)) {
            message = "法院判决书信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(requestType)) {
            message = "发明专利信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(requestType)) {
            message = "商标信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.BUSI_INFO_LIST.getValue().equals(requestType)) {
            message = "工商注册信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(requestType)) {
            message = "失信人信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else {
            message = "爬取信息类型:[" + requestType + "]不存在，请联系爬虫组同事进行维护。";
        }
        return new JsonResult(JSON_RESULT_TYPE.failure, message);
    }

    /**
     * <p>
     * description: Controller:登录类-->添加爬取任务请求。
     * </p>
     * 
     * @param param
     */
    @SuppressWarnings("rawtypes")
    @Override
    public JsonResult addTaskByParam(CreepersLoginParamDTO param) {
        logger.info("Entry CreepersListServiceImpl.addTaskByParam!");
        logger.info("======>>>  param:{}", param);
        // 消息体格式：请求类型|企业名称
        // 数据库中没记录，调用消息层发起请求
        String redisMessage = JSONObject.toJSONString(param, BaseConstant.features);
        redisPubService.sendMsg(param.getTaskType(), redisMessage);
        String message = "";
        if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(param.getTaskType())) {
            message = "社保信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(param.getTaskType())) {
            message = "公积金信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(param.getTaskType())) {
            message = "人行个人征信报告信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(param.getTaskType())) {
            message = "导游证信息已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
        } else {
            message = "爬取信息类型:[" + param.getTaskType() + "]不存在，请联系爬虫组同事进行维护。";
        }
        return new JsonResult(JSON_RESULT_TYPE.failure, message);
    }

    /**
     * <p>
     * description: Controller:全量类-->添加爬取任务请求。
     * </p>
     * 
     * @param requestType
     *            队列类型
     */
    @Override
    public void addTaskByRequestType(String requestType, String wsId) {

        List<TCreepersTaskList> entityList = creepersTaskListServiceImpl.findByTaskTypeAndFlag(requestType,
                BaseConstant.TaskListFlag.FAIL.getValue());
        List<CreepersTaskListDTO> dtoList = new ArrayList<CreepersTaskListDTO>();
        dtoList = BeanMapper.mapList(entityList, CreepersTaskListDTO.class);
        Long total = creepersTaskListServiceImpl.countByTaskTypeAndFlag(requestType,
                BaseConstant.TaskListFlag.FAIL.getValue());
        Long stored = 0l;
        for (CreepersTaskListDTO dto : dtoList) {
            redisScheduleServiceImpl.pushTask(dto);
            stored++;
            EchoAnnotation.echo(wsId, stored + "|" + total);
        }
    }

    /**
     * <p>
     * description: Controller:普通类-->重爬请求。
     * </p>
     * 
     * @param requestType
     *            队列类型
     * @param merName
     *            企业名称
     */
    @Override
    public void doRecycleByMerName(String requestType, String merName) {

        logger.info("Entry CreepersListServiceImpl.doRecycleByMerName!");
        logger.info("======>>>  requestType:{}", requestType);
        logger.info("======>>>  merName:{}", merName);
        // 消息体格式：请求类型|企业名称
        // 数据库中没记录，调用消息层发起请求
        redisPubService.sendMsg(requestType, merName);
    }

    /**
     * <p>
     * description: Controller：登录类-->重爬请求。
     * </p>
     * 
     * @param param
     */
    @Override
    public void doRecycleByParam(CreepersLoginParamDTO param) {
        String requestType = param.getTaskType();
        logger.info("Entry CreepersListServiceImpl.doRecycleByUserCode!");
        logger.info("======>>>  requestType:{}", requestType);
        logger.info("======>>>  UserCode:{}", param.getLoginName());
        // 消息体格式：请求类型|企业名称
        // 数据库中没记录，调用消息层发起请求
        String message = "";
        if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(requestType)) {
            TCreepersListInsurance entity = creepersListInsuranceDao.findTop1ByUserCode(param.getLoginName());
            param.setLoginName(entity.getUserCode());
            param.setPassword(entity.getPassword());
        } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(requestType)) {
            TCreepersListFund entity = creepersListFundDao.findTop1ByUserCode(param.getLoginName());// creepersListFundDao.queryListByUserCode(userCode);
            param.setLoginName(entity.getUserCode());
            param.setPassword(entity.getPassword());
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(requestType)) {
            TCreepersListCredit entity = creepersListCreditDao.findTop1ByUserCode(param.getLoginName());
            param.setLoginName(entity.getUserCode());
            param.setPassword(entity.getPassword());
            param.setMessageCaptchaValue(entity.getMessageCode());
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(requestType)) {
            logger.info("======>>>  guideNo:{}",
                    param.getSearchKeyWord(CreepersConstant.TCreepersListTourGuideColumn.GUIDE_NO.getValue()));
            logger.info("======>>>  cardNo:{}",
                    param.getSearchKeyWord(CreepersConstant.TCreepersListTourGuideColumn.CARD_NO.getValue()));
            logger.info("======>>>  certNo:{}",
                    param.getSearchKeyWord(CreepersConstant.TCreepersListTourGuideColumn.CERT_NO.getValue()));
            TCreepersListTourGuide entity = new TCreepersListTourGuide();
            if (StringUtils
                    .isNotBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                entity = creepersListTourGuideDao
                        .findByGuideNoAndCardNoAndCertNo(
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))
                        .get(0);

            } else if (StringUtils
                    .isNotBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()))
                    && StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                entity = creepersListTourGuideDao
                        .findByGuideNoAndCardNo(
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()))
                        .get(0);

            } else if ((StringUtils
                    .isBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    || StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue())))
                    && StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                entity = creepersListTourGuideDao
                        .findByGuideNoOrCertNo(
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))
                        .get(0);

            } else if ((StringUtils
                    .isBlank(param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()))
                    || StringUtils.isBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue())))
                    && StringUtils.isNotBlank(
                            param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))) {
                entity = creepersListTourGuideDao
                        .findByGuideNoAndCertNoOrCardNoAndCertNo(
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue()),
                                param.getSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CERT_NO.getValue()))
                        .get(0);

            }
            param.putSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.GUIDE_NO.getValue(), entity.getGuideNo());
            param.putSearchKeyWord(CreepersConstant.TCreepersTourGuideColumn.CARD_NO.getValue(), entity.getCardNo());
        }
        param.setTaskType(requestType);
        message = JSONObject.toJSONString(param, BaseConstant.features);
        redisPubService.sendMsg(requestType, message);
    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: 普通类-->添加爬虫任务请求。
     * </p>
     * 
     * @param param
     */
    @Override
    public void insertList(CreepersParamDTO param) {
        String taskType = param.getTaskType();
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(taskType)) {
            TCreepersListCourt entity = new TCreepersListCourt();
            entity.setMerName(param.getSearchKeyWordForString());
            entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
            CommonMethodUtils.setByDT(entity);
            creepersListCourtDao.save(entity);
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(taskType)) {
            TCreepersListJudgement entity = new TCreepersListJudgement();
            entity.setMerName(param.getSearchKeyWordForString());
            entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
            CommonMethodUtils.setByDT(entity);
            creepersListJudgementDao.save(entity);
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(taskType)) {
            TCreepersListPatent entity = new TCreepersListPatent();
            entity.setMerName(param.getSearchKeyWordForString());
            entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
            CommonMethodUtils.setByDT(entity);
            creepersListPatentDao.save(entity);
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(taskType)) {
            TCreepersListTradeMark entity = new TCreepersListTradeMark();
            entity.setMerName(param.getSearchKeyWordForString());
            entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
            CommonMethodUtils.setByDT(entity);
            creepersListTradeMarkDao.save(entity);
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(taskType)) {
            TCreepersListShixin entity = new TCreepersListShixin();
            entity.setMerName(param.getSearchKeyWordForString());
            entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
            CommonMethodUtils.setByDT(entity);
            creepersListShixinDao.save(entity);
        }

    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: 登录类-->添加爬虫任务请求-param。
     * </p>
     * 
     * @param param
     */
    @Override
    public void insertList(CreepersLoginParamDTO param) {
        // TODO Auto-generated method stub

    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: 登录类-->删除爬虫任务请求-param。
     * </p>
     * 
     * @param param
     */
    @Override
    public void deleteList(CreepersParamDTO param) {
        String taskType = param.getTaskType();
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(taskType)) {
            creepersListCourtDao.updateListByMerName(param.getSearchKeyWordForString(),
                    BaseConstant.TaskListStatus.DELETE.getValue());
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(taskType)) {
            creepersListJudgementDao.updateListByMerName(param.getSearchKeyWordForString(),
                    BaseConstant.TaskListStatus.DELETE.getValue());
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(taskType)) {
            creepersListPatentDao.updateListByMerName(param.getSearchKeyWordForString(),
                    BaseConstant.TaskListStatus.DELETE.getValue());
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(taskType)) {
            creepersListTradeMarkDao.updateListByMerName(param.getSearchKeyWordForString(),
                    BaseConstant.TaskListStatus.DELETE.getValue());
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(taskType)) {
            creepersListShixinDao.updateListByMerName(param.getSearchKeyWordForString(),
                    BaseConstant.TaskListStatus.DELETE.getValue());
        }
    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: controller：登录类-->删除爬虫任务请求。
     * </p>
     * 
     * @param param
     */
    @Override
    public void deleteList(CreepersLoginParamDTO param) {
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * description: 普通类-->修改任务List状态为失败。
     * </p>
     * 
     * @param param
     * @author Administrator
     * @see
     */
    @Override
    public void updateList(CreepersParamDTO param) {
        String taskType = param.getTaskType();
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(taskType)) {
            creepersListCourtDao.updateListByMerName(param.getSearchKeyWordForString(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(taskType)) {
            creepersListJudgementDao.updateListByMerName(param.getSearchKeyWordForString(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(taskType)) {
            creepersListPatentDao.updateListByMerName(param.getSearchKeyWordForString(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(taskType)) {
            creepersListTradeMarkDao.updateListByMerName(param.getSearchKeyWordForString(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(taskType)) {
            creepersListShixinDao.updateListByMerName(param.getSearchKeyWordForString(), param.getTaskStatus());
        }
    }

    /**
     * 
     * <p>
     * description: 登录类-->修改任务List状态为失败。
     * </p>
     * 
     * @param param
     * @author Administrator
     * @see
     */
    @Override
    public void updateList(CreepersLoginParamDTO param) {
        String taskType = param.getTaskType();
        if (BaseConstant.TaskListType.INSURANCE_LIST.getValue().equals(taskType)) {
            creepersListInsuranceDao.updateListByUserCode(param.getLoginName(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.FUND_LIST.getValue().equals(taskType)) {
            creepersListFundDao.updateListByUserCode(param.getLoginName(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.CREDIT_REFERENCE_LIST.getValue().equals(taskType)) {
            creepersListCreditServiceImpl.updateTaskListStatus(param.getLoginName(), param.getTaskStatus());
        } else if (BaseConstant.TaskListType.TOUR_GUIDE_LIST.getValue().equals(taskType)) {
            TCreepersListTourGuide entity = creepersListTourGuideDao.findTop1ByGuideNoOrCardNo(
                    param.getSearchKeyWord(CreepersConstant.TCreepersListTourGuideColumn.GUIDE_NO.getValue()),
                    param.getSearchKeyWord(CreepersConstant.TCreepersListTourGuideColumn.CARD_NO.getValue()));
            entity.setFlag(param.getTaskStatus());
            CommonMethodUtils.setByDT(entity);
            creepersListTourGuideDao.saveAndFlush(entity);
        }
    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: 查询请求。
     * </p>
     * 
     * @param param
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List selectList(CreepersParamDTO param) {
        String taskType = param.getTaskType();
        if (BaseConstant.TaskListType.COURT_LIST.getValue().equals(taskType)) {
            return creepersListCourtDao.queryListByMerName(param.getSearchKeyWordForString());
        } else if (BaseConstant.TaskListType.JUDGEMENT_LIST.getValue().equals(taskType)) {
            return creepersListJudgementDao.queryListByMerName(param.getSearchKeyWordForString());
        } else if (BaseConstant.TaskListType.PATENT_LIST.getValue().equals(taskType)) {
            return creepersListPatentDao.queryListByMerName(param.getSearchKeyWordForString());
        } else if (BaseConstant.TaskListType.TRADE_MARK_LIST.getValue().equals(taskType)) {
            return creepersListTradeMarkDao.queryListByMerName(param.getSearchKeyWordForString());
        } else if (BaseConstant.TaskListType.SHI_XIN_LIST.getValue().equals(taskType)) {
            return creepersListShixinDao.queryListByMerName(param.getSearchKeyWordForString());
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * 当前不使用，故不需要写
     * <p>
     * description: 查询请求。
     * </p>
     * 
     * @param param
     * @author MaXin
     * @since 2016-12-20 15:30:24
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List selectList(CreepersLoginParamDTO param) {
        return new ArrayList<>();
    }

    @Override
    public void addTaskByRedisPush(String taskListType, String processType) {
        if (StringUtils.isBlank(taskListType) || StringUtils.isBlank(processType)) {
            return;
        }
        List<TCreepersTaskList> taskList = new ArrayList<TCreepersTaskList>();
        if (BaseConstant.ProcessByRedisType.WHOLE_DATA.getValue().equalsIgnoreCase(processType)) {
            int count = creepersTaskListDao.countByTaskTypeAndFlagIn(taskListType, Lists.newArrayList("0", "1", "2"));
            int step = 100000;
            for (int i = 0; i < (int) Math.ceil((double)count / step); i++) {
                taskList = creepersTaskListDao.findByTaskTypeAndFlagInAndRownum(taskListType,
                        Lists.newArrayList("0", "1", "2"), step);
                creepersTaskListServiceImpl.pushRequest(taskList, processType, step);
                taskList.clear();
            }
        } else if (BaseConstant.ProcessByRedisType.WHOLE_DATA_MAX_10W.getValue().equalsIgnoreCase(processType)) {
            taskList = creepersTaskListServiceImpl.findTop100000ByTaskTypeAndFlagNotOrderById(taskListType,
                    BaseConstant.TaskListFlag.PROCESSING.getValue());
        } else if (BaseConstant.ProcessByRedisType.BREAK_POINT_DATA.getValue().equalsIgnoreCase(processType)) {
            int count = creepersTaskListDao.countByTaskTypeAndFlagIn(taskListType, Lists.newArrayList("0", "2"));
            int step = 1000;
            for (int i = 0; i < (int) Math.ceil((double)count / step); i++) {
                taskList = creepersTaskListDao.findByTaskTypeAndFlagInAndRownum(taskListType,
                        Lists.newArrayList("0", "2"), step);
                creepersTaskListServiceImpl.pushRequest(taskList, processType, step);
                taskList.clear();
            }
        }
        redisPubService.sendMsg(taskListType, taskListType);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public JsonResult processByKeyWord(String requestType, String keyWord) {
        JsonResult jsonResult = null;
        // 在数据库中查询此关键字的信息
        Map resultList = this.queryInfoByMerName(requestType, keyWord);
        String message = "";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("result", resultList);
        if (CommonMethodUtils.isEmpty(resultList)) {
            // 数据库不存在此关键字信息，发送信息通知爬虫爬取数据
            redisPubService.sendMsg(requestType, keyWord);
            message = keyWord + "：" + "已经加入待爬列表中，等待爬取。请稍后再进行信息查询。";
            jsonResult = new JsonResult(JSON_RESULT_TYPE.failure, message, map);
        } else {
            message = "获取" + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese() + "信息成功！";
            jsonResult = new JsonResult(JSON_RESULT_TYPE.success, message, map);
        }
        return jsonResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public JsonResult processFastlyByMap(String requestType, Map<String, String> paramMap) {
        JsonResult jsonResult = null;
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String message = "";
        String keyWord = JSONObject.toJSONString(paramMap);
        if (BaseConstant.TaskListType.DISHONEST_BAIDU_LIST.getValue().equals(requestType)) {
            int count = creepersDishonestBaiduDao.countByNameAndCertNo(paramMap.get("name"), paramMap.get("certNo"));
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.putAll(paramMap);
            map.put("source", "最高法");
            map.put("count", count);
            listMap.add(map);
            dataMap.put(CreepersConstant.TableNamesOthers.T_CREEPERS_DISHONEST_BAIDU.getMapKey(), listMap);
            Map resultMap = new HashMap();
            resultMap.put("result", dataMap);
            if (count <= 0) {
                message = "网络异常，无法确定：" + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                        + "中是否存在" + keyWord;
                jsonResult = new JsonResult(JSON_RESULT_TYPE.failure, message, resultMap);
                String jsonStr = HttpClientUtil.getInstance().sendHttpGet(
                        "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E5%A4%B1%E4%BF%A1%E8%A2%AB%E6%89%A7%E8%A1%8C%E4%BA%BA%E5%90%8D%E5%8D%95&cardNum="
                                + paramMap.get("certNo") + "&iname=" + paramMap.get("name")
                                + "&areaName=&ie=utf-8&oe=utf-8&format=json");
                if (null != jsonStr) {
                    if (JsonValidator.validate(jsonStr)) {
                        List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
                        JSONObject json = JSONObject.parseObject(jsonStr);
                        JSONArray datajsonArr = json.getJSONArray("data");
                        if (datajsonArr.size() > 0) {
                            JSONArray jsonArr = datajsonArr.getJSONObject(0).getJSONArray("result");
                            for (int i = 0; i < jsonArr.size(); i++) {
                                HashMap<String, Object> mapTemp = new HashMap<String, Object>();
                                mapTemp.put("name", jsonArr.getJSONObject(i).get("iname"));
                                mapTemp.put("certNo", jsonArr.getJSONObject(i).get("cardNum"));
                                mapTemp.put("caseCode", jsonArr.getJSONObject(i).get("caseCode"));
                                mapTemp.put("content", jsonArr.getJSONObject(i).toJSONString());
                                mapList.add(mapTemp);
                            }
                        }
                        if (!CommonMethodUtils.isEmpty(mapList)) {
                            try {
                                // map转entity
                                List<TCreepersDishonestBaidu> entityList = new ArrayList<TCreepersDishonestBaidu>();
                                for (Map<String, Object> mapItem : mapList) {
                                    TCreepersDishonestBaidu entity = new TCreepersDishonestBaidu();
                                    CommonMethodUtils.mapTransEntity(mapItem, entity);
                                    CommonMethodUtils.setByDT(entity);
                                    entityList.add(entity);
                                    creepersDishonestBaiduServiceImpl.saveEntity(entity);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            map.put("count", mapList.size());
                            message = "获取"
                                    + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                                    + "信息成功！";
                        } else {
                            message = keyWord + "："
                                    + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                                    + "不存在此关键字！";
                        }
                        jsonResult.setType(JSON_RESULT_TYPE.success.name());
                        jsonResult.setMessage(message);
                    }
                }
            } else {
                message = "获取" + BaseConstant.TaskListType.valueOf(requestType.toUpperCase()).getNameChinese()
                        + "信息成功！";
                jsonResult = new JsonResult(JSON_RESULT_TYPE.success, message, resultMap);
            }
        }
        return jsonResult;
    }
}
