package org.lwt.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ActivitiController {

    @Autowired
    private ProcessEngine processEngine;
    
    @Autowired
    private RepositoryService rs;

    
    
    /*@Autowired
    private ProcessDefinition processDef;*/
    
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public String homePage() {
        System.out.println("进入Controller");
        System.err.println(processEngine.getName());
        
        // System.err.println(processEngine.getRepositoryService().createProcessDefinitionQuery().);
        System.err.println("rs is : "+rs);
        /*System.err.println("def: "+ processDef);
        System.err.println("def processDefinition key: "+ processDef.getKey());
        System.err.println("def deploymentId : "+ processDef.getDeploymentId());*/
        
        ProcessDefinition processDef = rs.createProcessDefinitionQuery().singleResult();
        System.err.println("processDef: "+processDef);
        System.err.println("processDef getKey() : "+processDef.getKey());
        System.err.println("processDef getDeploymentId() : "+processDef.getDeploymentId());
        return "home";
    }
    
    @RequestMapping(value="/views", method=RequestMethod.GET)
    public String demo() {
        System.out.println("进入Controller demo");
        return "demo";
    }
    
    @RequestMapping(value="/see", method=RequestMethod.GET)
    public String sees() {
        System.out.println("进入Controller sees");
        return "sees";
    }
    
    @RequestMapping(value="/fast", method=RequestMethod.GET)
    @ResponseBody
    public String fast() {
        System.out.println("进入Controller sees");
        return "sees";
    }
    
    
}
