package com.primeton.manageProvider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.pojo.Area;

import com.primeton.manageProvider.mapper.PatientMapper;
import com.primeton.manageProvider.service.IAreaService;
import com.primeton.manageProvider.mapper.AreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {


    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private PatientMapper patientMapper;

    /**
     * 查询 此ID 对应的区域，其中包括其对应的多级上级区域
     * @param areaId
     * @return
     */
    @Override
    public Area findParentArea(String areaId) {
        return areaMapper.findParent(areaId);
    }

    /**
     * 新增区域
     * @param area
     * @return
     */
    @Override
    public Integer insertArea(Area area) {
        Date date = new Date();
        area.setCreateTime(date);
        return areaMapper.insert(area);
    }

    /**
     * 获得所有区域及其子集
     * @return
     */
    @Override
    public List getAreaAll() {
        return areaMapper.findPatientWithAreaId("");
    }

    /**
     * 更改区域
     * @param area
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateArea(Area area) {
        int updateArea = areaMapper.updateById(area);
        return updateArea;
    }

    /**
     * 获取不同等级的区域
     * @param areaLevel
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Area> getLevelArea(Integer areaLevel, Integer pageIndex, Integer pageSize, boolean needPage) {
        if (needPage){
            PageHelper.startPage(pageIndex,pageSize);
        }
        List<Area> menuList = areaMapper.getLevelArea(areaLevel);
        return new PageInfo<>(menuList);
    }


    /**
     * 根据 区域ID集合 查询区域以及其子区域的信息
     * @param list ID集合
     * @return
     */
    @Override
    public List<Area> findChildAreaListByIdList(List<String> list) {
        return areaMapper.findChildAreaListByIdList(list);
    }


    /**
     * 定时更新区域的三区划分等级
     */
    @Override
    @Scheduled(cron = "30 27 12 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void updateAreaStatus() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(0);
        list2.add(-1);
        //查询 行政区集合， 对象中包括多级子区域的信息
        List<Area> regionAreaList = areaMapper.findChildArea("0");
        for (Area region: regionAreaList) {//行政区
            int regionStatus = 0;
            for (Area street: region.getChildren()) {//街道
                int status = 0;
                for (Area community: street.getChildren()) {//小区
                    //查询对应区域的所有患者
                    List<String> stringList = patientMapper.selectInfectedPatientByAreaId(list, 2, community.getAreaId());
                    stringList = stringList.size()>0 && stringList!=null ? stringList  : null;
                    //得出 患者阳性之后 未转运到方舱的人数
                    Integer seal = patientMapper.selectCountOfPatientInCenter(1, String.valueOf(0), false, stringList);
                    if (seal <= 0) {//排除为封控区，之后
                        //查询7日内转去方舱的人数
                        Integer toShelters = patientMapper.selectCountOfPatientInCenter(1, null, true, stringList);
                        //查询 7日内自行转阴或死亡的人数
                        Integer stayHome = patientMapper.selectCountOfPatientCured(list2, 2, "1301001");
                        if ((toShelters + stayHome)>0){
                            //若 7日内有 转去 方舱 或者 自行转阴或因疫情死亡的人，则为管控区
                            community.setAreaStatus(2);
                            areaMapper.updateById(community);
                        }else {
                            //7日内没有 转去方舱 且没有自转阴或死亡 并且没有阳性患者的情况下，为防范区
                            community.setAreaStatus(1);
                            areaMapper.updateById(community);
                        }
                    }else {//为封控区
                        community.setAreaStatus(3);
                        areaMapper.updateById(community);
                    }
                    if (status < community.getAreaStatus()){
                        status = community.getAreaStatus();
                    }
                    if (regionStatus < community.getAreaStatus()){
                        regionStatus = community.getAreaStatus();
                    }
                 }
                street.setAreaStatus(status);
                areaMapper.updateById(street);
             }
            region.setAreaStatus(regionStatus);
            areaMapper.updateById(region);
        }

    }

    @Override
    public List<Area> findAllArea() {
        return areaMapper.findAllArea();
    }

    @Override
    public List<String> getBuildingByAreaId(String areaId) {
        return areaMapper.getBuildingByAreaId(areaId);
    }

    @Override
    public PageInfo<Area> findChildrenList(Integer pageIndex, Integer pageSize, String parentId, boolean needPage) {
        if(needPage){
            PageHelper.startPage(pageIndex,pageSize);
        }
        QueryWrapper<Area> wrapper = new QueryWrapper<>();
        wrapper.eq("parentId", parentId);
        List<Area> list = areaMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }
}
