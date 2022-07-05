package com.primeton.manageProvider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.pojo.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    PageInfo<Menu> getMenu(Integer pageIndex, Integer pageSize, String menuName,boolean needPage);

    void insertMenu(Menu menu);

    void updateByMenuId(Menu menu);

    PageInfo<Menu> getLevelMenu(String menuLevel,Integer pageIndex,Integer pageSize,Boolean needPage);

    PageInfo<Menu> getRoleMenu(String roleId, Integer pageIndex, Integer pageSize,boolean needPage);

    void addPermissionRole(String roleId, List<String> menuIdList);

    void deletePermission(String roleId, String menuId);

    Menu findMenu(String menuId);
}
