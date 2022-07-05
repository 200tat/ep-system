package com.primeton.manageProvider.controller;

import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Area;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.IAreaService;
import com.primeton.manageProvider.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("area")
@Api(tags = "区域")
public class AreaController {

    @Autowired
    private IAreaService areaService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    /**
     * 获得全部区域信息
     * @return
     */
    @ApiOperation("获取全部区域")
    @GetMapping(value = "all")
    public ResultVo getAreaAll(){

        List areaAll = areaService.getAreaAll();

        //判断查询到的数据是否为空
        if(areaAll == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return  new ResultVo<>(areaAll);
    }

    /**
     * 添加区域
     * @param area 区域信息
     * @return
     */
    @ApiOperation("添加区域")
    @PostMapping(value = "area")
    public ResultVo insertArea(@ApiParam("区域信息") @RequestBody Area area){
        //判断必要参数不为空
        if(area == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        Integer result = areaService.insertArea(area);

        //判断添加是否成功
        if(result == null || result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        return new ResultVo();
    }

    /**
     * 删除区域
     * @param areaId
     * @return
     */
    @ApiOperation("删除区域")
    @DeleteMapping(value = "{areaId}")
    public ResultVo deleteMenu(@PathVariable(value = "areaId")String areaId){

        //判断必要参数不为空
        if("".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        Integer result = areaService.getBaseMapper().deleteById(areaId);

        //判断删除是否成功
        if(result == null || result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        return new ResultVo();
    }

    /**
     * 更改区域
     * @param area
     * @return
     */
    //不需要areaId 直接传area
    @ApiOperation(value = "更改区域",notes = "该方法不能更改areaId")
    @PutMapping(value = "areaId")
    public ResultVo updateArea(@ApiParam("区域信息")@RequestBody Area area){

        //判断必要参数不为空
        if(area == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        Integer result = areaService.updateArea(area);

        //判断删除是否成功
        if(result == null || result < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        return new ResultVo();    }

    /**
     * 此方法是获取 区域 的 直接 子区域信息
     * @param parentId 父区域主键
     * @return 结果中带有 此区域 的 子区域集合
     */
    @ApiOperation("获取区域的子区域")
    @GetMapping(value = "{parentId}/child")
    public ResultVo<Area> findChildrenList(@ApiParam("父级区域id") @PathVariable("parentId") String parentId,
                                       @RequestParam(value = "pageIndex",defaultValue = "1",required = false)Integer pageIndex,
                                       @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize,
                                       @RequestParam(value = "needPage") boolean needPage){

        //判断必要参数不为空
        if("".equals(parentId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        PageInfo<Area> pageInfo = areaService.findChildrenList(pageIndex,pageSize,parentId,needPage);

        //判断查询是否为空
        if(pageInfo == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }

        return  new ResultVo(pageInfo);
    }

    /**
     * 查询区域中包含其 多层 父区域信息
     * @param areaId  区域ID
     * @return
     */
    @ApiOperation("获取区域的父区域")
    @GetMapping(value = "{areaId}/parent")
    public ResultVo findParentArea(@ApiParam("当前区域id")@PathVariable("areaId") String areaId){
        //判断必要参数不为空
        if("".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        Area area =  areaService.findParentArea(areaId);

        //判断查询是否为空
        if(area == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(area);
    }



    /**
     * 返回某个区域等级下的所有区域
     * @param areaLevel
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation("返回某个区域等级下的所有区域")
    @GetMapping(value = "{areaLevel}")
    public ResultVo getLevelMenu(@ApiParam("区域等级") @PathVariable(value = "areaLevel")Integer areaLevel,
                                 @RequestParam(value = "pageIndex",defaultValue = "1")Integer pageIndex,
                                 @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                 @RequestParam(value = "needPage") boolean needPage){
        //判断输入参数是否合理
        if(areaLevel < 0 || areaLevel > 3){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }

        PageInfo<Area> pageInfo = areaService.getLevelArea(areaLevel,pageIndex,pageSize,needPage);

        //判断查询是否为空
        if(pageInfo == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(pageInfo);
    }


}
