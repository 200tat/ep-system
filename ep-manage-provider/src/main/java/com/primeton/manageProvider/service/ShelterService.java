package com.primeton.manageProvider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.primeton.commom.pojo.Shelters;

import java.util.List;

public interface ShelterService extends IService<Shelters> {

    List<Shelters> getShelters(String sheltersName);

    void insertShelters(Shelters shelters);

    void updateShelter(Shelters shelters);
}
