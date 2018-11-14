/*package org.lwt.init;

import java.util.UUID;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

*//**
 * @param 在所有 Spring Beans 都初始化之后，SpringApplication.run() 之前执行做一些初始化操纵
 * @author Administrator
 *
 *//*
@Component
public class UserInitializer implements CommandLineRunner {
    
    @Autowired
    private IdentityService identityService;
    
    @Override
    public void run(String... args) throws Exception {
        System.err.println("执行初始化操作");
        // 初始化用户和用户组
        // 初始化一次
        // initGroupsAndUsers(identityService);

    }
    
    
 // 创建用户组及其用户
    private void createGroup(IdentityService identityService, String groupId,
            String groupName, String groupType, String userId, String userName,
            String passwd) {
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
    }

    // 初始化用户组
    private void initGroupsAndUsers(IdentityService identityService) {
        // 用户组
        createGroup(identityService, "employee", "员工组", "employee", UUID
                .randomUUID().toString(), "员工甲", "123456");
        createGroup(identityService, "manager", "经理组", "manager", UUID
                .randomUUID().toString(), "经理甲 ", "123456");
        createGroup(identityService, "director", "总监组", "director", UUID
                .randomUUID().toString(), "总监甲 ", "123456");
        createGroup(identityService, "hr", "人事组", "hr", UUID.randomUUID()
                .toString(), "人事甲 ", "123456");
        createGroup(identityService, "boss", "老板组", "boss", UUID.randomUUID()
                .toString(), "老板甲 ", "123456");
        createGroup(identityService, "finance", "财务组", "finance", UUID
                .randomUUID().toString(), "财务甲 ", "123456");
    }

}
*/