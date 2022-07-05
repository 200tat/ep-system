package com.primeton.manageProvider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.pojo.User;

import java.util.Date;
import java.util.List;

public interface UserService extends IService<User> {
    PageInfo<User> findUsersInArea(Integer pageIndex,Integer pageSize,Integer areaLevel, String userName, String account, String areaId, Date beginDate, Date endDate);

    User findUserById(String userId);

    //插入用户以及调价对应的负责区域
    void insertUser(User user, String areaId);

    //主键更新患者，以及其在区域的关联
    void updateUser(User user, List<String> areaIdList);

    User findByAccount(String account);

    PageInfo<User> findSheltersUser(Integer pageIndex, Integer pageSize,String userName, String account, String sheltersId, Date beginDate, Date endDate);

    void insertSheltersUser(String sheltersId, User user);

    void updateSheltersUser(List<String> sheltersId, String roleId, User user);

    User findUserRoleById(String userId);

    PageInfo<User> findUserByRoleId(Integer pageIndex, Integer pageSize, String roleId);

    void deleteUser(String userId);

}
