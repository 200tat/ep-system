package com.primeton.manageProvider.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.PatientAreaShelters;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PatientAreaSheltersMapper extends BaseMapper<PatientAreaShelters> {

    List<PatientAreaShelters> selectPatientByAreaId(String areaId, String patientName, String cardId, Date beginInfectionDate, Date endInfectionDate);
}
