package org.lwt.service;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.lwt.entity.TaskVO;
import org.lwt.vo.ProcessVO;

public interface VacationService {
    /**
     * p 启动请假流程
     */
    Map<String, Object> startProcess();
    /**
     * param 完成任务
     * @param userId
     */
    String complete(String taskId, String userId, Map<String, Object> vars);
    /**
     * 
     * @param userId 查看登陆用户的所有申请
     * @return
     */
    List<ProcessVO> listVacation(String userId);
    
    /**
     * 
     * @param userId
     * @return 查看属于登陆用户的所有任务
     */
    List<TaskVO> listTasks(String userId);
    /**
     * 
     * @param userId
     * @return 查询办理人任务列表
     */
    List<TaskVO> listAssigneeTasks(String userId);
    /**
     *  领取任务
     * @param taskId
     * @param userId
     */
    void claim(String taskId, String userId);
    /**
     * 获取前一个任务所提交的表单数据
     * @param taskId
     * @return
     */
    Map<String, Object> handleTask(String taskId);
    /**
     * 完成指定的任务
     * @param taskId
     * @param vars
     * @param userid
     */
    void complete(String taskId, Map<String, Object> vars, String userid);
    /**
     *  获取流程图的输入流
     * @param processInstanceId
     * @return
     */
    InputStream getDiagram(String processInstanceId);
    
    String completeOther(String taskId, Map<String, Object> vars, String userid);
}
