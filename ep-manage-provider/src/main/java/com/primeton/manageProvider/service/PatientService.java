package com.primeton.manageProvider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.pojo.Patient;
import com.primeton.commom.pojo.User;

import java.util.Date;
import java.util.List;

public interface PatientService extends IService<Patient> {

    PageInfo<Patient> selectPatientByAreaId(User user, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate, Integer areaLevel, Integer recheckStatus);

    //行政区和街道管理员复核患者
    void updatePatientRecheckStatus(Patient patient, Integer recheckStatus);

    void insertPatient(Patient patient, String areaId, String sheltersId);

    void updatePatient(String patientId, String sheltersId);

    void updatePatientStatus(String patientId, Integer patientStatus, Date resurrectionDate);

    void updatePatientInfo(Patient patient, String areaId);

    List<Patient> findPatientsByResourceId(String sheltersId);

    Integer getPatientCount(String areaId,Boolean isNew,Integer patientStatus);

    Integer getPatientToSheltersCount(String areaId, Boolean stayHome, Boolean todayBool);

    PageInfo<Patient> selectPatientWithAreaId(String areaId, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate, int areaLevel, Integer recheckStatus);

    Patient findPatientById(String patientId);

    PageInfo<Patient> findPatientsInShelter(String sheltersId, Integer pageIndex, Integer pageSize, String patientName, String cardId, Date beginDate, Date endDate, String areaId);

    Integer newCuredDeathCount(String areaId, int patientStatus);

}
