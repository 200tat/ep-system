package com.primeton.manageProvider.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.primeton.commom.pojo.Role;

public interface RoleService extends IService<Role> {
    Role findRoleWithMenuByRoleId(String roleId);

}
