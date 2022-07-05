package com.primeton.manageProvider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.Area;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {
    Area findParent(String areaId);

    //添加一个通过区域Id查询子区域信息的方法，用于PatientServiceImpl层调用
    Area findPatientWithId(String areaId);

    List<Area> findChildAreaListByIdList(List<String> list);

    List<Area> findPatientWithAreaId(String areaId);

    List<Area> getLevelArea(Integer areaLevel);

    List<Area> findChildArea(String parentId);

    List<Area> findAllArea();

    List<String> getBuildingByAreaId(String areaId);


    List<Area> getAreaByUserId(String userId);
}
