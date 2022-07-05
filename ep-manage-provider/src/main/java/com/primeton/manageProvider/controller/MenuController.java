package com.primeton.manageProvider.controller;

import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Menu;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Api(tags = "菜单")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 菜单管理中查询菜单以及子菜单
     * @param menuId
     * @return
     */
    @ApiOperation("查询菜单以及其子菜单")
    @PostMapping(value = "child/{menuId}")
    public ResultVo insertMenu(@ApiParam("菜单信息") @RequestParam String menuId){
        Menu menu = menuService.findMenu(menuId);
        return new ResultVo(menu);
    }

    /**
     * 菜单管理中添加菜单
     * @param menu
     * @return
     */
    @ApiOperation("添加菜单")
    @PostMapping(value = "menu")
    public ResultVo insertMenu(@ApiParam("菜单信息") @RequestBody Menu menu){
        if (menu == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        menuService.insertMenu(menu);
        return new ResultVo();
    }

    /**
     * 删除菜单
     * @param menuId
     * @return
     */
    @ApiOperation("删除菜单")
    @DeleteMapping(value = "{menuId}")
    public ResultVo deleteMenu(@ApiParam("菜单id") @PathVariable(value = "menuId") String menuId){
        Integer result = menuService.getBaseMapper().deleteById(menuId);
        if (result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return new ResultVo();
    }

    /**
     * 菜单管理中更改菜单
     * @param menu
     * @return
     */
    @ApiOperation("更改菜单")
    @PutMapping(value = "menuId")
    public ResultVo updateMenu(@ApiParam("更新菜单信息") @RequestBody Menu menu){
        if (menu == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        menuService.updateByMenuId(menu);
        return new ResultVo();
    }

    /**
     * 菜单管理中查看所有菜单
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation("查看所有菜单")
    @GetMapping(value = "all")
    public ResultVo getMenu(@RequestParam(value = "pageIndex",defaultValue = "1",required = false)Integer pageIndex,
                            @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize,
                            @RequestParam(value = "needPage") boolean needPage,
                            @ApiParam("菜单名") @RequestParam(value = "menuName",required = false)String menuName){
        PageInfo<Menu> pageInfo = menuService.getMenu(pageIndex,pageSize,menuName,needPage);
        return new ResultVo(pageInfo);
    }

    /**
     * 菜单管理中分级查看菜单
     * @param menuType 菜单等级
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation("分级查看菜单")
    @GetMapping(value = "level/{menuType}")
    public ResultVo getLevelMenu(@ApiParam("菜单等级") @PathVariable(value = "menuType")String menuType,
                                 @RequestParam(value = "pageIndex",defaultValue = "1",required = false)Integer pageIndex,
                                 @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize,
                                 @RequestParam(value = "needPage") boolean needPage
                                 ){
        if ("".equals(menuType)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        PageInfo<Menu> menuList = menuService.getLevelMenu(menuType,pageIndex,pageSize,needPage);

        return new ResultVo(menuList);

    }


    /**
     * 根据角色Id查询不同角色所拥有的菜单
     * @param roleId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation("查询不同角色所拥有的菜单")
    @GetMapping(value = "role/{roleId}")
    public ResultVo getRoleMenu(@ApiParam("角色id") @PathVariable(value = "roleId")String roleId,
                                @RequestParam(value = "pageIndex",defaultValue = "1",required = false)Integer pageIndex,
                                @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize,
                                @RequestParam("needPage") boolean needPage){
        if ("".equals(roleId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        PageInfo<Menu> pageInfo = menuService.getRoleMenu(roleId,pageIndex,pageSize,needPage);
        return new ResultVo(pageInfo);
    }

    /**
     * 根据角色Id分配权限
     * @param roleId
     * @param menuIdList
     * @return
     */
    @ApiOperation("分配权限")
    @PostMapping(value = "permission/{roleId}")
    public ResultVo addPermission(@PathVariable(value = "roleId")String roleId,
                                  @RequestParam List<String> menuIdList){
        if ("".equals(roleId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        if (menuIdList.size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        /*for (String menu:menuIdList
             ) {
            menuService.addPermissionRole(roleId,menu);
        }*/
        menuService.addPermissionRole(roleId, menuIdList);
        return new ResultVo();
    }

    /**
     * 根据角色Id和菜单Id取消权限
     * @param roleId
     * @param menuId
     * @return
     */
    @ApiOperation("取消赋权")
    @DeleteMapping(value = "roleId/{roleId}")
    public ResultVo deletePermission(@PathVariable(value = "roleId")String roleId,
                                     @RequestParam String menuId){
        if ("".equals(roleId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        menuService.deletePermission(roleId,menuId);
        return new ResultVo();
    }




}
