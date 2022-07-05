package com.primeton.manageProvider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.Role;
import com.primeton.commom.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    //根据区域等级 条件查询用户信息查询（以及其对应的区域信息）
    List<User> findUsersInArea(Integer areaLevel, String userName, String account, String areaId, Date beginDate, Date endDate);

    //根据用户ID 条件查询用户信息查询（以及其对应的区域信息）
    User findAreaUserById(String userId);
    //根据用户ID 条件查询用户信息查询（以及其对应的方舱信息）
    User findSheltersUserById(String userId);

    //登入
    User findByAccount(String account);

    //根据方舱ID 条件查询用户信息查询（以及其对应的方舱信息）
    List<User> findSheltersUser(String userName, String account, String sheltersId, Date beginDate, Date endDate);

    //根据用户ID 查询用户（包括角色信息）
    User findUserRoleById(String userId);

    //根据userId得到管辖的资源list
    List<String> findUserResource(String userId);

    //根据角色ID 查询角色（包括其对应的负责的菜单信息）
    Role selectRoleById(String roleId);
}
