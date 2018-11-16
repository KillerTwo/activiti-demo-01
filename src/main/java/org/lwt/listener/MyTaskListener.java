package org.lwt.listener;

import java.io.Serializable;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 在某个任务节点审核不通过时，设置“回退”标志流程参数和“不通过原因”流程参数
 * @author Administrator
 *
 */
@Component("myTaskListener")
public class MyTaskListener implements Serializable {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    public void taskBack(DelegateTask task) {
        System.err.println("监听器执行");
        String taskId = task.getId(); 
        
        /*System.err.println("监听器中获取的taskId是 ："+ taskId);
        System.err.println("监听器中获取的taskService是: "+ taskService);*/
        
        String pass = (String) taskService.getVariable(taskId, "pass");
        String noPassReason = (String) taskService.getVariable(taskId, "noPassReason");
        
        ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        //String isBack = (String) runtimeService.getVariable(pi.getId(), "isBack");  // 回退标志
        /*System.err.println("监听器监听到不是回退。");
        System.err.println("监听器中获取的回退标志isBack是 ："+ isBack);*/
        if(!pass.equals("同意")) {
            // System.err.println("监听器监听到回退。");
            // 设置不同过原因（流程参数）
            runtimeService.setVariable(pi.getId(), "noPassReason", noPassReason);
            // 不同意则设置回退标志（流程参数）
            runtimeService.setVariable(pi.getId(), "isBack", true);                   // 将回退标志设置为当前任务id，表示是由该任务回退的。
        }else {
            runtimeService.setVariable(pi.getId(), "isBack", false);                   // 将回退标志设置为false，表示是由该任务不是回退任务。
        }
    }
}
