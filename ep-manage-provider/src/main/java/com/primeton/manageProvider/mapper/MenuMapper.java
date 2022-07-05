package com.primeton.manageProvider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> getMenu(String menuName);

    List<Menu> getLevelMenu(String menuLevel);

    List<Menu> getRoleMenu(String roleId);


    List<Menu> getChildMenuByRoleAndParent(String parentId, String roleId);

    Menu findMenu(String menuId);
}
