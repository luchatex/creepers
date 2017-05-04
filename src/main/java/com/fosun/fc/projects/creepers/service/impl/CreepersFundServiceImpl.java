package com.fosun.fc.projects.creepers.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.convert.FundConvert;
import com.fosun.fc.projects.creepers.dao.CreepersFundBasicDao;
import com.fosun.fc.projects.creepers.dao.CreepersFundBasicDetailDao;
import com.fosun.fc.projects.creepers.dao.CreepersFundExtraDao;
import com.fosun.fc.projects.creepers.dao.CreepersFundExtraDetailDao;
import com.fosun.fc.projects.creepers.dao.CreepersFundLoansDao;
import com.fosun.fc.projects.creepers.dao.CreepersListFundDao;
import com.fosun.fc.projects.creepers.downloader.FundSimulateLoginDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasic;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasicDetail;
import com.fosun.fc.projects.creepers.entity.TCreepersFundExtra;
import com.fosun.fc.projects.creepers.entity.TCreepersFundExtraDetail;
import com.fosun.fc.projects.creepers.entity.TCreepersFundLoans;
import com.fosun.fc.projects.creepers.pageprocessor.FundProcessor;
import com.fosun.fc.projects.creepers.pipeline.FundPipline;
import com.fosun.fc.projects.creepers.redis.service.Impl.AbstractRedisCacheService;
import com.fosun.fc.projects.creepers.service.ICreepersFundService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.code.BaseCode;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 
 * <p>
 * description:
 * </p>
 * 
 * @author pengyk
 * @since 2016年9月07日
 * @see
 */
@Service
@Transactional
public class CreepersFundServiceImpl extends CreepersBaseServiceImpl implements ICreepersFundService {

    @Autowired
    private CreepersFundBasicDao creepersFundBasicDao;
    @Autowired
    private CreepersFundBasicDetailDao creepersFundBasicDetailDao;
    @Autowired
    private CreepersFundExtraDao creepersFundExtraDao;
    @Autowired
    private CreepersFundExtraDetailDao creepersFundExtraDetailDao;
    @Autowired
    private FundProcessor fundProcessor;
    @Autowired
    private FundPipline fundPipline;
    @Autowired
    private CreepersFundLoansDao creepersFundLoansDao;
    @Autowired
    private CreepersListFundDao creepersListFundDao;
    @Autowired
    private AbstractRedisCacheService<String,String> redisCacheServiceImpl;
    @Autowired
    private RedisTemplate<String,Long> stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 公积金查询
     */
    @Override
    public Map<String, Object> findByLoginNameForMap(String loginName) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<TCreepersFundBasic> entityBasicList = creepersFundBasicDao.findByLoginName(loginName);
        List<TCreepersFundBasicDetail> entityBasicDetailList = creepersFundBasicDetailDao.findByLoginName(loginName);
        List<TCreepersFundExtra> entityExtraList = creepersFundExtraDao.findByLoginName(loginName);
        List<TCreepersFundExtraDetail> entityExtraDetailList = creepersFundExtraDetailDao.findByLoginName(loginName);
        List<TCreepersFundLoans> entityLoansList = creepersFundLoansDao.findByLoginName(loginName);

        List<Map<String, Object>> basicList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> basicDetailList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> extraList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> extraDetailList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> loansList = new ArrayList<Map<String, Object>>();
        try {
            if (!CommonMethodUtils.isEmpty(entityBasicList)) {
                basicList = CommonMethodUtils.entityList(entityBasicList);
                result.put(CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC.getMapKey(), basicList);
            }

            if (!CommonMethodUtils.isEmpty(entityBasicDetailList)) {
                basicDetailList = CommonMethodUtils.entityList(entityBasicDetailList);
                result.put(CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC_DETAIL.getMapKey(), basicDetailList);
            }

            if (!CommonMethodUtils.isEmpty(entityExtraList)) {
                extraList = CommonMethodUtils.entityList(entityExtraList);
                result.put(CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA.getMapKey(), extraList);
            }

            if (!CommonMethodUtils.isEmpty(entityExtraDetailList)) {
                extraDetailList = CommonMethodUtils.entityList(entityExtraDetailList);
                result.put(CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA_DETAIL.getMapKey(), extraDetailList);
            }

            if (!CommonMethodUtils.isEmpty(entityLoansList)) {
                loansList = CommonMethodUtils.entityList(entityLoansList);
                result.put(CreepersConstant.TableNamesFund.T_CREEPERS_FUND_LOANS.getMapKey(), loansList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("CreepersFundServiceImpl findByLoginNameForMap error:", e);
            CreepersLoginParamDTO param = new CreepersLoginParamDTO();
            param.setLoginName(loginName);
            param.setErrorInfo("CreepersFundServiceImpl findByLoginNameForMap error:" + e.getCause().getClass()
                    + e.getCause().getMessage());
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
            logger.error("======================>>>  CreepersFundServiceImpl.findByLoginNameForMap end!");
        }
        return result;
    }

    @Override
    public Map<String, Object> findByLoginNameForMapForRest(String loginName) throws Exception {
        List<TCreepersFundBasic> entityBasicList = creepersFundBasicDao.findByLoginName(loginName);
        List<TCreepersFundBasicDetail> entityBasicDetailList = creepersFundBasicDetailDao.findByLoginName(loginName);
        List<TCreepersFundLoans> entityLoansList = creepersFundLoansDao.findByLoginName(loginName);
        return FundConvert.convert(entityBasicList, entityBasicDetailList, entityLoansList);
    }
    

    @SuppressWarnings("unchecked")
    public Map<String, Object> findByLoginNameForMapFromRedis(CreepersLoginParamDTO param) throws Exception {
        String basicKey = param.getTaskType() + "|" + param.getLoginName() + "|"
                + CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC.getMapKey();
        Object basicJson = redisCacheServiceImpl.get(basicKey);
        Map<String, String> basicMap = null == basicJson ? new HashMap<String,String>()
                : JSON.parseObject((String) basicJson, HashMap.class);
        List<TCreepersFundBasic> entityBasicList = new ArrayList<TCreepersFundBasic>();
        TCreepersFundBasic basic = new TCreepersFundBasic();
        CommonMethodUtils.mapTransEntity(basicMap, basic);
        if(null != basicJson)
            entityBasicList.add(basic);
        
        String basicDetailKey = param.getTaskType() + "|" + param.getLoginName() + "|"
                + CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC_DETAIL.getMapKey();
        Object basicDetailListJson = redisCacheServiceImpl.get(basicDetailKey);
        List<TCreepersFundBasicDetail> entityBasicDetailList = new ArrayList<TCreepersFundBasicDetail>();
        List<HashMap<String, String>> basicDetailList = new ArrayList<HashMap<String,String>>();
        if(null!=basicDetailListJson){
            JSONArray basicDetailArray = JSONArray.parseArray((String)basicDetailListJson);
            for(int i=0;i<basicDetailArray.size();i++){
                HashMap<String, String> basicDetailMap = JSONObject.parseObject(basicDetailArray.getJSONObject(i).toJSONString(), HashMap.class);
                basicDetailList.add(basicDetailMap);
            }
            entityBasicDetailList=CommonMethodUtils.mapList(basicDetailList, new TCreepersFundBasicDetail());
        }
        
        String loansKey = param.getTaskType() + "|" + param.getLoginName() + "|"
                + CreepersConstant.TableNamesFund.T_CREEPERS_FUND_LOANS.getMapKey();
        Object loansJson = redisCacheServiceImpl.get(loansKey);
        Map<String, String> loansMap = null == loansJson ? new HashMap<String, String>()
                : JSON.parseObject((String) loansJson, HashMap.class);
        List<TCreepersFundLoans> entityLoansList = new ArrayList<TCreepersFundLoans>();
        TCreepersFundLoans loans = new TCreepersFundLoans();
        CommonMethodUtils.mapTransEntity(loansMap, loans);
        if(null!=loansJson)
            entityLoansList.add(loans);
        return FundConvert.convert(entityBasicList, entityBasicDetailList, entityLoansList);
    }


    /**
     * 公积金查询
     */
    @Override
    public void processByParam(CreepersLoginParamDTO param) {

        logger.info("=============>CreepersFundServiceImpl.processByMerName start!");
        // 初始化Param DTO
        param.setTaskType(BaseConstant.TaskListType.FUND_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, "https://persons.shgjj.com/");
        param.putOrderUrl(BaseConstant.OrderUrlKey.CAPTCHA_URL, "https://persons.shgjj.com/VerifyImageServlet");
        param.putOrderUrl(BaseConstant.OrderUrlKey.DO_LOGIN_URL, "https://persons.shgjj.com/SsoLogin");
        param.setCaptchaKey("imagecode");
        param.setLoginNameKey("username");
        param.setPasswordKey("password");
        List<String> targetUrlList = new ArrayList<String>();
        targetUrlList.add("https://persons.shgjj.com/MainServlet?ID=1");
        targetUrlList.add("https://persons.shgjj.com/MainServlet?ID=11");
        targetUrlList.add("https://persons.shgjj.com/MainServlet?ID=3");
        targetUrlList.add("https://persons.shgjj.com/MainServlet?ID=31");
        targetUrlList.add("https://persons.shgjj.com/MainServlet?ID=5");
        param.setTargetUrlList(targetUrlList);
        param.putNameValuePair("SUBMIT.x", (int) (Math.random() * 70) + 2 + "");
        param.putNameValuePair("SUBMIT.y", (int) (Math.random() * 20) + 2 + "");
        param.putNameValuePair("password_md5", BaseCode.encryptMD5To16(param.getPassword()));
        Request request = CommonMethodUtils.buildIndexRequest(param);
        // 启动爬虫
        logger.info("=============>启动爬虫!");
        Spider.create(fundProcessor).addPipeline(fundPipline)
                .setDownloader(new FundSimulateLoginDownloader().setParam(param)).addRequest(request).thread(3).run();
        logger.info("=============>CreepersFundServiceImpl.processByMerName start!");

    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateFund(Map<String, Object> map, String taskType, String loginName) throws Exception {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC.getMapKey().equals(entry.getKey())) {
                if (CommonMethodUtils.isEmpty(entry.getValue())) {
                   continue;
                }else {
                    Map<String, String> basicMap = (HashMap<String, String>) entry.getValue();
                    creepersFundBasicDao.deleteByLoginName(basicMap.get(CreepersConstant.TCreepersFundBasicColumn.LOGIN_NAME.getValue()));
                    logger.info("step: ======>> entry saveEntity");
                    TCreepersFundBasic entity = new TCreepersFundBasic();
                    CommonMethodUtils.mapTransEntity(basicMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    creepersFundBasicDao.saveAndFlush(entity);
                    String key = taskType+"|"+loginName+"|"+CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC.getMapKey();
                    redisCacheServiceImpl.set(key, JSON.toJSONString(basicMap));
                    redisCacheServiceImpl.expire(key, 3600L, TimeUnit.SECONDS);
                    logger.info("step: ======>> saveEntity succeed!");
                }
            } else
                if (CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC_DETAIL.getMapKey().equals(entry.getKey())) {
                if (CommonMethodUtils.isEmpty(entry.getValue())) {
                    continue;
                } else {
                    List<HashMap<String, String>> basicDetailList = (List<HashMap<String, String>>) entry.getValue();
                    creepersFundBasicDetailDao.deleteByLoginName(basicDetailList.get(0)
                            .get(CreepersConstant.TCreepersFundBasicDetailColumn.LOGIN_NAME.getValue()));
                    for (HashMap<String, String> basicDetailMap : basicDetailList) {
                        TCreepersFundBasicDetail entity = new TCreepersFundBasicDetail();
                        CommonMethodUtils.mapTransEntity(basicDetailMap, entity);
                        CommonMethodUtils.setByDT(entity);
                        logger.info("step: ======>> entry saveList");
                        creepersFundBasicDetailDao.saveAndFlush(entity);
                    }
                    String key = taskType+"|"+loginName+"|"+CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC_DETAIL.getMapKey();
                    redisCacheServiceImpl.set(key, JSON.toJSONString(basicDetailList));
                    redisCacheServiceImpl.expire(key, 3600L, TimeUnit.SECONDS);
                    logger.info("step: ======>> saveEntity succeed!");
                }
                logger.info("step: ======>> saveList succeed!");
            } else if (CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA.getMapKey().equals(entry.getKey())) {
                if (CommonMethodUtils.isEmpty(entry.getValue())) {
                    continue;
                } else {
                    HashMap<String, String> extraMap = (HashMap<String, String>) entry.getValue();
                    creepersFundExtraDao.deleteByLoginName(extraMap.get(CreepersConstant.TCreepersFundExtraColumn.LOGIN_NAME.getValue()));
                    logger.info("step: ======>> entry saveEntity");
                    TCreepersFundExtra entity = new TCreepersFundExtra();
                    CommonMethodUtils.mapTransEntity(extraMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    creepersFundExtraDao.saveAndFlush(entity);
                    String key = taskType+"|"+loginName+"|"+CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA.getMapKey();
                    redisCacheServiceImpl.set(key, JSON.toJSONString(extraMap));
                    redisCacheServiceImpl.expire(key, 3600L, TimeUnit.SECONDS);
                    logger.info("step: ======>> saveEntity succeed!");
                }
            } else
                if (CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA_DETAIL.getMapKey().equals(entry.getKey())) {
                if (CommonMethodUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                List<HashMap<String, String>> extraDetailList = (List<HashMap<String, String>>) entry.getValue();
                creepersFundExtraDetailDao.deleteByLoginName(extraDetailList.get(0)
                        .get(CreepersConstant.TCreepersFundExtraDetailColumn.LOGIN_NAME.getValue()));
                for (HashMap<String, String> extraDetailMap : extraDetailList) {
                    TCreepersFundExtraDetail entity = new TCreepersFundExtraDetail();
                    CommonMethodUtils.mapTransEntity(extraDetailMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    logger.info("step: ======>> entry saveList");
                    creepersFundExtraDetailDao.saveAndFlush(entity);
                }
                String key = taskType+"|"+loginName+"|"+CreepersConstant.TableNamesFund.T_CREEPERS_FUND_EXTRA_DETAIL.getMapKey();
                redisCacheServiceImpl.set(key, JSON.toJSONString(extraDetailList));
                redisCacheServiceImpl.expire(key, 3600L, TimeUnit.SECONDS);
                logger.info("step: ======>> saveList succeed!");
            } else if (CreepersConstant.TableNamesFund.T_CREEPERS_FUND_LOANS.getMapKey().equals(entry.getKey())) {
                if (CommonMethodUtils.isEmpty(entry.getValue())) {
                    continue;
                }else {
                    HashMap<String, String> loansMap = (HashMap<String, String>) entry.getValue();
                    creepersFundLoansDao.deleteByLoginName(loansMap.get(CreepersConstant.TCreepersFundLoansColumn.LOGIN_NAME.getValue()));
                    logger.info("step: ======>> entry saveEntity");
                    TCreepersFundLoans entity = new TCreepersFundLoans();
                    CommonMethodUtils.mapTransEntity(loansMap, entity);
                    CommonMethodUtils.setByDT(entity);
                    creepersFundLoansDao.saveAndFlush(entity);
                    String key = taskType+"|"+loginName+"|"+CreepersConstant.TableNamesFund.T_CREEPERS_FUND_LOANS.getMapKey();
                    redisCacheServiceImpl.set(key, JSON.toJSONString(loansMap));
                    redisCacheServiceImpl.expire(key, 3600L, TimeUnit.SECONDS);
                    logger.info("step: ======>> saveEntity succeed!");
                }
            }
        }
        String redisKey = taskType + "|" + loginName;
        Long count = stringRedisTemplate.opsForValue().increment(redisKey,1);
        System.err.println("================>"+count+"<================");
        if (null != count && count >= 6) {
            logger.info("step: ======>> update listFund");
            creepersListFundDao.updateListByUserCode(loginName, BaseConstant.TaskListStatus.SUCCEED.getValue());
            redisCacheServiceImpl.expire(redisKey, 3600L, TimeUnit.SECONDS);
        }
        logger.info("step: ======>> FundDetailPipline Insert DB succeed!");
    }

    /**
     * 公积金删除
     */
    @Override
    public void deleteByLoginName(String loginName) {
        creepersFundBasicDao.deleteByLoginName(loginName);
        creepersFundBasicDetailDao.deleteByLoginName(loginName);
        creepersFundExtraDao.deleteByLoginName(loginName);
        creepersFundExtraDetailDao.deleteByLoginName(loginName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T get(Map map, String key) {
        Object o = map.get(key);
        if (o == null) {
            return null;
        }
        return (T) map.get(key);
    }

}
