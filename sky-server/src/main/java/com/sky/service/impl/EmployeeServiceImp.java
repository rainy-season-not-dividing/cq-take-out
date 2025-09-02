package com.sky.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeAddDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePasswordModifyDTO;
import com.sky.dto.EmployeePageDTO;
import com.sky.entity.EmployeePO;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImp implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public void updatePassword(EmployeePasswordModifyDTO employeePasswordModifyDTO) {
        EmployeePO employeePO = employeeMapper.getById(Long.valueOf(employeePasswordModifyDTO.getEmpId()));
        //1、账号不存在
        if (employeePO == null){
            throw new AccountNotFoundException("账号不存在");
        }
        //2、密码错误【输入密码加密后是否和数据库的密码相等】
        if(!employeePO.getPassword().equals(DigestUtils.md5DigestAsHex(employeePasswordModifyDTO.getOldPassword().getBytes()))){
            throw new PasswordErrorException("密码错误");
        }
        employeePasswordModifyDTO.setNewPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.updatePassword(employeePasswordModifyDTO);
    }

    @Override
    public PageResult<EmployeePO> page(EmployeePageDTO employeePageDTO) {
        // 通过PageHelper进行分页，得到的对象是List<EmployeePO>
//        PageHelper.startPage(employeePageDTO.getPage(),employeePageDTO.getPageSize());
        // 一条sql进行分页，自动加入limit关键字分页，Page类会自动执行两条sql语句，一个是自动加上limit的，另一个是查总记录数的，由getTotal()函数得到
        Page<EmployeePO> page = employeeMapper.pageQuery(employeePageDTO);
        //封装成PageResult类
        //返回结果
        return new PageResult<>(page.getTotal(), page.getRecords());

    }

    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        // 获取员工信息并返回
        EmployeePO employeePO = employeeMapper.getByName(username);
        //1、账号不存在
        if (employeePO == null){
             throw new AccountNotFoundException("账号不存在");
        }
        //2、密码错误【输入密码加密后是否和数据库的密码相等】
        if(!employeePO.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
            throw new PasswordErrorException("密码错误");
        }
        EmployeeLoginVO employeeLoginVo = new EmployeeLoginVO();
        BeanUtils.copyProperties(employeePO, employeeLoginVo);
        return employeeLoginVo;
    }

    @Override
    public void add(EmployeeAddDTO employeeAddDTO) {
        EmployeePO employeePO = new EmployeePO();
        BeanUtils.copyProperties(employeeAddDTO, employeePO);
        // 这里不用考虑id的问题，一般不会指定，而是通过数据库的自增进行操作
        //密码加密
        employeePO.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeePO.setStatus(StatusConstant.DISABLE); //采用状态常量，避免硬编码，便于维护
//        employeePO.setCreateTime(String.valueOf(LocalDateTime.now()));
//        employeePO.setUpdateTime(String.valueOf(LocalDateTime.now()));
//        employeePO.setCreateUser(BaseContext.getCurrentId());
//        employeePO.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employeePO);
    }

    @Override
    public EmployeePO getById(Long id) {
        return employeeMapper.getById(id);
    }

    @Override
    public void updateEmp(EmployeeAddDTO employeeAddDTO) {
        // 可以直接修改，因为是根据已有员工来进行修改的
        EmployeePO employeePO = employeeMapper.getById(Long.valueOf(employeeAddDTO.getId()));
        employeePO.setIdNumber(employeeAddDTO.getIdNumber());
        employeePO.setName(employeeAddDTO.getName());
        employeePO.setPhone(employeeAddDTO.getPhone());
        employeePO.setSex(employeeAddDTO.getSex());
        employeePO.setUsername(employeeAddDTO.getUsername());
//        employeePO.setUpdateTime(String.valueOf(LocalDateTime.now()));
//        employeePO.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateById(employeePO);
    }

    @Override
    public void modifyStatus(Integer status, Integer id) {
        employeeMapper.updateStatus(status,id);
    }
}
