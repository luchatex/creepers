package com.fosun.fc.projects.creepers.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.modules.utils.JsonResult.JSON_RESULT_TYPE;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.dao.CreepersFundBasicDao;
import com.fosun.fc.projects.creepers.dao.CreepersFundBasicDetailDao;
import com.fosun.fc.projects.creepers.dao.CreepersListFundDao;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasic;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasicDetail;
import com.fosun.fc.projects.creepers.entity.TCreepersListFund;
import com.fosun.fc.projects.creepers.redis.service.IRedisPubService;
import com.fosun.fc.projects.creepers.redis.service.Impl.AbstractRedisCacheService;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;
import com.fosun.fc.projects.creepers.service.ICreepersFundService;
import com.fosun.fc.projects.creepers.service.ICreepersListFundService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

@Service
@Transactional
public class CreepersListFundServiceImpl implements ICreepersListFundService {

    @Autowired
    private CreepersListFundDao creepersListFundDao;
    @Autowired
    private ICreepersFundService creepersFundServiceImpl;
    @Autowired
    private IRedisPubService redisPubService;
    @Autowired
    private RedisTemplate<String, Long> stringRedisTemplate;
    @Autowired
    private CreepersFundBasicDao creepersFundBasicDao;
    @Autowired
    private CreepersFundBasicDetailDao creepersFundBasicDetailDao;
    @Autowired
    private AbstractRedisCacheService<String, String> redisCacheServiceImpl;
    @Autowired
    protected RedisScheduler redisSchedul;
    
    /**
     * <p>
     * description:个人公积金查询，若数据库中listfund表中存在该用户的爬取成功记录且更新时间小于一个月，
     * 基础数据表中有数据则表示爬取成功直接返回成功状态和数据，否则在listfund表中保存或更新爬取记录为正在爬取状态并启动爬虫爬取数据，
     * 再把该信息返回给用户。
     * </p>
     * 
     * @param param
     *            登录信息
     * @return
     * @throws Exception
     * @author LiZhanPing
     * @see
     */
    @Override
    public JsonResult<Map<String, Object>> queryByParam(CreepersLoginParamDTO param) throws Exception {

        JsonResult<Map<String, Object>> jsonResult = null;
        Map<String, Object> map = new HashMap<String, Object>();
        String message = "";
        Map<String, Object> data = null;
        if ("database".equals(processFlag(param))) {
            message = "账号为" + param.getLoginName() + "的个人公积金信息爬取成功";
            data = creepersFundServiceImpl.findByLoginNameForMapForRest(param.getLoginName());
            map.put("data", data);
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
        } else if ("rediscache".equals(processFlag(param))) {
            message = "账号为" + param.getLoginName() + "的个人公积金信息爬取成功";
            data = creepersFundServiceImpl.findByLoginNameForMapFromRedis(param);
            map.put("data", data);
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
        } else {
            String redisKey = param.getTaskType() + "|" + param.getLoginName();
            Object count = stringRedisTemplate.opsForValue().get(redisKey);
            if (null == count) {
                // 查询任务List，结果为空或状态不为1则启动爬寻
                List<TCreepersListFund> oldTaskList = creepersListFundDao.findByUserCode(param.getLoginName());
                TCreepersListFund entity = null;
                if (!CommonMethodUtils.isEmpty(oldTaskList)) {
                    entity = oldTaskList.get(0);
                    entity.setPassword(param.getPassword());
                    entity.setId(entity.getId());
                    entity.setVersion(entity.getVersion());
                    entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
                    entity.setUpdatedDt(new Date());
                } else {
                    entity = new TCreepersListFund();
                    entity.setUserCode(param.getLoginName());
                    entity.setPassword(param.getPassword());
                    entity.setFlag(BaseConstant.TaskListStatus.DEFAULT.getValue());
                    CommonMethodUtils.setByDT(entity);
                }
                creepersListFundDao.save(entity);
                // 数据库中没记录，调用消息层发起请求
                redisSchedul.pushParam(param.getTaskType(), param);
                redisPubService.sendMsg(param.getTaskType(), param.getTaskType());
            }
            message = "账号为" + param.getLoginName() + "的个人公积金信息正在爬取";
            map.put("data", data);
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
        }
        return jsonResult;
    }

    private String processFlag(CreepersLoginParamDTO param) {
        String flag = "false";
        List<TCreepersListFund> entityList = creepersListFundDao.findByUserCode(param.getLoginName());
        List<TCreepersFundBasic> basicList = creepersFundBasicDao.findByLoginName(param.getLoginName());
        if (!CommonMethodUtils.isEmpty(entityList)
                && BaseConstant.TaskListStatus.SUCCEED.getValue().equals(entityList.get(0).getFlag())
                && (System.currentTimeMillis() - entityList.get(0).getUpdatedDt().getTime())
                        / (24 * 60 * 60 * 1000) <= 30
                && basicList.size() > 0) {
            flag = "database";
        }
        String redisKey = param.getTaskType() + "|" + param.getLoginName();
        Object count = stringRedisTemplate.opsForValue().get(redisKey);
        String accountsKey = param.getTaskType() + "|" + param.getLoginName() + "|"
                + CreepersConstant.TableNamesFund.T_CREEPERS_FUND_BASIC.getMapKey();
        Object accounts = redisCacheServiceImpl.get(accountsKey);
        if (null != count &&  Long.valueOf((String)count) >= 5 && accounts != null) {
            flag = "rediscache";
        }
        return flag;
    }

    /**
     * <p>
     * description:爬取状态查询，用于用户数据过多且jpa入库速度慢（有的用户数据达到200条，入库速度竟然需要一分钟）
     * 导致发生爬取记录表成功但是数据表没有数据的情况，从而引入缓存机制，公积金账户明细表为关键数据若不存在则将状态置为4，
     * 先查redis缓存中查询是否存在数据（缓存数据存在有效期，有效期为1小时），若存在则状态置为1（若其中公积金账户明细表不存在的话置为4），
     * 若不存在则查询查数据库，状态为0表示正在爬取，状态为1表示爬取成功，（若其中公积金账户明细表不存在的话置为4），状态为2表示爬取失败，
     * 状态为3表示用户名或密码错误，状态为4表示网络不稳定
     * </p>
     * 
     * @param param
     * @return
     * @author LiZhanPing
     * @see
     */
    @Override
    public JsonResult<Map<String, Object>> queryStatusByParam(CreepersLoginParamDTO param) {

        JsonResult<Map<String, Object>> jsonResult = null;
        String message = "";
        String flag = "";
        List<TCreepersListFund> entityList = creepersListFundDao.findByUserCode(param.getLoginName());
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        String key = param.getTaskType() + "|" + param.getLoginName();
        Object count = stringRedisTemplate.opsForValue().get(key);
        String flowsKey = param.getTaskType() + "|" + param.getLoginName() + "|"
                + CreepersConstant.TableNamesFund.T_CREEPERS_FUND_LOANS.getMapKey();
        Object flows = redisCacheServiceImpl.get(flowsKey);
        if (null != count && Long.valueOf((String)count) >= 5) {
            if (null != flows) {
                data.put("flag", "1");
                message = "账号为" + param.getLoginName() + "的个人公积金信息爬取成功";
                data.put("message", message);
                map.put("data", data);
                jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
            } else {
                data.put("flag", "4");
                message = "账号为" + param.getLoginName() + "的个人公积金信息爬取过程中网络不稳定";
                data.put("message", message);
                map.put("data", data);
                jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
                creepersListFundDao.updateListByUserCode(param.getLoginName(), "4");
            }
        } else {
            if (!CommonMethodUtils.isEmpty(entityList)) {
                flag = entityList.get(0).getFlag();
                if ("0".equals(flag)) {
                    data.put("flag", flag);
                    message = "账号为" + param.getLoginName() + "的个人公积金信息正在爬取";
                    data.put("message", message);
                    map.put("data", data);
                    jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
                } else if ("1".equals(flag)) {
                    List<TCreepersFundBasicDetail> basicDetails = creepersFundBasicDetailDao
                            .findByLoginName(param.getLoginName());
                    if (basicDetails.size() <= 0) {
                        data.put("flag", "4");
                        message = "账号为" + param.getLoginName() + "的个人公积金信息爬取过程中网络不稳定";
                        data.put("message", message);
                        map.put("data", data);
                        jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
                    } else {
                        data.put("flag", flag);
                        message = "账号为" + param.getLoginName() + "的个人公积金信息爬取成功";
                        data.put("message", message);
                        map.put("data", data);
                        jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.success, message, map);
                        creepersListFundDao.updateListByUserCode(param.getLoginName(), "4");
                    }
                } else if ("2".equals(flag)){
                    data.put("flag", flag);
                    message = "账号为" + param.getLoginName() + "的个人公积金信息爬取失败";
                    data.put("message", message);
                    map.put("data", data);
                    jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
                } else if ("3".equals(flag)) {
                    data.put("flag", flag);
                    message = "账号为" + param.getLoginName() + "的个人公积金信息的用户名或密码错误";
                    data.put("message", message);
                    map.put("data", data);
                    jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
                } else {
                    data.put("flag", flag);
                    message = "账号为" + param.getLoginName() + "的个人公积金信息爬取出现未知错误";
                    data.put("message", message);
                    map.put("data", data);
                    jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
                }
            } else {
                data.put("flag", "2");
                message = "账号为" + param.getLoginName() + "的个人公积金信息爬取失败";
                data.put("message", message);
                map.put("data", data);
                jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, message, map);
            }
        }
        return jsonResult;
    }

    @Override
    public void updateStatus(String loginName, String newFlag) {
        creepersListFundDao.updateListByUserCode(loginName, newFlag);
    }

}
