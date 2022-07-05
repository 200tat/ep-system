package com.primeton.viewProvider.feign;



import com.primeton.commom.pojo.Area;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;



@FeignClient(value = "ep-manage-provider")
@RequestMapping("provide/view")
public interface ViewFeign {

    /**
     * 提供所有区域的信息
     * @return
     */
    @RequestMapping("area")
    public List<Area> getAllArea();

    /**
     * 根据区域ID获取此区域的新增阳性患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("newPatientCount")
    public Integer newPatientCount(@RequestParam("areaId") String areaId);

    /**
     * 根据区域ID获取此区域的新增治愈个数
     * @param areaId
     * @return
     */
    @RequestMapping("newCuredCount")
    public Integer newCuredCount(@RequestParam String areaId);

    /**
     * 根据区域ID获取此区域的新增死亡个数
     * @param areaId
     * @return
     */
    @RequestMapping("newDeathCount")
    public Integer newDeathCount(@RequestParam String areaId);

    /**
     * 根据区域ID 获取此区域的 还未痊愈(未转阴)的 阳性患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("presentPatientCount")
    public Integer presentPatientCount(@RequestParam String areaId);

    /**
     * 根据区域ID 获取此区域的 累计患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("allPatientCount")
    public Integer allPatientCount(@RequestParam String areaId);

    /**
     * 根据区域ID 获取此区域的 累计治愈患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("allCuredCount")
    public Integer curedPatientCount(@RequestParam String areaId);

    /**
     * 根据区域ID 获取此区域的 累计死亡患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("allDeathCount")
    public Integer deathPatientCount(@RequestParam String areaId);

    /**
     * 根据区域ID 获取此区域的 转运至方舱的患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("transferCount")
    public Integer transferCount(@RequestParam String areaId,@RequestParam Boolean todayBool);
    /**
     * 根据区域ID 获取此区域的 未转运至方舱（防疫中心，即居家）的患者个数
     * @param areaId
     * @return
     */
    @RequestMapping("stayCount")
    public Integer stayCount(@RequestParam String areaId);

}
