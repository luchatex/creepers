package com.fosun.fc.projects.creepers.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;

import oracle.net.aso.e;

public class BaseRestControllerTest extends SpiderBaseTest {

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testFund() throws InterruptedException {
        String loginName = "178413";
        String password = "bxy178413";
        Map<String, String> params = new HashMap<String, String>();
        params.put("loginName", loginName);
        params.put("password", password);
        params.put("requestCode", "creepers_fund");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/fund/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map<String, Object> data = null;
        int i = 0;
        if ("success".equals(jsonResult.getType())) {
            data = jsonResult.getData();
        } else {
            for (; ; ) {
                String statusUrl = "http://localhost:8081/creepers/api/fund/checkStatus";
                JsonResult<Map<String, Object>> statusJsonResult = restTemplate.postForObject(statusUrl, JSON.parse(jsonString),
                        JsonResult.class);
                String flag = (String)((Map<String, Object>)statusJsonResult.getData().get("data")).get("flag");
                if ("1".equals(flag)) {
                    jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
                    data = jsonResult.getData();
                    System.err.println((String)((Map<String, Object>)statusJsonResult.getData().get("data")).get("message"));
                    break;
                } else if ("0".equals(flag)) {
                    System.err.println((String)((Map<String, Object>)statusJsonResult.getData().get("data")).get("message"));
                } else {
                    System.err.println((String)((Map<String, Object>)statusJsonResult.getData().get("data")).get("message"));
                    break;
                }
                i=i+3;
                System.err.println("========================"+i+"========================");
                Thread.sleep(3000);
            }
        }
        System.err.println("\njsonResult.getData():====================>>>>" + data);
    }

    // @Test
    public void testDishonestBaidu() {
        String name = "无锡霞鑫塑料科技有限公司";
        String certNo = "";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("certNo", certNo);
        params.put("requestCode", "creepers_shixin_all");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/dishonestBaidu/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        System.out.println(System.currentTimeMillis());
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.out.println(System.currentTimeMillis());
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        if (!jsonObject.isEmpty()) {
            String arrStr = jsonObject.getString("tCreepersDishonestBaidu");
            JSONArray jsonArray = JSONArray.parseArray(arrStr);
            String time = jsonArray.getJSONObject(0).getString("name");
            System.err.println(time);
        }
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Test
    public void testTaxEvasion() {
        String name = "安徽大昌矿业集团有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("requestCode", "creepers_tax_evasion");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/taxEvasion/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("tCreepersTaxEvasion");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("code");
        System.err.println(time);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Test
    public void testSaction() {
        String key = "海南中航建筑工程有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("requestCode", "creepers_saction");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8080/creepers/api/saction/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("tCreepersAdmin");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String object = jsonArray.getString(0);
        String gsname = JSON.parseObject(object).getString("name");
        System.err.println(gsname);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Test
    public void testAdminSaction() {
        String name = "海南韦记美食美客餐饮管理有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", name);
        params.put("requestCode", "creepers_admin_saction");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/saction/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("tCreepersAdmin").trim();
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String encryStr = jsonArray.getJSONObject(0).getString("encryStr");
        System.err.println(encryStr);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Test
    public void testShixinAll() {
        String name = "aa";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("requestCode", "creepers_shixin_all");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/shixinAll/query";
        // String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("tCreepersShixinAll");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("code");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    // @Test
    public void testCourtCorpBonds() {
        String name = "上海微著商贸有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("requestCode", "creepers_court_corp_bonds");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8081/creepers/api/courtCorpBands/query";
        String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("tCreepersCourtCorpBonds");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("code");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    // @Test
    public void testMedicalInstrumentForeign() {
        String key = "国食药监械(进)字2013第2403413号";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("requestCode", "creepers_medical_instrument_foreign");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8080/creepers/api/medicalInstrumentForeign/query";
        String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("medical_instrument_foreign_list");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("piZhunRiQi");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    // @Test
    public void testMedicalInstrumentDomestic() {
        String key = "国食药监械(准)字2014第3460245号";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("requestCode", "creepers__medical_instrument_domestic");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8080/creepers/api/medicalInstrumentDomestic/query";
        String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("medical_instrument_domestic_list");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("piZhunRiQi");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    // @Test
    public void testMedicalGMP() {
        String key = "广西百草堂药业有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("requestCode", "creepers__medical_GMP");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8080/creepers/api/medicalGMP/query";
        String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("medical_GMP_list");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("faZhengRiQi");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    // @Test
    public void testMedicalGSP() {
        String key = "蕲春县李时珍中药饮片厂老街药店";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("requestCode", "creepers__medical_GSP");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        System.err.println("\njsonRequest:====================>>>>" + jsonString);
        String localUrl = "http://localhost:8080/creepers/api/medicalGSP/query";
        String CDUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(localUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        JSONObject jsonObject = (JSONObject) msg.get("result");
        String arrStr = jsonObject.getString("medical_GSP_list");
        JSONArray jsonArray = JSONArray.parseArray(arrStr);
        String time = jsonArray.getJSONObject(0).getString("faZhengShiJian");
        System.err.println(time);
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // @Test
    // 导游证验真测试
    public void testTourGuideInfo() {
        String guideNo = "D-4308-004238";// D-4502-009024
        Map<String, String> params = new HashMap<String, String>();
        params.put("guideNo", guideNo);
        params.put("requestCode", "creepers_tour_guide");  // 调用的接口
        params.put("bizUk", guideNo); // 订单号
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        // 导游证验真
        // String baseUrl =
        // "http://localhost:8080/creepers/api/touristGuide/query";
        // String baseUrl =
        // "http://10.166.10.214:10086/creepers/api/touristGuide/query";
        // String baseUrl =
        // "http://10.166.0.246:10086/creepers/api/touristGuide/query";
        String baseUrl = "http://creepers.mis.liangfuzhengxin.com/creepers/api/touristGuide/query";
        // String baseUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(baseUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // @Test
    // 导游黑名单测试
    public void testTouristGuideBlackList() {
        String type = "2", guideNo = "D-4301-505009";
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", type);
        params.put("guideNo", guideNo);
        params.put("bizUk", guideNo);
        params.put("requestCode", "creepers_tour_blacklist");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        // 导游黑名单
        // String baseUrl =
        // "http://localhost:8080/creepers/api/touristBlackList/query";
        // String baseUrl =
        // "http://10.166.10.214:10086/creepers/api/touristBlackList/query";
        String baseUrl = "http://creepers.mis.liangfuzhengxin.com/creepers/api/touristBlackList/query";
        // String baseUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(baseUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // @Test
    // 旅行社黑名单测试
    public void testTouristAgentcyBlackList() {
        String type = "1", guideNo = "L-XZ00036", agentName = "海南航旅旅行社有限公司";
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", type);
        params.put("guideNo", guideNo);
        params.put("agentName", agentName);
        params.put("bizUk", guideNo);
        params.put("requestCode", "creepers_tourAgency_blacklist");  // 调用的接口
        params.put("invokerName", "creepers");   // 业务名称
        params.put("systemName", "project-creepers");   // 系统名称
        String jsonString = JSON.toJSONString(params);
        // 旅行社导游黑名单
        // String baseUrl =
        // "http://localhost:8080/creepers/api/touristAgencyBlackList/query";
        // String baseUrl =
        // "http://10.166.10.214:10086/creepers/api/touristAgencyBlackList/query";
        String baseUrl = "http://10.166.0.246:10086/creepers/api/touristAgencyBlackList/query";
        // String baseUrl =
        // "http://creepers.mis.liangfuzhengxin.com/creepers/api/touristAgencyBlackList/query";
        // 导游证查询
        // String baseUrl = "http://10.166.10.216:10080/diplomat/api/services";
        JsonResult<Map> jsonResult = restTemplate.postForObject(baseUrl, JSON.parse(jsonString), JsonResult.class);
        System.err.println("\njsonResult:====================>>>>" + jsonResult);
        Map msg = jsonResult.getData();
        System.err.println("\njsonResult.getData():====================>>>>" + msg);
    }

    // @Test
    public void testCreepers() {
        String baseUrl = "http://10.166.10.214:10084/diplomat/api/service";
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
        requestMap.add("bizUk", "深圳市裕同包装科技股份有限公司");
        requestMap.add("requestCode", "creepers_patent");
        requestMap.add("invokerName", "test");
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(requestMap).build().toUriString();
        JsonResult<?> jsonResult = restTemplate.getForObject(url, JsonResult.class, new Object[0]);
        logger.debug("-------------------jsonResult-------------------:" + jsonResult.toString());
    }

    @SuppressWarnings("unused")
    private String objectToString(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, obj);
        return writer.toString();
    }

    @SuppressWarnings("unused")
    private String jsonRead(String fullFileName) {
        File file = new File(fullFileName);
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
    }

    @SuppressWarnings("unused")
    private String ReadFile(String Path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}
