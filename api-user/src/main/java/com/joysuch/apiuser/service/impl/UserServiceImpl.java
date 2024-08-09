package com.joysuch.apiuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joysuch.apiuser.domain.User;
import com.joysuch.apiuser.mapper.UserMapper;
import com.joysuch.apiuser.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-08-09 19:44:17
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




