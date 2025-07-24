package com.sky.service;

import com.sky.dto.EmployeeAddDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePasswordModifyDTO;
import com.sky.dto.EmployeePageDTO;
import com.sky.entity.EmployeePO;
import com.sky.result.PageResult;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService {

    //修改密码
    void updatePassword(EmployeePasswordModifyDTO employeePasswordModifyDTO);

    //（根据姓名）分页查询
    PageResult<EmployeePO> page(EmployeePageDTO employeePageDTO);

    //员工登录
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    //新增员工
    void add(EmployeeAddDTO employeeAddDTO);

    //根据id查询员工
    EmployeePO getById(Long id);

    //修改员工信息
    void updateEmp(EmployeeAddDTO employeeAddDTO);

    // 修改员工账号状态
    void modifyStatus(Integer status, Integer id);
}
