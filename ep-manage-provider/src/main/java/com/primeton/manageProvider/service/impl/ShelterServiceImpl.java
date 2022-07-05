package com.primeton.manageProvider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Shelters;
import com.primeton.commom.vo.CodeMsg;

import com.primeton.manageProvider.mapper.ShelterMapper;
import com.primeton.manageProvider.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ShelterServiceImpl extends ServiceImpl<ShelterMapper, Shelters> implements ShelterService {

    @Autowired
    private ShelterMapper shelterMapper;


    @Override
    public List<Shelters> getShelters(String sheltersName) {
        List<Shelters> sheltersList = shelterMapper.getShelters(sheltersName);
        if (sheltersList ==null || sheltersList.isEmpty()){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return sheltersList;
    }

    @Override
    public void insertShelters(Shelters shelters) {
        String shelterId = UUID.randomUUID().toString().replace("-", "");
        shelters.setSheltersId(shelterId);
        Integer result = shelterMapper.insert(shelters);
        if (result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    @Override
    public void updateShelter(Shelters shelters) {
        Integer result = shelterMapper.updateById(shelters);
        if (result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }
}
