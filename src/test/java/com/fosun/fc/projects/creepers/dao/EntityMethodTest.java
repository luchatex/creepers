package com.fosun.fc.projects.creepers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.entity.TCreepersTempShixin;
import com.fosun.fc.projects.creepers.service.ICreepersListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;


public class EntityMethodTest extends SpiderBaseTest{

    // @Autowired(required = true)
    // private CreepersListShixinDao creepersListShixinDao;
    // @Autowired(required = true)
    // private CreepersListCreditDao creepersListCreditDao;
    // @Autowired(required = true)
    // private CreepersListTourGuideDao creepersListTourGuideDao;
    @Autowired(required = true)
    private CreepersCourtCorpBondsDao creepersCourtCorpBondsDao;

    @Autowired(required = true)
    private ICreepersTaskListService creepersTaskListServiceImpl;
    
    @Autowired(required = true)
    private ICreepersListService creepersListServiceImpl;
    
    @Autowired(required = true)
    private CreepersTaskListDao creepersTaskListDao;

    @Autowired
    private CreepersTempShixinDao creepersTempShixinDao;
    
    public void methodTest() {
        /*
         * TCreepersAccount entity = new TCreepersAccount();
         * entity.setUpdatedBy("test"); entity.setCreatedDt(new Date());
         * entity.setUsr("CRE3"); entity.setPwd("888888");
         * entity.setRptNo("100000000001"); entity.setStatus("0");
         * entity.setFilePath("D:/"); entity.setCde("sa90");
         * entity.setCreatedBy("test"); entity.setUpdatedDt(new Date()); //
         * creepersAccountDao.save(entity);
         */

        /*
         * TCreepersBasic basicEntity = new TCreepersBasic();
         * basicEntity.setRptNo("100000000001"); basicEntity.setQueryDt(new
         * Date()); basicEntity.setRptDt(new Date());
         * basicEntity.setName("asd"); basicEntity.setIdType("1");
         * basicEntity.setIdNo("17784757585");
         * CommonMethodUtils.setByDT(basicEntity);
         */

        // TCreepersProxyList entity = new TCreepersProxyList();
        // entity.setId(308);
        // entity.setIp("222.174.72.122");
        // entity.setPort("99992");
        // entity.setIp("HTTP/HTTPS");
        // CommonMethodUtils.setByDT(entity);
        // creepersProxyListDao.saveAndFlush(entity);
        /*
         * List<TCreepersProxyList> entityList = creepersProxyListDao.findAll();
         * StringBuffer result = new StringBuffer(); StringBuffer proxyList =
         * new StringBuffer(); proxyList.append(
         * "List<String[]> list = new ArrayList<String[]>();\n"); for
         * (TCreepersProxyList entity : entityList) {
         * result.append(entity.getIp()).append(" "
         * ).append(entity.getPort()).append("\n");
         * 
         * try { Integer.valueOf(entity.getPort());
         * proxyList.append("list.add(").append("new String[]{\""
         * ).append(entity.getIp()).append("\",\"")
         * .append(entity.getPort()).append("\"});").append("\n"); } catch
         * (Exception e) { System.out.println(
         * "port is not a int,can't be translated!"); } }
         * System.out.println(proxyList.toString()); try { File file = new
         * File("/webmagic/proxyFile/"); if (file.exists()) { // to do nothing.
         * } else { file.mkdirs(); } PrintWriter pw = new PrintWriter(new
         * FileWriter(new File("/webmagic/proxyFile/proxy.txt")));
         * pw.write(result.toString()); pw.flush(); pw.close(); } catch
         * (IOException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         * 
         * 
         */
        // String requestType =
        // BaseConstant.TaskListType.JUDGEMENT_LIST.getValue();
        // long count = creepersConfigDao.countByRequestType(requestType);
        // TCreepersConfig oldEntity =
        // creepersConfigDao.findTop1ByRequestType(requestType);
        // TCreepersConfig entity = new TCreepersConfig();
        // entity.setId(oldEntity.getId());
        // entity.setRequestType(BaseConstant.TaskListType.JUDGEMENT_LIST.getValue());
        // entity.setAgentFlag("1");
        // entity.setCdRequestCode("setCdRequestCode");
        // entity.setCdUrl("setCdUrl");
        // entity.setDomain("setDomain");
        // entity.setHeaders("setHeaders");
        // entity.setRetryTimes("3");
        // entity.setRootUrl("setRootUrl");
        // entity.setSwitchFlag("0");
        // entity.setThreadNum("11");
        // entity.setTimeOut("3000000");
        // entity.setUserAgent("setUserAgent");
        // CommonMethodUtils.setByDT(entity);
        // creepersConfigDao.saveAndFlush(entity);
        // creepersListShixinDao.updateListByMerName("111", "0");
        // TCreepersListCredit entity = new TCreepersListCredit();
        // entity.setUserCode("1234");
        // CommonMethodUtils.setByDT(entity);
        // creepersListCreditDao.save(entity);

        /*
         * CreepersLoginParamDTO dto = new CreepersLoginParamDTO();
         * dto.setLoginName("123"); dto.setPassword("456");
         * dto.setMessageCaptchaValue("789"); String str =
         * JSONObject.toJSONString(dto); System.out.println(str);
         */
        // List<TCreepersListCredit> result =
        // creepersListCreditDao.findByUserCode("1234");
        // for (TCreepersListCredit eachEntity : result) {
        // System.err.println(eachEntity.toString());
        // }
        // TCreepersListTourGuide entity = new TCreepersListTourGuide();
        // entity.setCardNo("60065813");
        // entity.setCertNo("411");
        // entity.setGuideNo("D-4502-009024");
        // CommonMethodUtils.setByDT(entity);
        // creepersListTourGuideDao.save(entity);
        // List<TCreepersListTourGuide> result = creepersListTourGuideDao
        // .findByGuideNoAndCertNoOrCardNoAndCertNo("D-4502-009024", "411", "",
        // "411");
        // System.err.println("=======>>>:" + result.size());

        // List<TCreepersCourtCorpBonds> oldList =
        // creepersCourtCorpBondsDao.findByName("吉林成城集团股份有限公司");
        // TCreepersCourtCorpBonds entity1 = oldList.get(0);
        // entity1.setProvince("河南3");
        // creepersCourtCorpBondsDao.save(entity1);
        // TCreepersCourtCorpBonds entity2 = oldList.get(0);
        // entity2.setProvince("河南4");
        // creepersCourtCorpBondsDao.save(entity2);
        // TCreepersCourtCorpBonds entity3 = oldList.get(0);
        // entity1.setProvince("河南5");
        // creepersCourtCorpBondsDao.save(entity3);
        // TCreepersCourtCorpBonds entity4 = oldList.get(0);
        // entity2.setProvince("河南6");
        // creepersCourtCorpBondsDao.save(entity4);
        // List<TCreepersCourtCorpBonds> oldList1 =
        // creepersCourtCorpBondsDao.findByName("吉林成城集团股份有限公司");
        // System.err.println(oldList1);
        // System.out.println("最后一次查询完成！");
//        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.err.println(dateFormater.format(new Date().getTime()));
//        CreepersTaskListDTO dto = new CreepersTaskListDTO();
//        dto.setTaskType("admin_license_list");
//        dto.setUrl("http://www.creditchina.gov.cn/publicity_info_search?t=");
//        dto.setHttpType("post");
//        dto.setParamMap("{\"areaCode\":\"\",\"searchtype\":\"1\",\"dataType\":\"1\",\"exact\":\"0\",\"areas\":\"\",\"page\":\"1\",\"keyword\":\"\",\"templateId\":\"\",\"creditType\":\"\",\"objectType\":\"2\"}");
//        creepersTaskListServiceImpl.initTaskList(dto, "525923","525921", "");
//        System.err.println(dateFormater.format(new Date().getTime()));
//        List<String> flagList = new ArrayList<String>();
//        flagList.add("0");
//        flagList.add("2");
//        List<TCreepersTaskList> taskLists=creepersTaskListDao.findByTaskTypeAndFlagIn("medical_GMP_list", flagList);
//        System.err.println(1);
//        creepersTaskListDao.deleteByTaskType("medical_GSP_list");
        
//        creepersListServiceImpl.addTaskByRedisPush(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue(), BaseConstant.ProcessByRedisType.BREAK_POINT_DATA.getValue());
//        System.out.println("===================>>>>>   push finashed!");
        
//        creepersTaskListServiceImpl.clearRedisCache("test");
        List<TCreepersTempShixin> entityList = creepersTempShixinDao.findAll();
        System.err.println("ok");
        
    }

    @Test
    public void run() {
        methodTest();
    }

    public static void main(String args[]) {
//        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long times = System.currentTimeMillis();
//        String time = dateFormater.format(new java.util.Date().getTime());
//        System.out.print(time);
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("keyword", "");
//        map.put("searchtype", "1");
//        map.put("objectType", "2");
//        map.put("areas", "");
//        map.put("creditType", "");
//        map.put("dataType", "1");
//        map.put("areaCode", "");
//        map.put("templateId", "");
//        map.put("exact", "0");
//        map.put("page", "1");
//        String jsonString = JSON.toJSONString(map);
//        System.err.println(jsonString);
    }

}
