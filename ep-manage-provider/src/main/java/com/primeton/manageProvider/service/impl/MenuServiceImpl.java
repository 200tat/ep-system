package com.primeton.manageProvider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Menu;
import com.primeton.commom.pojo.Permission;
import com.primeton.commom.vo.CodeMsg;

import com.primeton.manageProvider.mapper.PermissionMapper;
import com.primeton.manageProvider.mapper.MenuMapper;
import com.primeton.manageProvider.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper,Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 分页展示菜单列表
     * @param pageIndex
     * @param pageSize
     * @param menuName
     * @return
     */
    @Override
    public PageInfo<Menu> getMenu(Integer pageIndex, Integer pageSize, String menuName,boolean needPage) {
        if (needPage)
            PageHelper.startPage(pageIndex,pageSize);
        List<Menu> menuList = menuMapper.getMenu(menuName);
        if (menuList == null || menuList.isEmpty()){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new PageInfo<>(menuList);
    }

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    @Override
    public void insertMenu(Menu menu) {
        String menuId = UUID.randomUUID().toString().replace("-", "");
        menu.setMenuId(menuId);
        menu.setCreateTime(new Date());
        int insert = menuMapper.insert(menu);
        if (insert < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 更改菜单
     * @param menu
     * @return
     */
    @Override
    public void updateByMenuId(Menu menu) {
        int update = menuMapper.updateById(menu);
        if (update < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 获取不同等级菜单
     * @param menuLevel 菜单等级
     * @return
     */
    @Override
    public PageInfo<Menu> getLevelMenu(String menuLevel,Integer pageIndex,Integer pageSize,Boolean needPage) {
        if(needPage){
            PageHelper.startPage(pageIndex,pageSize);
        }
        List<Menu> menuList = menuMapper.getLevelMenu(menuLevel);
        if (menuList == null || menuList.isEmpty()){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new PageInfo<>(menuList);
    }

    /**
     * 根据角色Id查询不同角色所拥有的菜单
     * @param roleId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Menu> getRoleMenu(String roleId, Integer pageIndex, Integer pageSize,boolean needPage) {
        if (needPage)
            PageHelper.startPage(pageIndex,pageSize);
        List<Menu> menuList = menuMapper.getRoleMenu(roleId);
        if (menuList == null || menuList.isEmpty()){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        for (Menu parent: menuList) {
            parent = getMenuByParentAndRole(parent,roleId);
        }
        return new PageInfo<>(menuList);
    }

    /**
     * 查询旗下的所有有权限的菜单
     * @param menu
     * @param roleId
     * @return
     */
    public Menu getMenuByParentAndRole(Menu menu, String roleId){
        if (menu.getChildren() != null){
            List<Menu> children = menuMapper.getChildMenuByRoleAndParent(menu.getMenuId(), roleId);
            menu.setChildren(children);
            if (children != null || !children.isEmpty()){
                for (Menu m: children) {
                    m = getMenuByParentAndRole(m, roleId);
                }
            }
        }
        return menu;
    }

    /**
     * 根据角色Id添加可使用菜单
     * @param roleId
     * @param menuIdList
     * @return
     */
    @Override
    public void addPermissionRole(String roleId, List<String> menuIdList) {
        int insert = 0;
        for (String menuId: menuIdList) {
            String permissionId = UUID.randomUUID().toString().replace("-", "");
            Permission permission = new Permission(permissionId, roleId, menuId);
            insert += permissionMapper.insert(permission);
        }
        if (insert < menuIdList.size()){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    @Override
    public void deletePermission(String roleId, String menuId) {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId",roleId);
        map.put("menuId",menuId);
        int i = permissionMapper.deleteByMap(map);
        if(i < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    @Override
    public Menu findMenu(String menuId) {
        return menuMapper.findMenu(menuId);
    }


}
