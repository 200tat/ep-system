package com.primeton.manageProvider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.Patient;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    //使用条件查询，查询多个小区内患者信息
    List<Patient> selectPatientByAreaId(List<String> areaIdList, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate,Integer areaLevel,Integer recheckStatus);

    List<Patient> findPatientsByResourceId(String sheltersId);


    List<String> selectInfectedPatientByAreaId(List<Integer> patientStatusList, Integer recheckStatus, String areaId);



    /*resourceType（Integer）
    resourceId(String)
    seven(Boolean)
    patientIdList(List)*/
    Integer selectCountOfPatientInCenter(Integer resourceType, String resourceId,Boolean seven,List<String> patientIdList);
    /*        patientStatusList
              recheckStatus
              areaId*/
    Integer selectCountOfPatientCured(List<Integer> patientStatusList, Integer recheckStatus,String areaId);

    Integer getPatientCount(String areaId,Boolean isNew,Integer patientStatus);

    /**
     * 查询区域ID中 患者的ID集合
     * @param areaId
     * @return
     */
    List<String> getPatientIdWithArea(String areaId);

    Integer selectCountPatientBySheltersId(List<String> patientIdList, Boolean stayHome, Boolean todayBool);

    Patient findPatientById(String patientId);

    List<Patient> selectCountPatientBySheltersId(String sheltersId);

    List<Patient> selectPatientBySheltersId(String sheltersId, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate);

    /**
     * 查询在防疫中心的患者
     * @param patientName
     * @param cardId
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Patient> findPatientInCenter(String sheltersId,String patientName, String cardId, Date beginDate, Date endDate,List<String> areaIdList);

    Integer getNewCuredDeathCount(String areaId, int patientStatus);
}
