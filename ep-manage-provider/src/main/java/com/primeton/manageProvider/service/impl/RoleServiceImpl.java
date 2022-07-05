package com.primeton.manageProvider.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.primeton.commom.pojo.Role;

import com.primeton.manageProvider.mapper.RoleMapper;
import com.primeton.manageProvider.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Autowired
    private RoleMapper roleMapper;


    @Override
    @Transactional(readOnly = true)
    public Role findRoleWithMenuByRoleId(String roleId) {
        return roleMapper.findRoleWithMenuByRoleId(roleId);
    }
}
