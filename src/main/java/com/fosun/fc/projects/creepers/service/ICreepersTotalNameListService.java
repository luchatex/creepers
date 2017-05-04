package com.fosun.fc.projects.creepers.service;

import java.math.BigDecimal;

public interface ICreepersTotalNameListService extends BaseService {

    public void saveAndUpdateTag(String name, BigDecimal businessTag);
}
