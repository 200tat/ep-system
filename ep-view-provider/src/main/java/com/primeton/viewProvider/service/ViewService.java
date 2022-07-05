package com.primeton.viewProvider.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.primeton.commom.pojo.View;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ViewService extends IService<View> {
    public void updateCommunityViewDaily();

    Map<Date, Integer> getTransferCount(String areaId,Integer day);


    Map<String, Object> getInDeCuCount(Integer day);

    List<View> selectTodayViews();

    /**
     * 更据区域ID 获取 所有小区或者上榜小区
     * @param areaId
     * @param outstanding
     * @param date
     * @return
     */
    List<View> getAreaEpidemic(String areaId, boolean outstanding, Date date);

    Map<Date, Integer> getStayCount(String areaId, Integer day);

    List<String> getChildrenId(String areaId);

    Map<String, Object> getAreaInfo(String areaId, Integer day);
}
