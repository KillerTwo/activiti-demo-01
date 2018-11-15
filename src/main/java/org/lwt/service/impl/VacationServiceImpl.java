package org.lwt.service.impl;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.lwt.dao.VacationRepository;
import org.lwt.entity.TaskVO;
import org.lwt.entity.Vacation;
import org.lwt.form.VacationForm;
import org.lwt.service.VacationService;
import org.lwt.vo.ProcessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Service;


@Service
public class VacationServiceImpl implements VacationService {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private FormService formService;
    /*@Autowired
    ProcessDefinition processDefinition;*/

    @Autowired
    VacationRepository vacationRepository;
    
    /**
     * @param 启动流程
     * return ProcessInstance id(返回启动流程后对应的流程实例id)
     */
    @Override
    public Map<String, Object> startProcess() {
        Map<String, Object> map = new HashMap<>();
        // 获取请假流程对应的流程定义
        /*ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("Vacation").singleResult();*/
        // 启动请假流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Vacation");
        System.err.println("申请名称： "+ processInstance.getName());
        // 将processInstance
        // id返回到前端，当用户填写完申请的点击提交后，前端带着用户填写的数据和processInstanceId请求后端，完成对应的用户任务。
        System.err.println("流程启动成功。");
     // 查询第一个任务
       
        Task firstTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId()).singleResult();
        // 设置任务受理人
        // taskService.setAssignee(firstTask.getId(), vacation.getUserId());
        // 记录请假数据
        // saveVacation(vacation, pi.getId());
        
        System.err.println("启动流程时taskId 是："+ firstTask.getId());
        FormData formData = formService.getTaskFormData(firstTask.getId());
        List<FormProperty> props = formData.getFormProperties();
        
        List<VacationForm> formList = new ArrayList<>();
        for (FormProperty formProperty : props) {
            VacationForm vacationForm = new VacationForm();
            vacationForm.setKey(formProperty.getId());
            vacationForm.setTitle(formProperty.getName());
            vacationForm.setValue(formProperty.getValue());
            formList.add(vacationForm);
        }
        
        map.put("processInstanceId", processInstance.getId());
        map.put("formData", formList);
        map.put("taskId", firstTask.getId());
        return map;
    }

    /**
     * return 完成请假申请业务任务，
     * @throws ParseException 
     */
    @Override
    public String complete(String taskId, String userId, Map<String, Object> vars){
        ProcessInstance processInstance = getProcessInstance(taskId);
        System.err.println("完成任务时的taskId 是："+ taskId);
        Task task = taskService.createTaskQuery()
                .taskId(taskId).singleResult();
        
        String taskName = task.getName();
        
        taskService.setAssignee(taskId, userId);
        taskService.complete(taskId, vars);
        // 完成任务之后将对应的申请信息存入数据库中
        saveVacation(vars, processInstance);
        
        return taskName;
    }
    /**
     *  1 创建Vacation对象，将申请信息存入数据库中
     * @param vars
     * @param processInstance
     */
    public void saveVacation(Map<String, Object> vars,ProcessInstance processInstance) {
        Vacation vc = new Vacation();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            vc.setBeginDate(formatter.parse((String) vars.get("startDate")));
            vc.setEndDate(formatter.parse((String)vars.get("endDate")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        vc.setDays(Integer.parseInt((String) vars.get("days")));
        
        vc.setProcessInstanceId(processInstance.getId());
        vc.setReason((String)vars.get("reason"));
        vc.setUserId((String)vars.get("userId"));
        vacationRepository.save(vc);
    }


    private ProcessInstance getProcessInstance(String taskId) {
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 根据任务查询流程实例
        ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        return pi;
    }

    // 创建用户组及其用户
    /*private void createGroup(IdentityService identityService, String groupId, String groupName, String groupType,
            String userId, String userName, String passwd) {
        // 用户组
        Group g1 = identityService.newGroup(groupId);
        g1.setName(groupName);
        g1.setType(groupType);
        identityService.saveGroup(g1);
        // 用户
        User u = identityService.newUser(userId);
        u.setLastName(userName);
        u.setPassword(passwd);
        identityService.saveUser(u);
        identityService.setUserInfo(u.getId(), "age", String.valueOf(30));
        // 绑定关系
        identityService.createMembership(u.getId(), g1.getId());
    }*/
    
    
    public List<ProcessVO> listVacation(String userId) {
        // 查询OA_VACATION表的数据
        List<Vacation> vacs = vacationRepository.findByUserId(userId);
       
        List<ProcessVO> result = new ArrayList<ProcessVO>();
        for (Vacation vac : vacs) {
            System.err.println("vac.getUserId(): "+vac.getUserId());
            // 查询流程实例
            ProcessInstance pi = this.runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(vac.getProcessInstanceId())
                    .singleResult();
            if (pi != null) {
                // 查询流程参数
                String startTime = (String) runtimeService.getVariable(pi.getId(), "startDate");
                System.err.println("listVacation开始时间是： "+startTime);
                // 封装界面对象
                ProcessVO vo = new ProcessVO();
                vo.setTitle("请假申请");
                vo.setRequestDate(startTime);
                vo.setId(pi.getId());
                result.add(vo);
            }
        }
        return result;
    }
    
    // 查询用户的待办任务
    public List<TaskVO> listTasks(String userId) {
        // 查询用户所属的用户组
        Group group = identityService.createGroupQuery()
                .groupMember(userId).singleResult();
        System.err.println("group id is : "+group.getId());
        // 根据用户组查询任务
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup(group.getId()).list();
        System.err.println("任务数量："+tasks.size());
        return createTaskVOList(tasks);
    }
    
    // 查询用户所受理的全部任务
    public List<TaskVO> listAssigneeTasks(String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId).list();
        // 将Task集合转为TaskVO集合
        return createTaskVOList(tasks);
    }

    // 将Task集合转为TaskVO集合
    private List<TaskVO> createTaskVOList(List<Task> tasks) {
        List<TaskVO> result = new ArrayList<TaskVO>();
        for (Task task : tasks) {
            System.err.println("任务名称是： "+ task.getName());
            // 查询流程实例
            ProcessInstance pi = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            
            // 查询流程参数
            String startTime = (String) runtimeService.getVariable(pi.getId(), "startDate");
            String userName = (String) runtimeService.getVariable(pi.getId(), "userName");
            System.err.println("任务的开始时间start time： " + startTime);
            System.err.println("任务的开始时间start time： " + userName);
            // 封装值对象
            TaskVO vo = new TaskVO();
            // vo.setProcessInstanceId(task.getProcessInstanceId());
            vo.setRequestDate(startTime);
            vo.setRequestUser(userName);
            vo.setTitle(userName+"的请假申请");
            vo.setTaskId(task.getId());
            vo.setProcessInstanceId(pi.getId());
            System.err.println("任务详情createTaskVOList： "+ vo);
            /*runtimeService.deleteProcessInstance(pi.getId(), "过时的流程");
            taskService.deleteTask(task.getId());*/
            result.add(vo);
        }
        return result;
    }
    
    // 领取任务,
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }
    // 办理任务页面
    public Map<String, Object> handleTask(String taskId) {
        ProcessInstance processInstance = getProcessInstance(taskId);

        Map<String, Object> vars = new HashMap<>();
        
        // String isPass = (String) taskService.getVariable(taskId, "ispass");
        // 获取当前任务自己的表单属性
        // taskService.getIdentityLinksForTask(taskId);
        List<VacationForm> vacations = getFormItems(processInstance);
        
        FormData formData = formService.getTaskFormData(taskId);
        List<FormProperty> props = formData.getFormProperties();
        for (FormProperty formProperty : props) {
            
            VacationForm vacationForm = new VacationForm();
            vacationForm.setTitle(formProperty.getName());
            vacationForm.setKey(formProperty.getId());
            vacationForm.setValue(formProperty.getValue());
            vacationForm.setDisabled(false);
            vacations.add(vacationForm);
            // System.err.println("办理任务页面表单属性： "+ formProperty.getName());
        }
        vars.put("forms", vacations);
        
        return vars;
    }
    /**
     *  获取指定的参数
     * @param processInstance
     * @return
     */
    public List<VacationForm> getFormItems(ProcessInstance processInstance) {
        List<VacationForm> vacations = new ArrayList<>();
        
        String startDate = (String) runtimeService.getVariable(processInstance.getId(), "startDate");
        VacationForm vacationForm = new VacationForm();
        vacationForm.setTitle("开始时间");
        vacationForm.setKey("startDate");
        vacationForm.setValue(startDate);
        vacationForm.setDisabled(true);
        vacations.add(vacationForm);
        
        String endDate = (String) runtimeService.getVariable(processInstance.getId(), "endDate");
        VacationForm vacationForm1 = new VacationForm();
        vacationForm1.setTitle("结束时间");
        vacationForm1.setKey("endDate");
        vacationForm1.setValue(endDate);
        vacationForm1.setDisabled(true);
        vacations.add(vacationForm1);
        
        String userName = (String) runtimeService.getVariable(processInstance.getId(), "userName");
        VacationForm vacationForm2 = new VacationForm();
        vacationForm2.setTitle("申请人姓名");
        vacationForm2.setKey("userName");
        vacationForm2.setValue(userName);
        vacationForm2.setDisabled(true);
        vacations.add(vacationForm2);
        
        String days = (String) runtimeService.getVariable(processInstance.getId(), "days");
        VacationForm vacationForm3 = new VacationForm();
        vacationForm3.setTitle("请假天数");
        vacationForm3.setKey("days");
        vacationForm3.setValue(days);
        vacationForm3.setDisabled(true);
        vacations.add(vacationForm3);
        
        String reason = (String) runtimeService.getVariable(processInstance.getId(), "reason");
        VacationForm vacationForm4 = new VacationForm();
        vacationForm4.setTitle("请假原因");
        vacationForm4.setKey("reason");
        vacationForm4.setValue(reason);
        vacationForm4.setDisabled(true);
        vacations.add(vacationForm4);
        
        return vacations;
    }
    
    // 审批通过任务
    public String completeOther(String taskId, Map<String, Object> vars, String userid) {
        
        ProcessInstance pi = getProcessInstance(taskId);
        FormData formData = formService.getTaskFormData(taskId);
        List<FormProperty> props = formData.getFormProperties();
        String taskName = taskService.createTaskQuery().taskId(taskId).singleResult().getName();
        // this.identityService.setAuthenticatedUserId(userid);
        // 添加评论
        // this.taskService.addComment(taskId, pi.getId(), content);
        // 完成任务
        if(!vars.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            for (FormProperty formProperty : props) {
                String key = formProperty.getId();
                map.put(key, vars.get(key));
            }
            taskService.complete(taskId, map);
        }
        else {
            taskService.complete(taskId);
        }
        return taskName;
    }
    // 显示流程图
    public InputStream getDiagram(String processInstanceId) {
        // 查询流程实例
        ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        // 查询流程定义
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(pi.getProcessDefinitionId()).singleResult();
        // 获取BPMN模型对象
        BpmnModel model = repositoryService.getBpmnModel(pd.getId());
        // 定义使用宋体
        String fontName = "宋体";
        // 获取流程实实例当前点的节点，需要高亮显示
        List<String> currentActs = runtimeService.getActiveActivityIds(pi.getId());
        // BPMN模型对象、图片类型、显示的节点
        InputStream is = this.processEngine
                .getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(model, "png", currentActs, new ArrayList<String>(), 
                fontName, fontName, fontName,null, 1.0);
        return is;
    }

    @Override
    public void complete(String taskId, Map<String, Object> vars, String userid) {
        // TODO Auto-generated method stub
        
    }
}
