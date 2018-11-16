package org.lwt.listener;

import java.beans.ExceptionListener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineLifecycleListener;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class ProcessListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        
        System.err.println("流程监听器ProcessListener执行ProcessInstanceId是 ： "+execution.getProcessInstanceId());
    }

}
