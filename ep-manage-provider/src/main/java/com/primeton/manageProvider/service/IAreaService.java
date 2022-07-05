package com.primeton.manageProvider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.pojo.Area;

import java.util.List;

public interface IAreaService extends IService<Area> {
    Area findParentArea(String areaId);

    Integer insertArea(Area area);

    List getAreaAll();

    Integer updateArea(Area area);

    PageInfo<Area> getLevelArea(Integer areaLevel, Integer pageIndex, Integer pageSize, boolean needPage);

    List<Area> findChildAreaListByIdList(List<String> list);

    void updateAreaStatus();

    List<Area> findAllArea();

    List<String> getBuildingByAreaId(String areaId);

    PageInfo<Area> findChildrenList(Integer pageIndex, Integer pageSize, String parentId, boolean needPage);
}
