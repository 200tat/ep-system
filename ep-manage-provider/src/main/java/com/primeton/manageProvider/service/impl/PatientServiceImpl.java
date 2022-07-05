package com.primeton.manageProvider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.exception.UserNotFoundException;
import com.primeton.commom.pojo.*;
import com.primeton.manageProvider.util.StringDealUtil;
import com.primeton.commom.vo.CodeMsg;

import com.primeton.manageProvider.mapper.AreaMapper;
import com.primeton.manageProvider.mapper.PatientAreaSheltersMapper;
import com.primeton.manageProvider.mapper.PatientMapper;
import com.primeton.manageProvider.mapper.UserRoleAreaSheltersMapper;
import com.primeton.manageProvider.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private PatientAreaSheltersMapper patientAreaSheltersMapper;

    @Autowired
    private UserRoleAreaSheltersMapper userRoleAreaSheltersMapper;


    /**
     * @param areaId             区域ID
     * @param pageIndex
     * @param pageSize
     * @param patientName        患者名
     * @param cardId             身份证
     * @param beginInfectionDate
     * @param endInfectionDate
     * @param areaLevel          区域等级
     * @param recheckStatus      复核状态
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<Patient> selectPatientWithAreaId(String areaId, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate, int areaLevel, Integer recheckStatus) {
        ArrayList<String> communityAreaIdList = new ArrayList<>();
        List<Patient> patientList = new ArrayList<>();
        Area area = areaMapper.findPatientWithId(areaId);
        //行政区管理员查询所管理的多个区域内患者信息以及条件查询
        if (areaLevel == 1) {
            //判断行政区下是否有街道
            if (area != null) {
                //遍历该区域信息中的子区域信息（街道信息）
                for (Area streetArea : area.getChildren()) {
                    //判断街道下是否有小区
                    if (streetArea != null) {
                        //获取该区域信息中的子区域信息（小区信息）
                        for (Area communityArea : streetArea.getChildren()) {
                            //判断小区是否为null
                            if (communityArea != null) {
                                //获取小区信息中的小区Id
                                String communityAreaId = communityArea.getAreaId();
                                //将获取到的小区Id装在统一的集合list中
                                communityAreaIdList.add(communityAreaId);
                            }
                        }
                    }
                }
            }
        }

        //街道管理员查询所管理的多个区域内患者信息以及条件查询
        if (areaLevel == 2) {

            //判断街道下是否有小区
            if (area != null) {

                //获取该区域信息中的子区域信息（小区信息）
                for (Area communityArea : area.getChildren()) {
                    //判断小区是否为null
                    if (communityArea != null) {
                        //获取小区信息中的小区Id
                        String communityAreaId = communityArea.getAreaId();
                        //将获取到的小区Id装在统一的集合list中
                        communityAreaIdList.add(communityAreaId);
                    }
                }
            }
        }

        //小区管理员查询所管理的多个区域内患者信息以及条件查询
        if (areaLevel == 3) {
            //判断小区是否为null
            if (area != null) {
                //获取小区信息中的小区Id
                String communityAreaId = area.getAreaId();
                //将获取到的小区Id装在统一的集合list中
                communityAreaIdList.add(communityAreaId);
            }
        }

        //判断communityAreaIdList是否为null
        if (communityAreaIdList.size() != 0) {
            //分页
            PageHelper.startPage(pageIndex, pageSize);
            //使用条件查询，查询多个小区内患者信息
            patientList = patientMapper.selectPatientByAreaId(communityAreaIdList, patientName, cardId, beginInfectionDate, endInfectionDate, areaLevel, recheckStatus);
        }
        if (patientList == null || patientList.isEmpty()) {
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new PageInfo<>(patientList);

    }

    @Override
    @Transactional(readOnly = true)
    public Patient findPatientById(String patientId) {
        Patient patient = patientMapper.findPatientById(patientId);
        if (patient == null) {
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        return patient;
    }


    @Override
    @Transactional(readOnly = true)
    public PageInfo findPatientsInShelter(String sheltersId, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginDate, Date endDate, String areaId) {
        patientName = StringDealUtil.verify(patientName);
        cardId = StringDealUtil.verify(cardId);

        List<String> communityAreaIdList = new ArrayList<>();

        Area area = null;
        if (areaId != null &&  !"".equals(areaId)){
            area = areaMapper.findPatientWithId(areaId);
            if(area == null){
                throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
            }
        }

        if (area != null){
            Integer areaLevel = area.getAreaLevel();
            //行政区管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 1) {
                //判断行政区下是否有街道
                if (area != null) {
                    //遍历该区域信息中的子区域信息（街道信息）
                    for (Area streetArea : area.getChildren()) {
                        //判断街道下是否有小区
                        if (streetArea != null) {
                            //获取该区域信息中的子区域信息（小区信息）
                            for (Area communityArea : streetArea.getChildren()) {
                                //判断小区是否为null
                                if (communityArea != null) {
                                    //获取小区信息中的小区Id
                                    String communityAreaId = communityArea.getAreaId();
                                    //将获取到的小区Id装在统一的集合list中
                                    communityAreaIdList.add(communityAreaId);
                                }
                            }
                        }
                    }
                }
            }

            //街道管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 2) {

                //判断街道下是否有小区
                if (area != null) {

                    //获取该区域信息中的子区域信息（小区信息）
                    for (Area communityArea : area.getChildren()) {
                        //判断小区是否为null
                        if (communityArea != null) {
                            //获取小区信息中的小区Id
                            String communityAreaId = communityArea.getAreaId();
                            //将获取到的小区Id装在统一的集合list中
                            communityAreaIdList.add(communityAreaId);
                        }
                    }
                }
            }

            //小区管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 3) {
                //判断小区是否为null
                if (area != null) {
                    //获取小区信息中的小区Id
                    String communityAreaId = area.getAreaId();
                    //将获取到的小区Id装在统一的集合list中
                    communityAreaIdList.add(communityAreaId);
                }
            }
        }

        if(communityAreaIdList.size() == 0){
            communityAreaIdList = null;
        }

        PageHelper.startPage(pageIndex, pageSize);
        List<Patient> patientList = patientMapper.findPatientInCenter(sheltersId,patientName, cardId, beginDate, endDate,communityAreaIdList);
        if (patientList == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new PageInfo(patientList);
    }

    @Override
    public Integer newCuredDeathCount(String areaId, int patientStatus) {
        return patientMapper.getNewCuredDeathCount(areaId,patientStatus);
    }

    //查询某区域中所有患者
    @Override
    @Transactional
    public PageInfo<Patient> selectPatientByAreaId(User user, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate, Integer areaLevel, Integer recheckStatus) {
        //患者集合
        List<Patient> patientList = new ArrayList<>();

        //小区Id集合，用于查询患者信息时所对应的小区Id的参数传递
        ArrayList<String> communityAreaIdList = new ArrayList<>();

        //通过user查询areaIdList
        HashMap<String, Object> map = new HashMap<>();
        map.put("participantId", user.getUserId());
        List<UserRoleAreaShelters> userRoleAreaSheltersList = userRoleAreaSheltersMapper.selectByMap(map);
        ArrayList<String> areaIdList = new ArrayList<>();
        for (UserRoleAreaShelters uras:
                userRoleAreaSheltersList) {
            areaIdList.add(uras.getResourceId());

        }


        //循环遍历多个区域Id
        for (String areaId: areaIdList) {

            //查询每个循环的区域Id所对应的区域信息
            Area area = areaMapper.findPatientWithId(areaId);

            //行政区管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 1) {
                //判断行政区下是否有街道
                if (area != null) {
                    //遍历该区域信息中的子区域信息（街道信息）
                    for (Area streetArea : area.getChildren()) {
                        //判断街道下是否有小区
                        if (streetArea != null) {
                            //获取该区域信息中的子区域信息（小区信息）
                            for (Area communityArea : streetArea.getChildren()) {
                                //判断小区是否为null
                                if (communityArea != null) {
                                    //获取小区信息中的小区Id
                                    String communityAreaId = communityArea.getAreaId();
                                    //将获取到的小区Id装在统一的集合list中
                                    communityAreaIdList.add(communityAreaId);
                                }
                            }
                        }
                    }
                }
            }

            //街道管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 2) {

                //判断街道下是否有小区
                if (area != null) {

                    //获取该区域信息中的子区域信息（小区信息）
                    for (Area communityArea : area.getChildren()) {
                        //判断小区是否为null
                        if (communityArea != null) {
                            //获取小区信息中的小区Id
                            String communityAreaId = communityArea.getAreaId();
                            //将获取到的小区Id装在统一的集合list中
                            communityAreaIdList.add(communityAreaId);
                        }
                    }
                }
            }

            //小区管理员查询所管理的多个区域内患者信息以及条件查询
            if (areaLevel == 3) {
                //判断小区是否为null
                if (area != null) {
                    //获取小区信息中的小区Id
                    String communityAreaId = area.getAreaId();
                    //将获取到的小区Id装在统一的集合list中
                    communityAreaIdList.add(communityAreaId);
                }
            }
        }

            //判断communityAreaIdList是否为null
            if (communityAreaIdList.size() != 0) {
                //分页
                PageHelper.startPage(pageIndex, pageSize);
                //使用条件查询，查询多个小区内患者信息
                patientList = patientMapper.selectPatientByAreaId(communityAreaIdList, patientName, cardId, beginInfectionDate, endInfectionDate, areaLevel, recheckStatus);
            }
        if (patientList == null || patientList.isEmpty()){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new PageInfo<>(patientList);
    }

    /**
     * 更改 患者的 复核状态
     * @param patient
     * @param recheckStatus
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePatientRecheckStatus(Patient patient, Integer recheckStatus) {
        if(recheckStatus > 0){
            patient.setRecheckStatus(patient.getRecheckStatus() + 1);
        } else {
            patient.setRecheckStatus((patient.getRecheckStatus() + 1) * -1);
        }
        int update = patientMapper.updateById(patient);
        if (update < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 新增病人
     * @param patient 患者信息
     * @param areaId 区域Id
     * @param sheltersId 默认为0
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertPatient(Patient patient, String areaId, String sheltersId) {
        patient.setPatientStatus(1);
        String patientId = UUID.randomUUID().toString().replace("-", "");
        patient.setPatientId(patientId);
        int insert1 = patientMapper.insert(patient);
        PatientAreaShelters patientArea = new PatientAreaShelters(UUID.randomUUID().toString().replace("-", ""), areaId, 0, patientId,0,null);
        PatientAreaShelters patientShelters = new PatientAreaShelters(UUID.randomUUID().toString().replace("-", ""), sheltersId, 1, patientId, 0, null);
        int insert2 = patientAreaSheltersMapper.insert(patientArea);
        int insert3 = patientAreaSheltersMapper.insert(patientShelters);
        int result = insert1+insert2+insert3;
        if (result < 3)
            throw new OperationException(CodeMsg.OPERATION_FAIL);
    }

    /**
     * 方舱转运病人
     * @param patientId 患者Id
     * @param sheltersId 方舱Id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePatient(String patientId, String sheltersId) {
        Map<String, Object> map = new HashMap<>();
        map.put("participantId",patientId);
        map.put("resourceType","1");
        List<PatientAreaShelters> pas = patientAreaSheltersMapper.selectByMap(map);
        PatientAreaShelters patientAreaShelters = pas.get(0);
        patientAreaShelters.setResourceId(sheltersId);
        int update = patientAreaSheltersMapper.updateById(patientAreaShelters);
        if (update < 1)
            throw new OperationException(CodeMsg.OPERATION_FAIL);
    }

    /**
     * 根据患者ID查询患者信息，修改状态和转阴/死亡时间
     * @param patientId 患者id
     * @param patientStatus 患者状态
     * @param resurrectionDate 转阴/死亡时间
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePatientStatus(String patientId, Integer patientStatus, Date resurrectionDate) {
        Patient patient = patientMapper.selectById(patientId);
        patient.setPatientStatus(patientStatus);
        patient.setResurrectionDate(resurrectionDate);
        int update = patientMapper.updateById(patient);
        if (update < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 小区更改患者信息及区域
     * @param patient 患者更改后信息
     * @param areaId 区域id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePatientInfo(Patient patient, String areaId) {
        int update1 = patientMapper.updateById(patient);
        Map<String, Object> map = new HashMap<>();
        map.put("participantId",patient.getPatientId());
        map.put("resourceType","0");
        List<PatientAreaShelters> pas = patientAreaSheltersMapper.selectByMap(map);
        PatientAreaShelters patientAreaShelters = pas.get(0);
        patientAreaShelters.setResourceId(areaId);
        int update2 = patientAreaSheltersMapper.updateById(patientAreaShelters);
        if ((update1+update2) < 2){
           throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 根据方舱id获取病人信息
     * @param sheltersId
     * @return
     */
    @Override
    @Transactional
    public List<Patient> findPatientsByResourceId(String sheltersId) {
        List<Patient> list = patientMapper.findPatientsByResourceId(sheltersId);
        return list;
    }


    /**
     * 获取当日新增患者人数 提供 isNew(boolean)
     * 获取还未转阴的患者人数 提供 patientStatus(Integer) 输入1
     * 获取累计患者人数，全部输入null
     * 获取累计治愈患者人数， 提供 patientStatus(Integer) 输入0
     * 获取累计死亡患者人数， 提供 patientStatus(Integer) 输入-1
     * @param areaId
     * @param isNew
     * @param patientStatus
     * @return
     */
    @Override
    @Transactional
    public Integer getPatientCount(String areaId,Boolean isNew,Integer patientStatus) {
        return patientMapper.getPatientCount(areaId,isNew,patientStatus);
    }


    /**
     * 查询 患者在防疫中心 或者是 方舱的人数
     * stayHome 为 true 时 查询防疫中心（居家）的患者人数
     *          为false 时 查询转运去方舱的患者人数
     * @param areaId
     * @param stayHome
     * @return
     */
    @Override
    @Transactional
    public Integer getPatientToSheltersCount(String areaId, Boolean stayHome, Boolean todayBool) {
        List<String> patientIdList = patientMapper.getPatientIdWithArea(areaId);
        return patientMapper.selectCountPatientBySheltersId(patientIdList,stayHome,todayBool);
    }


}
