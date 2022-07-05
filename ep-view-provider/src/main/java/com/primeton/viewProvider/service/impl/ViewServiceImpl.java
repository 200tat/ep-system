package com.primeton.viewProvider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.primeton.commom.pojo.Area;
import com.primeton.commom.pojo.View;
import com.primeton.commom.util.DateDealUtil;
import com.primeton.viewProvider.feign.ViewFeign;
import com.primeton.viewProvider.mapper.ViewMapper;
import com.primeton.viewProvider.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ViewServiceImpl extends ServiceImpl<ViewMapper,View> implements ViewService{

    @Autowired
    private ViewFeign viewFeign;

    @Autowired
    private ViewMapper viewMapper;

    //所有区域的数据更新
//    @Scheduled(cron = "0 48 12 * * ?")
    public void updateCommunityViewDaily(){
        //获取所有的区域信息
        List<Area> allAreaList = viewFeign.getAllArea();

        //获取当天日期
        Date today = new Date();

        //给上海市添加基本信息
        updateShangHaiViewDaily();

        //所有区域对象添加基本信息
        for (Area a: allAreaList) {
            String areaId = a.getAreaId();
            String parentName = a.getParentArea() == null ? null : a.getParentArea().getAreaName();
            View view = new View();
            view.setAreaId(a.getAreaId());
            view.setAreaName(a.getAreaName());
            view.setParentId(a.getParentId());
            view.setParentName(parentName);
            view.setAreaLevel(a.getAreaLevel());
            view.setIsolationLevel(a.getAreaStatus());
            view.setBuilding(areaId);
            view.setStatisticalDate(today);

            //为小区添加其他患者人数和疫情信息
            if (a.getAreaLevel() == 3){
                Integer allPatientCount = viewFeign.allPatientCount(areaId);
                Integer newPatientCount = viewFeign.newPatientCount(areaId);
                Integer newCuredCount = viewFeign.newCuredCount(areaId);
                Integer newDeathCount = viewFeign.newDeathCount(areaId);
                Integer presentPatientCount = viewFeign.presentPatientCount(areaId);
                Integer curedPatientCount = viewFeign.curedPatientCount(areaId);
                Integer deathPatientCount = viewFeign.deathPatientCount(areaId);
                Integer transferCount = viewFeign.transferCount(areaId,false);
                Integer transferCountToday = viewFeign.transferCount(areaId,true);
                Integer stayCount = viewFeign.stayCount(areaId);
                view.setAllPatientCount(allPatientCount);
                view.setNewPatientCount(newPatientCount);
                view.setNewCuredCount(newCuredCount);
                view.setNewDeathCount(newDeathCount);
                view.setPresentPatientCount(presentPatientCount);
                view.setAllCuredCount(curedPatientCount);
                view.setAllDeathCount(deathPatientCount);
                view.setTransferCount(transferCount);
                view.setTransferCountToday(transferCountToday);
                view.setStayCount(stayCount);
            }
            viewMapper.insert(view);
        }

        //添加街道的患者和疫情信息
        updateCommunityOtherViewDaily(2,today);

        //添加行政区的患者和疫情信息
        updateCommunityOtherViewDaily(1,today);

        //添加上海市的患者和疫情信息
        updateCommunityOtherViewDaily(0,today);

    }

    //更新上海市每日view基本信息
    public void updateShangHaiViewDaily(){
        //创建view对象用于接收更新信息
        View view = new View();

        //获取当天日日期
        Date today = DateDealUtil.paresDate(new Date());

        //给上海市插入基本信息
        view.setAreaId("0");
        view.setAreaName("上海市");
//        view.setParentId("0");
//        view.setParentName();
        view.setAreaLevel(0);
//        view.setIsolationLevel(a.getAreaStatus());
//        view.setBuilding(areaId);
        view.setStatisticalDate(today);

        viewMapper.insert(view);

    }


    //根据子区域疫情患者信息更新父区域疫情患者信息
    //areaLevel为父区域，区域等级

    public void updateCommunityOtherViewDaily(Object areaLevel,Date today) {
        today = DateDealUtil.paresDate(today);

        //父区域数据更新
        HashMap<String, Object> areaLevelMap = new HashMap<>();
        areaLevelMap.put("areaLevel", areaLevel);
        areaLevelMap.put("statisticalDate",today);
        //查询所有父区域
        List<View> areaList = viewMapper.selectByMap(areaLevelMap);
        //遍历每个父区域并更新数据
        for (View area : areaList) {
            //获取父区域Id
            String areaId = area.getAreaId();
            HashMap<String, Object> areaMap = new HashMap<>();
            //将父区域Id用于查询
            areaMap.put("parentId", areaId);
            areaMap.put("statisticalDate",today);
            //查询parentId为父区域Id下的子区域
            List<View> streetListByAreaId = viewMapper.selectByMap(areaMap);
            //创建子区域疫情患者的接收容器
            Integer allPatientCount = 0;
            Integer newPatientCount = 0;
            Integer newCuredCount = 0;
            Integer newDeathCount = 0;
            Integer presentPatientCount = 0;
            Integer allCuredCount = 0;
            Integer allDeathCount = 0;
            Integer transferCount = 0;
            Integer transferCountToday = 0;
            Integer stayCount = 0;
            //遍历子区域集合进行各患者数量加和
            for (View street : streetListByAreaId) {
                //父区域获取每个子区域的相关信息
                allPatientCount += street.getAllPatientCount();
                newPatientCount += street.getNewPatientCount();
                newCuredCount += street.getNewCuredCount();
                newDeathCount += street.getNewDeathCount();
                presentPatientCount += street.getPresentPatientCount();
                allCuredCount += street.getAllCuredCount();
                allDeathCount += street.getAllDeathCount();
                transferCount += street.getTransferCount();
                transferCountToday += street.getTransferCountToday();
                stayCount += street.getStayCount();

            }
            //将父区域其他信息数据封装
            area.setAllPatientCount(allPatientCount);
            area.setNewPatientCount(newPatientCount);
            area.setNewCuredCount(newCuredCount);
            area.setNewDeathCount(newDeathCount);
            area.setPresentPatientCount(presentPatientCount);
            area.setAllCuredCount(allCuredCount);
            area.setAllDeathCount(allDeathCount);
            area.setTransferCount(transferCount);
            area.setTransferCountToday(transferCountToday);
            area.setStayCount(stayCount);

            //将父区域的信息存储进数据库
            viewMapper.updateById(area);
        }
    }

    //趋势图
    @Override
    public Map<String, Object> getInDeCuCount(Integer day){
        Map<String,Object> map = new HashMap<>();
        // 新增 治愈 死亡 *day天
        int count[][] = new int[3][day];
        Map<String,Map> district = new HashMap<>();
        for (View districtView : viewMapper.findByAreaLevel(1)) {
            List<View> info = viewMapper.getInfoByAreaId(districtView.getAreaId(),day);
            Map<String,Object> districtMap = new HashMap<>();

            LinkedHashMap<Date,Object> districtNewPatientMap = new LinkedHashMap<>();
            LinkedHashMap<Date,Object> districtNewCuredMap = new LinkedHashMap<>();
            LinkedHashMap<Date,Object> districtNewDeathMap = new LinkedHashMap<>();
            for(View view : info){
                int i = 0;
                districtNewPatientMap.put(view.getStatisticalDate(),view.getNewPatientCount());
                districtNewCuredMap.put(view.getStatisticalDate(),view.getNewCuredCount() == null ? 0 : view.getNewCuredCount());
                districtNewDeathMap.put(view.getStatisticalDate(),view.getNewDeathCount() == null ? 0 : view.getNewDeathCount());

                count[0][i] += view.getNewPatientCount() == null ? 0:view.getNewPatientCount();
                count[1][i] += view.getNewCuredCount() == null ? 0:view.getNewCuredCount();
                count[2][i] += view.getNewDeathCount() == null ? 0:view.getNewDeathCount();


            }
            districtMap.put("区新增患者",districtNewPatientMap);
            districtMap.put("区新增治愈",districtNewCuredMap);
            districtMap.put("区新增死亡",districtNewDeathMap);

            //街道列表
            Map<String,Map> street = new HashMap<>();
            for (View streetView : viewMapper.findAreaByParentId(districtView.getAreaId(),false,null)) {
                Map<String,Object> streetMap = new HashMap<>();
                LinkedHashMap<Date,Object> streetNewPatientMap = new LinkedHashMap<>();
                LinkedHashMap<Date,Object> streetNewCuredMap = new LinkedHashMap<>();
                LinkedHashMap<Date,Object> streetNewDeathMap = new LinkedHashMap<>();

                for (View view : viewMapper.getInfoByAreaId(streetView.getAreaId(),day)) {
                    streetNewPatientMap.put(view.getStatisticalDate(),view.getNewPatientCount());
                    streetNewCuredMap.put(view.getStatisticalDate(),view.getNewCuredCount() == null ? 0 : view.getNewCuredCount());
                    streetNewDeathMap.put(view.getStatisticalDate(),view.getNewDeathCount() == null ? 0 : view.getNewDeathCount());
                }
                streetMap.put("街道新增患者",streetNewPatientMap);
                streetMap.put("街道新增治愈",streetNewCuredMap);
                streetMap.put("街道新增死亡",streetNewDeathMap);

                //小区列表
                Map<String,Map> community = new HashMap<>();
                for (View communityView : viewMapper.findAreaByParentId(streetView.getAreaId(),false,null)) {
                    Map<String,Object> communityMap = new HashMap<>();
                    LinkedHashMap<Date,Object> communityNewPatientMap = new LinkedHashMap<>();
                    LinkedHashMap<Date,Object> communityNewCuredMap = new LinkedHashMap<>();
                    LinkedHashMap<Date,Object> communityNewDeathMap = new LinkedHashMap<>();

                    for (View view : viewMapper.getInfoByAreaId(communityView.getAreaId(),day)) {
                        communityNewPatientMap.put(view.getStatisticalDate(),view.getNewPatientCount());
                        communityNewCuredMap.put(view.getStatisticalDate(),view.getNewCuredCount() == null ? 0 : view.getNewCuredCount());
                        communityNewDeathMap.put(view.getStatisticalDate(),view.getNewDeathCount() == null ? 0 : view.getNewDeathCount());
                    }
                    communityMap.put("小区新增患者",communityNewPatientMap);
                    communityMap.put("小区新增治愈",communityNewCuredMap);
                    communityMap.put("小区新增死亡",communityNewDeathMap);

                    community.put(communityView.getAreaName(),communityMap);

                }

                streetMap.put("小区情况",community);

                street.put(streetView.getAreaName(),streetMap);
            }

            districtMap.put("街道情况",street);


            district.put(districtView.getAreaName(),districtMap);

        }

        LinkedHashMap<Date,Object> allNewPatientMap = new LinkedHashMap<>();
        LinkedHashMap<Date,Object> allNewCuredMap = new LinkedHashMap<>();
        LinkedHashMap<Date,Object> allNewDeathMap = new LinkedHashMap<>();
        for(int i = 0;i<day;i++){
            //获取第某天日期
            Date date = DateDealUtil.getDayBefore(i+1, DateDealUtil.paresDate(new Date()));
            allNewPatientMap.put(date,count[0][i]);
            allNewCuredMap.put(date,count[1][i]);
            allNewDeathMap.put(date,count[2][i]);
        }

        map.put("新增患者",allNewPatientMap);
        map.put("新增治愈",allNewCuredMap);
        map.put("新增死亡",allNewDeathMap);
        map.put("行政区情况",district);

        return map;
    }



    //根据子区域疫情患者信息更新父区域疫情患者信息
    //areaLevel为父区域，区域等级

    public void updateCommunityOtherViewDaily(Object areaLevel) {
        //父区域数据更新
        HashMap<String, Object> areaLevelMap = new HashMap<>();
        areaLevelMap.put("areaLevel", areaLevel);
        //查询所有父区域
        List<View> areaList = viewMapper.selectByMap(areaLevelMap);
        //遍历每个父区域并更新数据
        for (View area : areaList) {
            //获取父区域Id
            String areaId = area.getAreaId();
            HashMap<String, Object> areaMap = new HashMap<>();
            //将父区域Id用于查询
            areaMap.put("parentId", areaId);
            //查询parentId为父区域Id下的子区域
            List<View> streetListByAreaId = viewMapper.selectByMap(areaMap);
            //创建子区域疫情患者的接收容器
            Integer allPatientCount = 0;
            Integer newPatientCount = 0;
            Integer presentPatientCount = 0;
            Integer allCuredCount = 0;
            Integer allDeathCount = 0;
            Integer transferCount = 0;
            Integer transferCountToday = 0;
            Integer stayCount = 0;
            //遍历子区域集合进行各患者数量加和
            for (View street : streetListByAreaId) {
                //父区域获取每个子区域的相关信息
                allPatientCount += street.getAllPatientCount();
                newPatientCount += street.getNewPatientCount();
                presentPatientCount += street.getPresentPatientCount();
                allCuredCount += street.getAllCuredCount();
                allDeathCount += street.getAllDeathCount();
                transferCount += street.getTransferCount();
                transferCountToday += street.getTransferCountToday();
                stayCount += street.getStayCount();

            }

            //将父区域其他信息数据封装
            area.setAllPatientCount(allPatientCount);
            area.setNewPatientCount(newPatientCount);
            area.setPresentPatientCount(presentPatientCount);
            area.setAllCuredCount(allCuredCount);
            area.setAllDeathCount(allDeathCount);
            area.setTransferCount(transferCount);
            area.setTransferCountToday(transferCountToday);
            area.setStayCount(stayCount);

            //将父区域的信息存储进数据库
            viewMapper.updateById(area);

        }
    }


        @Override
        public List<View> selectTodayViews () {
            Date date = DateDealUtil.paresDate(new Date());
            QueryWrapper<View> wrapper = new QueryWrapper<>();
            wrapper.ge("statisticalDate", date);
            List<View> list = viewMapper.selectList(wrapper);
            if (list == null || list.isEmpty()){
                date = DateDealUtil.getDayBefore(1,date);
                System.out.println(date);
                wrapper.ge("statisticalDate", date);
                list = viewMapper.selectList(wrapper);
            }
            return list;
        }

        @Override
        public List<View> getAreaEpidemic (String areaId,boolean outstanding, Date date){
            areaId = ("".equals(areaId) || "0".equals(areaId)) ? null : areaId;
            List<View> list = viewMapper.findAreaEpidemicByAreaId(areaId, date);
            /*if (list == null || list.size() == 0) {
                throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
            }*/
            for (View v : list) {
                if (v.getAreaLevel() == 1) {//为行政区时
                    //获取街道view
                    List<View> streetViewList = viewMapper.findAreaByParentId(v.getAreaId(), outstanding, date);
                    v.setChildren(streetViewList);
                    if (streetViewList != null) {
                        //获取小区view
                        for (View streetView : streetViewList) {
                            List<View> communityViewList = viewMapper.findAreaByParentId(streetView.getAreaId(), outstanding, date);
                            streetView.setChildren(communityViewList);
                        }
                    }
                } else if (v.getAreaLevel() == 2) {//为街道时
                    List<View> communityViewList = viewMapper.findAreaByParentId(v.getAreaId(), outstanding, date);
                    v.setChildren(communityViewList);
                }
            }
            return list;
        }

    @Override
    public List<String> getChildrenId(String areaId){
        List<String> viewList = viewMapper.getChildrenId(areaId);
        return viewList;
    }

    @Override
    public Map<String, Object> getAreaInfo(String areaId, Integer day) {
        LinkedHashMap<String,Object> newPatientMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> newCuredMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> newDeathMap = new LinkedHashMap<>();
        LinkedHashMap<String,Object> transferCountToday = new LinkedHashMap<>();
        LinkedHashMap<String,Object> stayCount = new LinkedHashMap<>();

        List<View> infoByAreaId = viewMapper.getInfoByAreaId(areaId, day);
        ListIterator<View> viewListIterator = infoByAreaId.listIterator();
        while(viewListIterator.hasNext()){
            View view = viewListIterator.next();
            newPatientMap.put(DateDealUtil.parseDateString(view.getStatisticalDate()),view.getNewPatientCount() == null ? 0 : view.getNewPatientCount());
            newCuredMap.put(DateDealUtil.parseDateString(view.getStatisticalDate()),view.getNewCuredCount() == null ? 0 : view.getNewCuredCount());
            newDeathMap.put(DateDealUtil.parseDateString(view.getStatisticalDate()),view.getNewDeathCount() == null ? 0 : view.getNewDeathCount());
            transferCountToday.put(DateDealUtil.parseDateString(view.getStatisticalDate()),view.getTransferCountToday() == null ? 0 : view.getTransferCountToday());
            stayCount.put(DateDealUtil.parseDateString(view.getStatisticalDate()),view.getStayCount() == null ? 0 : view.getStayCount());
        }

        Map<String,Object> map = new HashMap<>();
        map.put("新增患者",newPatientMap);
        map.put("新增治愈",newCuredMap);
        map.put("新增死亡",newDeathMap);
        map.put("当日转运",transferCountToday);
        map.put("遗留人数",stayCount);


        return map;

    }

    //获取该areaId下的几天内每日新增转运人数
    @Override
    public Map<Date, Integer> getTransferCount(String areaId, Integer day) {

        //创建map集合存入每日的转运人数
        Map<Date, Integer> transferCountMap = new HashMap<>();
        //遍历获取几日内的view信息
        for (View viewList : viewMapper.getInfoByAreaId(areaId, day)) {
            //获取某日转运人数
            Integer transferCountToday = viewList.getTransferCountToday();
            //获取某日日期
            Date statisticalDate = viewList.getStatisticalDate();
            transferCountMap.put(statisticalDate,transferCountToday);
        }

        return transferCountMap;

    }

        //查找几日内区域遗留人数
    @Override
    public Map<Date, Integer> getStayCount(String areaId, Integer day) {
        //创建map集合存入每日的转运人数
        Map<Date, Integer> stayCountMap = new HashMap<>();
        //遍历获取几日内的view信息
        for (View viewList : viewMapper.getInfoByAreaId(areaId, day)) {
            //获取某日转运人数
            Integer stayCount = viewList.getStayCount();
            //获取某日日期
            Date statisticalDate = viewList.getStatisticalDate();
            stayCountMap.put(statisticalDate,stayCount);
        }

        return stayCountMap;
    }


}
