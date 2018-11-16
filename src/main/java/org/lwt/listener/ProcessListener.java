package org.lwt.listener;

import java.beans.ExceptionListener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineLifecycleListener;

public class ProcessListener implements ProcessEngineLifecycleListener {

    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {
        // TODO Auto-generated method stub

    }

}
