package com.primeton.manageProvider.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.primeton.commom.exception.OperationException;
import com.primeton.commom.pojo.Area;
import com.primeton.commom.pojo.User;
import com.primeton.commom.pojo.UserRoleAreaShelters;
import com.primeton.commom.vo.CodeMsg;

import com.primeton.manageProvider.mapper.AreaMapper;
import com.primeton.manageProvider.mapper.UserMapper;
import com.primeton.manageProvider.mapper.UserRoleAreaSheltersMapper;
import com.primeton.manageProvider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private UserRoleAreaSheltersMapper userRoleAreaSheltersMapper;

    /**
     * 此方法是查询某个级别的区域或是那个具体区域的合条件的管理者
     * @param areaLevel 判断查询的是哪一行政规划的管理者，比如是街道管理者还是行政区管理者
     * @param userName 管理者姓名
     * @param account 管理者账户
     * @param areaId 所属的区域的ID
     * @param beginDate 注册时间大于此时间
     * @param endDate 注册时间小于此时间
     * @return 返回带分页数据以及符合查询条件的用户
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<User> findUsersInArea(Integer pageIndex,Integer pageSize, Integer areaLevel, String userName, String account, String areaId, Date beginDate, Date endDate) {
        PageHelper.startPage(pageIndex,pageSize);
        List<User> userList = userMapper.findUsersInArea(areaLevel, userName, account, areaId, beginDate, endDate);

        if (userList != null && userList.size() != 0){
            for (User user: userList) {
                List<Area> areas = areaMapper.getAreaByUserId(user.getUserId());
                user.setAreaList(areas);
            }
        }
        return new PageInfo<>(userList);
    }

    /**
     * 主键查询
     * @param userId 用户主键
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserById(String userId) {
        User user = userMapper.findAreaUserById(userId);
        if (user == null) {
            user = userMapper.findSheltersUserById(userId);
        }
        return user;
    }


    /**
     * 新增 用户
     * @param user 用户信息
     * @param areaId 对应区域ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(User user, String areaId) {
        String userId = UUID.randomUUID().toString().replace("-","");
        user.setUserId(userId);
        user.setRegisterDate(new Date());
        user.setIsDelete(0);
        String roleId = user.getRoleId();
        int res = userMapper.insert(user);
        if(res <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        UserRoleAreaShelters uras = new UserRoleAreaShelters(UUID.randomUUID().toString().replace("-",""),roleId,areaId,0,userId,1);
        int insert = userRoleAreaSheltersMapper.insert(uras);
        if(insert <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return ;
    }


    /**
     * 更改用户信息 包括其对应的区域以及角色
     * (因为是批量修改，而不能确定管理员原来负责的方舱ID，此方法选择先删除后新增来达成更新的效果)
     * @param user 新的用户信息，包括ID
     * @param areaIdList 改后区域ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user, List<String> areaIdList) {
        String roleId = user.getRoleId();
        int updateUser = userMapper.updateById(user);
        if(updateUser < 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        updateUser =0;
        UpdateWrapper<UserRoleAreaShelters> wrapper = new UpdateWrapper<>();
        wrapper.eq("participantId",user.getUserId());
        wrapper.eq("resourceType",0);
        int delete = userRoleAreaSheltersMapper.delete(wrapper);
        if (delete > 0){
            for (String areaId: areaIdList) {
                String urasId = UUID.randomUUID().toString().replace("-", "");
                updateUser += userRoleAreaSheltersMapper.insert(new UserRoleAreaShelters(urasId,roleId,areaId,0, user.getUserId(), 1));
            }
        }else{
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        if(updateUser != areaIdList.size()){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
    }

    /**
     * 登录
     * @param account 账户
     */
    @Override
    @Transactional(readOnly = true)
    public User findByAccount(String account) {
        return userMapper.findByAccount(account);
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
    @Override
    @Transactional(readOnly = true)
    public PageInfo<User> findSheltersUser(Integer pageIndex,Integer pageSize,String userName, String account, String sheltersId, Date beginDate, Date endDate) {
        PageHelper.startPage(pageIndex,pageSize);
        List<User> list = userMapper.findSheltersUser(userName,account,sheltersId,beginDate,endDate);
        return new PageInfo<>(list);
    }


    /**
     * 新增方舱管理者 以及添加其对应关系
     * @param sheltersId 方舱ID
     * @param user 方舱管理者的信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSheltersUser(String sheltersId, User user) {
        String roleId = user.getRoleId();
        String userId = UUID.randomUUID().toString().replace("-","");
        user.setUserId(userId);
        user.setRegisterDate(new Date());
        user.setIsDelete(0);
        int insert = userMapper.insert(user);
        if(insert <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        UserRoleAreaShelters uras = new UserRoleAreaShelters(UUID.randomUUID().toString().replace("-",""),roleId,sheltersId,1,userId,1);
        int insertMid = userRoleAreaSheltersMapper.insert(uras);
        if(insertMid <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        return ;
    }




    /**
     * 修改方舱管理者的信息，以及更改其对应的方舱和角色
     * (因为是批量修改，而不能确定管理员原来负责的方舱ID，此方法选择先删除后新增来达成更新的效果)
     * @param sheltersIdList 管理者负责的方舱 ID
     * @param roleId 角色ID
     * @param user 用户的基本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSheltersUser(List<String> sheltersIdList, String roleId, User user) {
        int updateUser = userMapper.updateById(user);
        if(updateUser != 1){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }

        UpdateWrapper<UserRoleAreaShelters> wrapper = new UpdateWrapper<>();
        wrapper.eq("participantId",user.getUserId());
        wrapper.eq("resourceType",1);
        int delete = userRoleAreaSheltersMapper.delete(wrapper);
        if(delete <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }else{
            for (String areaId: sheltersIdList) {
                String urasId = UUID.randomUUID().toString().replace("-", "");
                updateUser += userRoleAreaSheltersMapper.insert(new UserRoleAreaShelters(urasId,roleId,areaId,1, user.getUserId(), 0));
            }
        }
        if(updateUser < sheltersIdList.size()){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return;
    }

    /**
     * 根据userId查找角色
     * @param userId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserRoleById(String userId) {
        return userMapper.findUserRoleById(userId);
    }

    /**
     * 根据roleId分页查找用户
     * @param pageIndex
     * @param pageSize
     * @param roleId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<User> findUserByRoleId(Integer pageIndex, Integer pageSize, String roleId) {
        PageHelper.startPage(pageIndex,pageSize);
        HashMap<String, Object> map = new HashMap<>();
        map.put("roleId",roleId);
        List<User> users = userMapper.selectByMap(map);
        return new PageInfo<>(users);
    }

    /**
     * 根据userId删除用户信息
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        User user = new User();
        user.setIsDelete(1);
        user.setUserId(userId);
        int result = userMapper.updateById(user);
        if(result <= 0){
            throw new OperationException(CodeMsg.OPERATION_FAIL);
        }
        return ;
    }


}
