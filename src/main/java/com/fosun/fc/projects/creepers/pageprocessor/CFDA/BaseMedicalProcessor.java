package com.fosun.fc.projects.creepers.pageprocessor.CFDA;

import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

public class BaseMedicalProcessor {

    @Autowired
    protected ICreepersTaskListService creepersTaskListServiceImpl;
}
