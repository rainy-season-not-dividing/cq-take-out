package com.sky.controller.admin;

import com.sky.constant.JwtConstant;
import com.sky.dto.EmployeeAddDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePasswordModifyDTO;
import com.sky.dto.EmployeePageDTO;
import com.sky.entity.EmployeePO;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    // 修改员工密码
    @PutMapping("/admin/employee/editPassword")
    public Result<String> updatePassword(@RequestBody EmployeePasswordModifyDTO employeePasswordModifyDTO){
        log.info("密码修改：{}",employeePasswordModifyDTO);
        employeeService.updatePassword(employeePasswordModifyDTO);
        return Result.success();
    }

    //分页查询
    @GetMapping("/admin/employee/page")
    public Result<PageResult> page(EmployeePageDTO employeePageDTO){
        PageResult<EmployeePO> pageResult = employeeService.page(employeePageDTO);
        return Result.success(pageResult);
    }

    //员工登录
    @PostMapping("/admin/employee/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);

        //登录成功后生成JWT令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtConstant.EmpId,employeeLoginVO.getId());
        String token = JwtUtil.createJwt(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        employeeLoginVO.setToken(token);

        return Result.success(employeeLoginVO);
    }

    // 新增员工
    @PostMapping("/admin/employee")
    public Result<Object> save(@RequestBody EmployeeAddDTO employeeAddDTO){
        employeeService.add(employeeAddDTO);
        return Result.success();
    }

    // 根据id查询员工信息
    @GetMapping("/admin/employee/{id}")
    public Result<EmployeePO> getById(@PathVariable Long id){
        EmployeePO employeePO = employeeService.getById(id);
        return Result.success(employeePO);
    }

    // 编辑员工信息
    @PutMapping("/admin/employee")
    public Result updateEmp(@RequestBody EmployeeAddDTO employeeAddDTO){
        employeeService.updateEmp(employeeAddDTO);
        return Result.success();
    }

    // 修改员工账号状态
    @PostMapping("/admin/employee/status/{status}")
    public Result<String> modifyStatus(@PathVariable Integer status, Integer id){
        employeeService.modifyStatus(status,id);
        return Result.success();
    }

    // 退出登录
    @PostMapping("/admin/employee/logout")
    public Result<String> logout(){
        return Result.success();
    }

}
