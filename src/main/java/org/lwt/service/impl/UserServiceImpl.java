package org.lwt.service.impl;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

import org.lwt.service.UserService;
import org.lwt.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private IdentityService identityService;
    
    // 验证用户
    public User loginValidate(UserVO userForm) {
        // 根据用户的名称查询用户
        User user = identityService.createUserQuery()
                .userLastName(userForm.getLastName()).singleResult();
        if (user == null)
            return null;
        // 验证密码
        if (userForm.getPasswd().equals(user.getPassword())) {
            return user;
        }
        return null;
    }
    
    public Group getGroup(String userId) {
        return this.identityService.createGroupQuery().groupMember(userId)
                .singleResult();
    }
}
