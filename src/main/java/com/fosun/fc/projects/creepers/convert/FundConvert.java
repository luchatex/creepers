package com.fosun.fc.projects.creepers.convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasic;
import com.fosun.fc.projects.creepers.entity.TCreepersFundBasicDetail;
import com.fosun.fc.projects.creepers.entity.TCreepersFundLoans;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

public class FundConvert {
    public static final Map<String, Object> convert(List<TCreepersFundBasic> entityBasicList,
            List<TCreepersFundBasicDetail> entityBasicDetailList, List<TCreepersFundLoans> entityLoansList)
                    throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        List<HashMap<String, Object>> accounts = new ArrayList<HashMap<String, Object>>();
        if (CommonMethodUtils.isEmpty(entityBasicList)) {
            result.put("accounts", accounts);
        } else {
            HashMap<String, Object> map = basicMap(entityBasicList.get(0));
            accounts.add(map);
            result.put("accounts", accounts);
        }

        List<HashMap<String, Object>> flows = new ArrayList<HashMap<String, Object>>();
        if (CommonMethodUtils.isEmpty(entityBasicDetailList)) {
            result.put("flows", flows);
        } else {
            for (TCreepersFundBasicDetail entity : entityBasicDetailList) {
                HashMap<String, Object> map = flowMap(entity);
                flows.add(map);
            }
            result.put("flows", flows);
        }

        List<HashMap<String, Object>> loans = new ArrayList<HashMap<String, Object>>();
        if (CommonMethodUtils.isEmpty(entityLoansList)) {
            result.put("loans", loans);
        } else {
            for (TCreepersFundLoans entity : entityLoansList) {
                HashMap<String, Object> map = loansMap(entity);
                loans.add(map);
            }
            result.put("loans", loans);
        }

        return result;

    }

    private static HashMap<String, Object> basicMap(TCreepersFundBasic entity) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (entity != null) {
            map.put("id", entity.getId());
            map.put("customer", "");
            map.put("name", entity.getName());
            map.put("idcard", "");
            map.put("balance", entity.getSumAmount().multiply(new BigDecimal(100)).intValue());
            map.put("last_record_date", entity.getEndDt().replace("年", "-").replace("月", "-"));
            map.put("status", entity.getCertificateStatus());
            map.put("company", entity.getUnit());
            map.put("deposit_amount", entity.getMonthlyAmount().multiply(new BigDecimal(100)).intValue());
            map.put("deposit_base", "");
            map.put("company_rate", "");
            map.put("person_rate", "");
        }
        return map;
    }

    private static HashMap<String, Object> flowMap(TCreepersFundBasicDetail entity) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (entity != null) {
            map.put("account_id", entity.getId());
            map.put("company", entity.getUnit());
            map.put("record_date", entity.getOperationDt().replace("年", "-").replace("月", "-"));
            if (entity.getOperationDesc().contains("汇缴")) {
                map.put("type", "汇缴");
                map.put("amount", entity.getAmount().multiply(new BigDecimal(100)).intValue());
            } else if (entity.getOperationDesc().contains("支取")) {
                map.put("type", "支取");
                map.put("amount", "-" + entity.getAmount().multiply(new BigDecimal(100)).intValue());
            } else if (entity.getOperationDesc().contains("结息")) {
                map.put("type", "结息");
                map.put("amount", entity.getAmount().multiply(new BigDecimal(100)).intValue());
            } else {
                map.put("type", "其他");
                map.put("amount", entity.getAmount().multiply(new BigDecimal(100)).intValue());
            }
            map.put("record_month", "");
            map.put("balance", "");
        }
        return map;
    }

    private static HashMap<String, Object> loansMap(TCreepersFundLoans entity) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (entity != null) {
            map.put("account_id", entity.getId());
            map.put("contract_no", "");
            map.put("name", "");
            map.put("idcard", "");
            map.put("phone", "");
            map.put("address", "");
            map.put("bank", "");
            map.put("status", entity.getLoanAccountStatus());
            map.put("limit", entity.getLoanAmount().replace(".", ""));
            map.put("balance", entity.getLeftAmount().replace(".", ""));
            map.put("period", entity.getPeriod());
            map.put("begin_date", CommonMethodUtils.dateFormat(entity.getLoanDt(),
                    BaseConstant.DateFormatPatternTypeArr.TYPE_ARRAY.getOne(0)));
            map.put("end_date", "");
            map.put("loan_rate", "");
            map.put("penalty_rate", "");
            map.put("refund", entity.getRepaymentMethod());
        }
        return map;
    }
}
