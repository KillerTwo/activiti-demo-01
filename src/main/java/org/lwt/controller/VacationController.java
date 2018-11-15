package org.lwt.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.lwt.dao.PersonRepository;
import org.lwt.dao.VacationRepository;
import org.lwt.entity.TaskVO;
import org.lwt.service.UserService;
import org.lwt.service.VacationService;
import org.lwt.vo.ProcessVO;
import org.lwt.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/vacation")
public class VacationController {

    @Autowired
    private VacationService vacationService;
    
    @Autowired
    PersonRepository personRepository;
    
    @Autowired
    VacationRepository vacationRepository;
    
    @Autowired
    private UserService userService;
    
    
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String vacation() {
        return "vacation";
    }
    /**
     * 
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public ModelAndView consumerStartProcess(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        System.err.println("进入启动流程");
        ModelAndView model = new ModelAndView();
        
        Map<String, Object> map = vacationService.startProcess(user.getId());
        
        model.setViewName("show_vacation");
        model.addObject("mapData", map);
        
        /*System.err.println("formData is : "+map.get("formData"));
        System.err.println("processInstanceId is: "+map.get("processInstanceId"));*/
        
        return model;
    }
    /**
     * param接收请求参数，填写完参数后完成对应的任务
     * @param map
     * @return
     */
    @RequestMapping(value = "/complateapply/{taskId}", method = RequestMethod.POST)
    public ModelAndView complateApply(@PathVariable String taskId,
            @RequestParam Map<String, Object> map, HttpSession session) {
        
        System.err.println("进入完成填写申请的流程。");
        System.err.println("username: "+map.get("userName"));
        System.err.println("cause: "+map.get("reason"));
        
        ModelAndView model = new ModelAndView();
        User user = (User) session.getAttribute("user");
        map.put("userId", user.getId());
        
        String taskName = vacationService.complete(taskId, user.getId(), map);
        
        System.err.println("任务名称是 ["+taskName+"]的任务已经被完成。");
        
        model.addObject("taskName", taskName);
        model.setViewName("complate");
        return model;
    }
    
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String loginPage() {
        return "login";
    }
    
    // 用户登录
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String login(UserVO userForm, HttpSession session) {
        System.err.println("真实姓名： "+ userForm.getLastName());
        
        User user = userService.loginValidate(userForm);
        if (user != null) {
            // 将用户放到session中
            session.setAttribute("user", user);
            
            // 将用户组放到session中
            Group group = userService.getGroup(user.getId());
            session.setAttribute("group", group);
            // 登陆成功
            return "vacation";
        } else {
//            this.loginMsg = "用户名或密码错误";
            return "login";
        }
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout(HttpSession session) {
        //System.err.println("真实姓名： "+ userForm.getLastName());
        
        session.invalidate();
        return "login";
    }
    
    
    /**
     * 1 查看登陆用户的所有申请，包括查看该流程的进度
     */
    @RequestMapping(value="/processins", method=RequestMethod.GET)
    public ModelAndView listProcessInstance(HttpSession session) {
        ModelAndView model = new ModelAndView();
        User user = (User) session.getAttribute("user");
        String userId = user.getId();
        List<ProcessVO> processVos = vacationService.listVacation(userId);
        model.addObject("procs", processVos);
        model.setViewName("process_list");
        return model;
    }
    /**
     * 2 查看用户拥有的所有任务列表,根据url中的type,如果是candidate则查看所有任务列表，如果是assignee则查看待办理的任务列表
     * @return
     */
    @RequestMapping(value="/tasks/{type}", method=RequestMethod.GET)
    public ModelAndView listTask(HttpSession session, @PathVariable String type) {
        ModelAndView model = new ModelAndView();
        User user = (User) session.getAttribute("user");
        String userId = user.getId();
        List<TaskVO> tasks = new ArrayList<>();
        if("assignee".equals(type)) {
            // 查看办理人任务列表
            tasks = vacationService.listAssigneeTasks(userId);
            model.setViewName("show_task_assignee");
        }else if("candidate".equals(type)) {
            // 如果是查看候选人任务列表
            tasks = vacationService.listTasks(userId);
            model.setViewName("show_task");
        }
        model.addObject("tasks", tasks);
        
        return model;
    }
    
    /**
     * 3 认领任务，将登陆的用户设置为指定任务的办理人
     */
    @RequestMapping(value="/claim/{taskId}")
    public String claim(@PathVariable String taskId, HttpSession session) {
        System.err.println("认领的任务id是：" + taskId);
        // ModelAndView model = new ModelAndView();
        User user = (User) session.getAttribute("user");
        String userId = user.getId();
        vacationService.claim(taskId, userId);
        return "redirect:/vacation/tasks/assignee";
    }
    
    // 打开办理页面
    @RequestMapping(value="/perform/{taskId}", method=RequestMethod.GET)
    public ModelAndView perform(@PathVariable String taskId) {
        ModelAndView model = new ModelAndView();
        Map<String, Object> map = vacationService.handleTask(taskId);
        model.addObject("vars", map);
        model.setViewName("show_handle_info");
        return model;
    }
    
    // 完成任务
    @RequestMapping(value="/complete/{taskId}", method=RequestMethod.POST)
    public ModelAndView complete(@RequestParam Map<String, Object> vars, @PathVariable String taskId, HttpSession session) {
        ModelAndView model = new ModelAndView();
        //Map<String, Object> vars = new HashMap<>();
        System.err.println("taskid id : "+ taskId);
        // System.err.println("ispass is : "+ vars.get("ispass"));
        
        for (Map.Entry<String, Object> entry : vars.entrySet()) { 
            System.err.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
        }
        
        model.setViewName("complate");
        User user = (User) session.getAttribute("user");
        String userId = user.getId();
        // 完成任务
        vacationService.complete(taskId, vars, userId);
        
        return model;
    }
    /**
     * 点击查看申请流程
     * @return
     */
    @RequestMapping(value="/show/{processInstanceId}")
    public ModelAndView show(@PathVariable String processInstanceId) {
        ModelAndView model = new ModelAndView();
        model.addObject("processInstanceId", processInstanceId);
        model.setViewName("process_img");
        return model;
    }
    
    // 显示流程图
    @RequestMapping(value="/diagram/{processInstanceId}")
    public String showDiagram(@PathVariable String processInstanceId, HttpServletResponse response) {
        OutputStream out = null;
        try {
            //HttpServletResponse response = ServletActionContext.getResponse();
            InputStream is = vacationService.getDiagram(processInstanceId);
            response.setContentType("multipart/form-data;charset=utf8");
            out = response.getOutputStream();
            out.write(getImgByte(is));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
    
    
    // 将输入流转换为byte数组
    private byte[] getImgByte(InputStream is) throws IOException {
        BufferedInputStream bufin = new BufferedInputStream(is);  
        int buffSize = 1024;  
        ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize); 
  
        byte[] temp = new byte[buffSize];  
        int size = 0;  
        while ((size = bufin.read(temp)) != -1) {  
            out.write(temp, 0, size);  
        }  
        bufin.close();  
        is.close();  
        byte[] content = out.toByteArray();  
        out.close();  
        return content; 
    }

}
