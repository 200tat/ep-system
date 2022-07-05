package com.primeton.manageProvider.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.*;

import com.primeton.commom.pojo.Area;
import com.primeton.commom.pojo.Patient;
import com.primeton.commom.pojo.User;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.IAreaService;
import com.primeton.manageProvider.service.PatientService;
import com.primeton.manageProvider.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@RestController
@RequestMapping("/patient")
@Api(tags = "患者")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IAreaService areaService;


    /**
     * 小区录入员新增患者
     * @param patient 病人信息
     * @param areaId 区域Id
     * @return
     */
    @ApiOperation("小区录入员新增患者")
    @PostMapping(value = "{areaId}")
    public ResultVo insertPatient(@ApiParam("患者信息")@RequestBody Patient patient,
                                  @ApiParam("患者所在区域") @PathVariable(value = "areaId")String areaId){
        if ("".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        //患者 以及其身份证 不能为空
        if (patient == null || patient.getCardId() == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        patientService.insertPatient(patient,areaId,"0");
        return new ResultVo();
    }

    /**
     * 方舱管理员转入患者，更改患者的方舱id
     * @param patientId 患者Id
     * @param sheltersId 方舱Id
     * @return
     */
    @ApiOperation("方舱管理员转入患者，更改患者的方舱id")
    @PatchMapping(value = "shelters/{sheltersId}")
    public ResultVo updatePatientShelters(@RequestParam String patientId,
                                          @PathVariable(value = "sheltersId")String sheltersId){
        if ("".equals(patientId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        patientService.updatePatient(patientId,sheltersId);
        return new ResultVo();
    }

    /**
     * 小区录入员更改未被复核患者信息及区域
     * @param patientId 患者id
     * @param patient 患者更改后信息
     * @param areaId 区域id
     * @return
     */
    @ApiOperation("小区录入员更改未被复核患者信息及区域")
    @PutMapping(value = "patientInfo/{patientId}")
    public ResultVo updatePatientInfo(@PathVariable(value = "patientId") String patientId,
                                      @RequestBody Patient patient,
                                      @RequestParam String areaId){
        if ("".equals(patientId) || patient == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Patient patientBefore = patientService.getBaseMapper().selectById(patientId);
        if (patientBefore == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        patient.setPatientId(patientId);
        //小区管理员只有在复核等级为0时有权限更改
        if (patientBefore.getRecheckStatus() != 0){
            throw new PermissionException(CodeMsg.NO_PERMISSION);
        }
        patientService.updatePatientInfo(patient,areaId);
        return new ResultVo();
    }

    /**
     * 根据患者ID查询患者信息，修改状态和转阴/死亡时间
     * @param patientId 患者id
     * @param patientStatus 患者状态（0阴 1阳 -1死亡）
     * @param resurrectionDate 转阴/死亡时间
     * @return
     */
    @ApiOperation("修改状态和转阴/死亡时间")
    @PatchMapping(value = "{patientId}")
    public ResultVo updatePatientStatus(@PathVariable(value = "patientId") String patientId,
                                        @RequestParam Integer patientStatus,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date resurrectionDate){
        if ("".equals(patientId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Patient patient = patientService.getBaseMapper().selectById(patientId);
        if (patient == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        if (patient.getPatientStatus() != 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        patientService.updatePatientStatus(patientId,patientStatus,resurrectionDate);
        return new ResultVo();
    }

    /**
     * 行政区和街道管理员复核患者
     * @param patientId 患者id
     * @param recheckStatus 复核状态
     * @return
     */
    @ApiOperation("行政区和街道管理员复核患者")
    @PatchMapping(value = "{recheckStatus}/{patientId}")
    public ResultVo updatePatientRecheckStatus(@PathVariable(value = "patientId")String patientId,
                                               @PathVariable(value = "recheckStatus") Integer recheckStatus){
        if ("".equals(recheckStatus) || "".equals(patientId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        if (recheckStatus < 0 || recheckStatus > 1){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        Patient patient = patientService.getBaseMapper().selectById(patientId);
        if (patient == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        patientService.updatePatientRecheckStatus(patient,recheckStatus);
        return new ResultVo();
    }


    /**
     * 根据患者ID查询患者（包括对应区域信息）
     * @param patientId
     * @return
     */
    @ApiOperation("根据患者ID查询患者")
    @GetMapping(value = "{patientId}")
    public ResultVo findPatientById(@PathVariable("patientId")String patientId){
        if ("".equals(patientId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Patient patient = patientService.findPatientById(patientId);
        return new ResultVo(patient);
    }


    //areaLevel通过area来获取不需要前端提供
    /**
     * 根据登录token使用条件查询多个区域内患者信息（行政区，街道，小区
     * @param recheckStatus 复核状态
     * @param PatientName 患者姓名
     * @param cardId 身份证号码
     * @param areaLevel 区域等级
     * @param beginInfectionDate 阳性日期左区间
     * @param endInfectionDate 阳性日期右区间
     * @return
     */
    @ApiOperation("根据登录token使用条件查询多个区域内患者信息（行政区，街道，小区)")
    @GetMapping(value = "area/{recheckStatus}")
    public ResultVo findPatientInArea(HttpServletRequest request,
                                        @PathVariable(value = "recheckStatus") Integer recheckStatus,
                                        @RequestParam(value = "areaLevel") int areaLevel,
                                        @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                        @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                        @RequestParam(value = "PatientName",required = false) String PatientName,
                                        @RequestParam(value = "cardId",required = false) String cardId,
                                        @RequestParam(value = "areaId",required = false) String areaId,
                                        @RequestParam(value = "beginInfectionDate",required = false) @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd") Date beginInfectionDate,
                                        @RequestParam(value = "endInfectionDate",required = false) @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd") Date endInfectionDate){
        if ("".equals(recheckStatus)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        if (areaLevel <0 || areaLevel >3 || recheckStatus <-2 || recheckStatus>2){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        PageInfo<Patient> pageInfo = null;
        if (areaId == null || areaId.isEmpty()){
            String token = request.getHeader("token");
            String userString =(String) redisTemplate.opsForValue().get(token);
            User user = JSON.parseObject(userString, User.class);
            pageInfo = patientService.selectPatientByAreaId(user,pageIndex,pageSize,PatientName,cardId,beginInfectionDate,endInfectionDate,areaLevel,recheckStatus);
        }else {
            pageInfo = patientService.selectPatientWithAreaId(areaId,pageIndex,pageSize,PatientName,cardId,beginInfectionDate,endInfectionDate,areaLevel,recheckStatus);
        }
        return new ResultVo(pageInfo);
    }



//    /**
//     * 根据token查询方舱内患者信息
//     * @return
//     */
//    //todo
//    @ApiOperation("根据token查询方舱内患者信息")
//    @GetMapping(value = "shelters/patients")
//    public ResultVo findPatientsByResourceId(HttpServletRequest request,
//                                             @ApiParam("病人姓名")@RequestParam(value = "patientName",required = false) String patientName,
//                                             @ApiParam("身份证号码")@RequestParam(value = "cardId",required = false) String cardId,
//                                             @ApiParam("方舱id")@RequestParam(value = "sheltersId",required = false) String sheltersId,
//                                             @RequestParam(value = "pageIndex",defaultValue = "1")Integer pageIndex,
//                                             @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize,
//                                             @RequestParam(value = "beginInfectionDate",required = false)@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date beginInfectionDate,
//                                             @RequestParam(value = "endInfectionDate",required = false)@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endInfectionDate){
//        PageInfo<Patient> pageInfo = null;
//        if(sheltersId == null || sheltersId.isEmpty()){
//            String token = request.getHeader("token");
//            String userString = (String)redisTemplate.opsForValue().get(token);
//            User user = JSON.parseObject(userString, User.class);
//            for (String shelterId : userService.findUserResource(user.getUserId())) {
//                pageInfo = patientService.findPatientsBySheltersId(shelterId,pageIndex,pageSize,patientName,cardId,beginInfectionDate,endInfectionDate);
//            }
//        }else{
//            pageInfo = patientService.findPatientsBySheltersId(sheltersId,pageIndex,pageSize,patientName,cardId,beginInfectionDate,endInfectionDate);
//
//        }
//
//        return new ResultVo(pageInfo);
//    }

    @ApiOperation("条件查询当前方舱管理员方舱的患者")
    @GetMapping("shelters")
    public ResultVo getPatientInShelters(HttpServletRequest request,
                                         @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                         @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                         @ApiParam("区域Id")@RequestParam(value = "areaId",required = false) String areaId,
                                         @ApiParam("患者姓名")@RequestParam(value = "patientName",required = false) String patientName,
                                         @ApiParam("患者身份证")@RequestParam(value = "cardId",required = false) String cardId,
                                         @ApiParam("阳性时间大于此时间")@RequestParam(value = "beginDate",required = false) Date beginDate,
                                         @ApiParam("阳性时间小于此时间")@RequestParam(value = "endDate",required = false) Date endDate){

        String token = request.getHeader("token");
        String stringObj = (String)redisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(stringObj, User.class);
        if (user == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        user = userService.findUserById(user.getUserId());




        if (user == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        PageInfo pageInfo = patientService.findPatientsInShelter(user.getShelters().getSheltersId(), pageIndex, pageSize, patientName, cardId, beginDate, endDate,areaId);
        return new ResultVo(pageInfo);
    }

    @ApiOperation("条件查询未进入方舱（即防疫中心）的患者")
    @GetMapping("center")
    public ResultVo getPatientInCenter(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                       @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                       @ApiParam("患者姓名")@RequestParam(value = "patientName",required = false) String patientName,
                                       @ApiParam("患者身份证")@RequestParam(value = "cardId",required = false) String cardId,
                                       @ApiParam("区域Id")@RequestParam(value = "areaId",required = false) String areaId,
                                       @ApiParam("阳性时间大于此时间")@RequestParam(value = "beginDate",required = false) Date beginDate,
                                       @ApiParam("阳性时间小于此时间")@RequestParam(value = "endDate",required = false) Date endDate){
        Area area = null;
        if (areaId != null &&  !"".equals(areaId)){
            area = areaService.getBaseMapper().selectById(areaId);
            if(area == null){
                throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
            }
        }

        PageInfo<Patient> pageInfo = patientService.findPatientsInShelter("0",pageIndex, pageSize, patientName, cardId, beginDate, endDate,areaId);

        if (pageInfo.getList() == null || pageInfo.getList().isEmpty())
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        return new ResultVo(pageInfo);
    }

}
