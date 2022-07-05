package com.primeton.manageProvider.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("test")
@Api(tags = "test")
public class TestController {

//    @ApiOperation("添加用户")
//    @PostMapping("add")
////    public Try add(@ApiParam("用户名")@RequestParam("userName")String userName,
////                    @ApiParam("密码")@RequestParam("password")String password){
////        return new Try(userName,password);
//    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public String update(){
        return "修改";
    }




}
