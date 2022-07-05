package com.primeton.manageProvider.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Shelters;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.ShelterService;
import com.primeton.manageProvider.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("shelter")
@Api(tags = "方舱")
public class SheltersController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 超级管理员条件查询方舱信息
     * @param sheltersName
     * @return
     */
    @ApiOperation("超级管理员条件查询方舱信息")
    @GetMapping(value = "shelters")
    public ResultVo getShelters(
            @RequestParam(value = "pageIndex" ,required = false) Integer pageIndex,
            @RequestParam(value = "pageSize" ,required = false) Integer pageSize,
            @ApiParam("方舱名")@RequestParam(value = "sheltersName" ,required = false) String sheltersName){

        List sheltersList = shelterService.getShelters(sheltersName);

        if(pageIndex == null || pageSize == null){
            return new ResultVo<>(sheltersList);
        }else {
            PageHelper.startPage(pageIndex,pageSize);
            PageInfo pageInfo = new PageInfo<>(sheltersList);
            return new ResultVo(pageInfo);
        }
    }

    /**
     * 超级管理员添加方舱
     * @param shelters
     * @return
     */
    @ApiOperation("超级管理员添加方舱")
    @PostMapping(value = "shelter")
    public ResultVo insertShelters(@ApiParam("方舱信息") @RequestBody Shelters shelters){
        shelterService.insertShelters(shelters);
        return new ResultVo();

    }

    /**
     * 超级管理员删除方舱
     * @param shelterId
     * @return
     */
    @ApiOperation("超级管理员删除方舱")
    @DeleteMapping(value = "{shelterId}")
    @Transactional(rollbackFor = Exception.class)
    public ResultVo deleteMenu(@PathVariable(value = "shelterId")String shelterId){
        if ("".equals(shelterId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        int delete = shelterService.getBaseMapper().deleteById(shelterId);
        if (delete < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return new ResultVo();
    }

    /**
     * 超级管理员更改方舱
     * @param shelters
     * @return
     */
    @ApiOperation("超级管理员更改方舱")
    @PatchMapping(value = "shelter")
    public ResultVo updateArea(@ApiParam("方舱信息")@RequestBody Shelters shelters){
        shelterService.updateShelter(shelters);
        return new ResultVo();
    }
}
