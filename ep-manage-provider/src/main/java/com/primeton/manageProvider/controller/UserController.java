package com.primeton.manageProvider.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.config.UserPasswordEncoder;
import com.primeton.commom.exception.*;
import com.primeton.commom.pojo.Area;
import com.primeton.commom.pojo.User;
import com.primeton.commom.vo.CodeMsg;
import com.primeton.commom.vo.ResultVo;

import com.primeton.manageProvider.service.IAreaService;
import com.primeton.manageProvider.service.UserService;
import com.primeton.manageProvider.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private IAreaService areaService;


    @Resource
    private UserPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录并存入session中
     * @param account 账户
     * @param userPassword 密码
     * @return 用户
     */
    @ApiOperation("登录")
    @PostMapping( value = "login")
    public ResultVo login(@ApiParam("账号") @RequestParam(required = true) String account,
                          @ApiParam("密码") @RequestParam(required = true) String userPassword,
                          HttpServletResponse res){
        if(account == null || userPassword == null){
            throw new IllegalArgException(CodeMsg.NULL_ARG);
        }
        res.setCharacterEncoding("utf-8");
        res.setContentType("text/html;charset=UTF-8");

        User user = userService.findByAccount(account);
        if(user == null){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        boolean matches = passwordEncoder.matches(userPassword, user.getUserPassword());
        if (!matches){
            throw new UserNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
        String token = TokenUtil.setToken(redisTemplate, user);
        return new ResultVo(token);
    }


    /**
     * 用户登出方法，同时删除了redis中的对应用户的 token
     * @param request
     * @return
     */
    @ApiOperation("登出")
    @GetMapping(value = "logout")
    public ResultVo logout(HttpServletRequest request){
        String token = request.getHeader("token");
        Boolean hasKey = redisTemplate.hasKey(token);
        if(!hasKey){
            throw new IllegalTokenException(CodeMsg.TOKEN_OVERTIME);
        }
        Boolean destroy = TokenUtil.destroy(redisTemplate,token);
        if(destroy){
            return new ResultVo();
        }else{
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 根据token查询的 对应 用户的信息（包含角色信息）
     * @param request
     * @return
     */
    @ApiOperation("获取当前登录的用户信息（包括角色信息）")
    @GetMapping("role")
    public ResultVo getUserRole(HttpServletRequest request){
        String token = request.getHeader("token");
        String userString =(String) redisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(userString, User.class);
        if (user != null){
            user = userService.findUserRoleById(user.getUserId());
            if(user!= null){
                return new ResultVo(user);
            }else{
                throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
            }
        }else{
            throw new DataNotFoundException(CodeMsg.USER_NOT_FOUND);
        }
    }

    /**
     * 根据以登入的用户信息查询 其对应负责的区域（树形）
     * @param request
     * @return
     */
    @ApiOperation("查询登入用户管理区域")
    @GetMapping(value = "area")
    public ResultVo getAreaListByUser(HttpServletRequest request){
        String token = request.getHeader("token");
        String userString =(String) redisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(userString, User.class);
        user = userService.findUserById(user.getUserId());
        ArrayList<String> idList = new ArrayList<>();
        if (user.getAreaList() == null || user.getAreaList().size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        user.getAreaList().forEach(area -> idList.add(area.getAreaId()));
        if(idList == null || idList.size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        List<Area> list = areaService.findChildAreaListByIdList(idList);
        if(list == null || list.size() == 0){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }
        return new ResultVo(list);
    }

    @ApiOperation("查询登入用户管理方舱")
    @GetMapping(value = "shelter")
    public ResultVo getShelterByUser(HttpServletRequest req){
        String token = req.getHeader("token");
        String userString =(String) redisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(userString, User.class);
        user = userService.findUserById(user.getUserId());
        if(user.getShelters() == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(user.getShelters().getSheltersId());
        }
    }


    /**
     * 新增用户，以及加上与区域和角色的关联
     * @param user 用户信息
     * @param areaId 对应的区域ID
     * @return
     */
    @ApiOperation("新增区域管理员")
    @PostMapping(value = "area/{areaId}")
    public ResultVo insertUser(@ApiParam("用户信息")@RequestBody User user,
                               @ApiParam("用户管理的区域id") @PathVariable String areaId){

        String password = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(password);
        userService.insertUser(user,areaId);
        return new ResultVo();
    }

    /**
     * 新增方舱管理者 以及添加其对应关系
     * @param sheltersId 方舱ID
     * @param user 方舱管理者的信息
     * @return
     */
    @ApiOperation("新增方舱管理员")
    @PostMapping(value = "shelters/prison/{sheltersId}")
    public ResultVo insertShelterUser(@ApiParam("用户管理的方舱id") @PathVariable("sheltersId") String sheltersId,
                                      @ApiParam("用户信息") @RequestBody User user){
        String password = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(password);
        userService.insertSheltersUser(sheltersId,user);
        return new ResultVo();
    }

    /**
     * 根据userId删除用户信息
     * @param userId
     * @return
     */
    //todo 批量删除 逗号分割的字符串转为list
    @ApiOperation("删除用户信息")
    @DeleteMapping(value = "user/{userId}")
    public ResultVo deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return new ResultVo();
    }

    /**
     * 更改用户信息 包括其对应的区域以及角色
     * @param user 新的用户信息，包括ID
     * @param areaIdList 改后区域ID
     * @return
     */
    @ApiOperation("修改区域管理员信息")
    @PutMapping(value = "user/jail")
    public ResultVo updateUser(@RequestBody User user,
                               @ApiParam("角色管理的区域列表")@RequestParam List<String> areaIdList){
        String password = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(password);
        userService.updateUser(user,areaIdList);
        return new ResultVo();
    }

    /**
     * 修改方舱管理者的信息，以及更改其对应的方舱和角色
     * @param sheltersIdList 管理者负责的方舱 ID
     * @param roleId 角色ID
     * @param user 用户的基本信息
     * @return
     */
    @ApiOperation("修改方舱管理者信息")
    @PutMapping(value = "shelters/{roleId}")
    public ResultVo updateShelterUsers(@RequestParam("sheltersId") List<String> sheltersIdList,
                                       @PathVariable("roleId") String roleId,
                                       @RequestBody User user){
        String password = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(password);
        try{
            int id = Integer.parseInt(roleId);
            if(id < 0 || id > 5){
                throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
            }
        }catch (NumberFormatException e){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }

        userService.updateSheltersUser(sheltersIdList,roleId,user);
        return new ResultVo();
    }




    /**
     * 根据主键查询区域或方舱的用户信息
     * @param userId 用户主键
     * @return
     */
    @ApiOperation("根据主键查询区域或方舱的用户信息")
    @GetMapping(value = "{userId}")
    public ResultVo findUserById(@PathVariable("userId") String userId){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(user);
        }
    }


    /**
     * 此方法是查询某个级别的区域或是哪个具体区域的合条件的管理者
     * @param areaLevel 判断查询的是哪一行政规划的管理者，比如是街道管理者还是行政区管理者
     * @param userName 管理者姓名
     * @param account 管理者账户
     * @param areaId 所属的区域的ID
     * @param beginDate 注册时间大于此时间
     * @param endDate 注册时间小于此时间
     * @return
     */
    @ApiOperation("查询某个级别的区域或是哪个具体区域的合条件的管理者")
    @GetMapping(value = "area/{areaLevel}")
    public ResultVo findUserInArea(@ApiParam("行政规划等级") @PathVariable("areaLevel") Integer areaLevel,
                                   @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                   @RequestParam(value = "userName",required = false) String userName,
                                   @RequestParam(value = "account",required = false) String account,
                                   @RequestParam(value = "areaId",required = false) String areaId,
                                   @ApiParam("注册时间大于此时间") @RequestParam(value = "beginDate",required = false)  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date beginDate,
                                   @ApiParam("注册时间小于此时间") @RequestParam(value = "endDate",required = false)  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate){

        if(areaLevel <= 0 || areaLevel > 3){
            throw new IllegalArgException(CodeMsg.ILLEGAL_ARG);
        }
        PageInfo<User> pageInfo =
                userService.findUsersInArea(pageIndex,pageSize,areaLevel,userName,account,areaId,beginDate,endDate);
        if(pageInfo == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(pageInfo);
        }
    }


    /**
     * 条件查询方舱管理员列表
     * @param pageIndex 页码
     * @param pageSize 每页展示条数
     * @param userName 方舱管理者 模糊名
     * @param account 方舱管理者账号
     * @param sheltersId 对应方舱ID
     * @param beginDate 注册时间大于此时间
     * @param endDate 注册时间小于此时间
     * @return
     */
    @ApiOperation("条件查询方舱管理员列表")
    @GetMapping(value = "shelters")
    public ResultVo findSheltersUser(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                     @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                     @RequestParam(value = "userName",required = false) String userName,
                                     @RequestParam(value = "account",required = false) String account,
                                     @RequestParam(value = "sheltersId",required = false) String sheltersId,
                                     @RequestParam(value = "beginDate",required = false) @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date beginDate,
                                     @RequestParam(value = "endDate",required = false) @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate){
        PageInfo<User> pageInfo =
                userService.findSheltersUser(pageIndex,pageSize,userName,account,sheltersId,beginDate,endDate);
        if(pageInfo == null){
            throw new DataNotFoundException(CodeMsg.DATA_NOT_FOUND);
        }else{
            return new ResultVo(pageInfo);
        }
    }



}
