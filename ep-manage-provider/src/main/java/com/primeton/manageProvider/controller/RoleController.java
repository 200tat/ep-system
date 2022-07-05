package com.primeton.manageProvider.controller;


import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.pojo.Role;
import com.primeton.commom.pojo.User;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.RoleService;
import com.primeton.manageProvider.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("role")
@Api(tags = "角色")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    /**
     * 此方法是查询 角色 （角色对象中包含了菜单集合）
     * @param roleId
     */
    @ApiOperation("查询角色及权限")
    @GetMapping("permission/{roleId}")
    public ResultVo getPermissions(@PathVariable("roleId") String roleId){
        try{
            int id = Integer.parseInt(roleId);
            if(id < 0 || id > 5){
                throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
            }
        }catch (NumberFormatException e){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }

        Role role = roleService.findRoleWithMenuByRoleId(roleId);
        if(role == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(role);
        }
    }

    /**
     * 获取不同角色的分页列表
     * @param roleId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping(value = "{roleId}")
    @ApiOperation("获取不同角色的分页列表")
    public ResultVo findUserByAreaLevel(@PathVariable("roleId")String roleId,
                                        @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                        @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        try{
            int id = Integer.parseInt(roleId);
            if(id < 0 || id > 5){
                throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
            }
        }catch (NumberFormatException e){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }

        PageInfo<User> pageInfo = userService.findUserByRoleId(pageIndex,pageSize,roleId);
        if(pageInfo == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(pageInfo);
        }
    }
}
