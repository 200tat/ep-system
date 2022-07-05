package com.primeton.manageProvider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.primeton.commom.pojo.Shelters;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ShelterMapper extends BaseMapper<Shelters> {

    List<Shelters> getShelters(String sheltersName);
}
