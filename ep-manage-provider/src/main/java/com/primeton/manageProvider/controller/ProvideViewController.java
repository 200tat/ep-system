package com.primeton.manageProvider.controller;

import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.pojo.Area;
import com.primeton.commom.vo.CodeMsg;

import com.primeton.manageProvider.service.IAreaService;
import com.primeton.manageProvider.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("provide/view")
@Api(tags = "疫情大数据")
public class ProvideViewController {

    @Autowired
    private IAreaService areaService;

    @Autowired
    private PatientService patientService;

    /**
     * 提供所有区域的信息
     * @return
     */
    @ApiOperation("提供所有区域的信息")
    @GetMapping("area")
    public List<Area> getAllArea(){
        List<Area> allArea = areaService.findAllArea();
        //判断查询是否为空
        if(allArea == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return allArea;

    }

    /**
     * 根据区域ID获取此区域的新增阳性患者个数
     * @param areaId
     * @return
     */
    @ApiOperation(value = "获取此区域的新增阳性患者个数",response = Integer.class)
    @GetMapping("newPatientCount")
    public Integer newPatientCount(@RequestParam("areaId") String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientCount = patientService.getPatientCount(areaId, true, null);
        //判断查询是否为空
        if(patientCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientCount;
    }

    /**
     * 获取此区域的新增治愈人数
     * @param areaId
     * @return
     */
    @ApiOperation(value = "获取此区域的新增治愈人数",response = Integer.class)
    @GetMapping("newCuredCount")
    public Integer newCuredCount(@RequestParam("areaId") String areaId){
        if("".equals(areaId)||areaId == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer newCuredCount = patientService.newCuredDeathCount(areaId,0);
        if(newCuredCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return newCuredCount;
    }


    /**
     * 获取此区域的新增死亡人数
     * @param areaId
     * @return
     */
    @ApiOperation(value = "获取此区域的新增死亡人数",response = Integer.class)
    @GetMapping("newDeathCount")
    public Integer newDeathCount(@RequestParam("areaId") String areaId){
        if("".equals(areaId)||areaId == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer newDeathCount = patientService.newCuredDeathCount(areaId,-1);
        if(newDeathCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return newDeathCount;
    }



    /**
     * 根据区域ID 获取此区域的 还未痊愈(未转阴)的 阳性患者个数
     * @param areaId
     * @return
     */
    @ApiOperation("获取此区域的现存病例数量")
    @GetMapping("presentPatientCount")
    public Integer presentPatientCount(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientCount = patientService.getPatientCount(areaId, false, 1);
        //判断查询是否为空
        if(patientCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientCount;
    }


    /**
     * 根据区域ID 获取此区域的 累计患者个数
     * @param areaId
     * @return
     */
    @ApiOperation("获取此区域的累计病例数量")
    @GetMapping("allPatientCount")
    public Integer allPatientCount(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientCount = patientService.getPatientCount(areaId, false, null);
        //判断查询是否为空
        if(patientCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientCount;
    }

    /**
     * 根据区域ID 获取此区域的 累计治愈患者个数
     * @param areaId
     * @return
     */
    @ApiOperation("获取此区域的治愈病例数量")
    @GetMapping("allCuredCount")
    public Integer curedPatientCount(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientCount = patientService.getPatientCount(areaId, false, 0);
        //判断查询是否为空
        if(patientCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientCount;
    }

    /**
     * 根据区域ID 获取此区域的 累计死亡患者个数
     * @param areaId
     * @return
     */
    @ApiOperation("获取此区域的死亡病例数量")
    @GetMapping("allDeathCount")
    public Integer deathPatientCount(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientCount = patientService.getPatientCount(areaId, false, -1);
        //判断查询是否为空
        if(patientCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientCount;
    }

    /**
     *根据区域Id和是否为当日转运 获取此区域的转运至方舱的患者数量（可为累计也可为当日）
     * @param areaId 区域Id
     * @param todayBool 是否为当日转运
     * @return
     */
    @ApiOperation("获取此区域的转运至方舱的患者数量")
    @GetMapping("transferCount")
    public Integer transferCount(String areaId, Boolean todayBool){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

       Integer patientToSheltersCount = patientService.getPatientToSheltersCount(areaId, false,todayBool);

        //判断查询是否为空
        if(patientToSheltersCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientToSheltersCount;
    }

    /**
     * 根据区域ID 获取此区域的 未转运至方舱（防疫中心，即居家）的患者个数
     * @param areaId
     * @return
     */
    @ApiOperation("获取此区域的居家患者数量")
    @GetMapping("stayCount")
    public Integer stayCount(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Integer patientToSheltersCount = patientService.getPatientToSheltersCount(areaId, true,false);
        //判断查询是否为空
        if(patientToSheltersCount == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return patientToSheltersCount;
    }


    /**
     * 获取小区的 封控楼栋
     * @param areaId
     * @return
     */
    @ApiOperation("获取封控楼栋")
    @GetMapping("building")
    public List<String> getBuilding(String areaId){
        //判断必要参数不为空
        if(areaId == null || "".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        List<String> buildingByAreaId = areaService.getBuildingByAreaId(areaId);
        //判断查询是否为空
        if(buildingByAreaId == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return buildingByAreaId;
    }


}
