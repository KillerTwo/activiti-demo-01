package org.lwt.service;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.lwt.vo.UserVO;

public interface UserService {
    
    public User loginValidate(UserVO userForm);
    
    public Group getGroup(String userId);
}
