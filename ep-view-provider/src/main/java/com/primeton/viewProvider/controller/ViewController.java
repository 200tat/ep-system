package com.primeton.viewProvider.controller;



import com.primeton.commom.exception.DataNotFoundException;
import com.primeton.commom.exception.IllegalArgException;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.View;
import com.primeton.commom.util.DateDealUtil;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;
import com.primeton.viewProvider.service.ViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;




@RestController
@RequestMapping("view")
@Api(tags = "疫情分布图")
public class ViewController {

    @Autowired
    private ViewService viewService;

    /**
     * 查询当日的所有的区域的疫情情况
     * @return
     */
    @GetMapping("today")
    @ApiOperation("查询当日的所有的区域的疫情情况")
    public ResultVo getPresentView(){
        List<View> viewList =  viewService.selectTodayViews();
        return new ResultVo(viewList);
    }
    /**
     * 查询当日的所有的区域的疫情情况
     * @return
     */
    @PostMapping("update")
    @ApiOperation("查询当日的所有的区域的疫情情况")
    public ResultVo updateView(){
        viewService.updateCommunityViewDaily();
        return new ResultVo();
    }




    /**
     * 根据区域Id查询 此区域以及其子区域的疫情情况 （每日）
     * @param areaId
     * @param outstanding
     * @return
     */
    @ApiOperation("根据区域Id查询此区域以及其子区域的疫情情况（每日）")
    @GetMapping("epidemic")
    public ResultVo getAreaEpidemic(@RequestParam(value = "areaId",required = false) String areaId,
                                    @ApiParam("是否上榜") @RequestParam(value = "outstanding", defaultValue = "false") boolean outstanding){
        Date date = DateDealUtil.paresDate(new Date());
        List<View> list = viewService.getAreaEpidemic(areaId,outstanding,date);
        try {
            if (list == null || list.isEmpty()) {
                date = DateDealUtil.getDayBefore(1,date);
                list = viewService.getAreaEpidemic(areaId, outstanding, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return new ResultVo(200,"最近更新数据",null,list);
    }

    /**
     * 获取全部区域前day天的新增阳性、治愈、死亡数据
     * @param day
     * @return
     */
    @ApiOperation("获取全部区域前day天的新增阳性、治愈、死亡数据")
    @GetMapping("info")
    public ResultVo getInDeCuCount(@ApiParam("获取前day天数据") @RequestParam(value = "day",defaultValue = "7")Integer day){
        if(day < 1){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        Map<String, Object> map = viewService.getInDeCuCount(day);
        if(map==null || map.size()==0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(map);
    }


    @ApiOperation("区域几日内转运人数")
    @GetMapping(value = "transferCount/{areaId}/{day}")
    public ResultVo getTransferCount(@PathVariable(value = "areaId")String areaId,
                                     @PathVariable(value = "day") Integer day){
        //判断必要参数不为空
        if("".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        //判断必要参数合理取值
        if(day < 1){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        Map<Date,Integer> resultMap =  viewService.getTransferCount(areaId,day);

        //判断查询是否为空
        if(resultMap.size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(resultMap);

    }

    @ApiOperation("区域几日的遗留人数")
    @GetMapping(value = "stayCount/{areaId}/{day}")
    public ResultVo getStayCount(@PathVariable(value = "areaId") String areaId,
                                 @PathVariable(value = "day") Integer day){
        //判断必要参数不为空
        if("".equals(areaId)){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }

        //判断必要参数合理取值
        if(day < 1){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        Map<Date,Integer> resultMap =  viewService.getStayCount(areaId,day);

        //判断查询是否为空
        if(resultMap.size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(resultMap);

    }

    @ApiOperation("获取直接子区域idList")
    @GetMapping("{areaId}/children")
    public ResultVo getChildrenId(@ApiParam("父id")@PathVariable String areaId){
        List<String> childrenId = viewService.getChildrenId(areaId);
        if (childrenId==null || childrenId.size()==0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(childrenId);
    }

    @ApiOperation("根据id查询day日信息")
    @GetMapping("{areaId}/info")
    public ResultVo getAreaInfo(@ApiParam("区域id")@PathVariable String areaId,
                                @ApiParam("前几天")@RequestParam Integer day){
        if(day < 1){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        Map<String, Object> map = viewService.getAreaInfo(areaId,day);
        if(map==null || map.size()==0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(map);

    }

}
