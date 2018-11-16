package org.lwt.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("processListener")
public class ProcessListener2 {
    public void notify(DelegateExecution execution) {
        System.err.println("流程监听器ProcessListener2执行： "+execution.getProcessInstanceId());
    }
}
