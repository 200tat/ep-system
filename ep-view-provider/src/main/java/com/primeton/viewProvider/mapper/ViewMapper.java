package com.primeton.viewProvider.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.View;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper
public interface ViewMapper extends BaseMapper<View> {
    /**
     * 根据区域ID 和 几天前时间来 获取 view 数据
     * @param areaId
     * @param date
     * @return
     */
    List<View> findAreaEpidemicByAreaId(String areaId, Date date);

    List<View> findByAreaLevel(Integer areaLevel);

    //通过areaId查询该区域当天的view信息
    List<View> selectView(List<String> areaIdList,Integer day);

    //通过areaId查询该区域下的所有子区域areaId
    Set<String> selectChildId(String areaId);

    List<View> getInfoByAreaId(String areaId,Integer day);

    /**
     * 根据父区域的ID 获取子区域的集合
     * @param areaId
     * @param outstanding
     * @param date
     * @return
     */
    List<View> findAreaByParentId(String areaId,boolean outstanding, Date date);

    /**
     * 获取直接子区域idList
     * @param areaId 父id
     * @return
     */
    List<String> getChildrenId(String areaId);
}
