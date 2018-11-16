package org.lwt.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyTaskLitener implements TaskListener {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.err.println("监听器执行");
        String taskId = delegateTask.getId(); 
        System.err.println("监听器中获取的taskId是 ："+ taskId);
        System.err.println("监听器中获取的taskService是: "+ taskService);
        String pass = (String) taskService.getVariable(taskId, "pass");
        ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(delegateTask.getProcessInstanceId())
                .singleResult();
        String isBack = (String) runtimeService.getVariable(pi.getId(), "isBack");  // 回退标志
        System.err.println("监听器监听到不是回退。");
        System.err.println("监听器中获取的回退标志isBack是 ："+ isBack);
        if(!pass.equals("同意")) {
            System.err.println("监听器监听到回退。");
            // 不同意则设置回退标志
            runtimeService.setVariable(pi.getId(), "isBack", taskId);                   // 将回退标志设置为当前任务id，表示是由该任务回退的。
        }
        
        
    }

}
