package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePasswordModifyDTO;
import com.sky.dto.EmployeePageDTO;
import com.sky.entity.EmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {
    // 根据id和密码修改密码
    @Update("update employee set password=#{newPassword} where id=#{empId}")
    void updatePassword(EmployeePasswordModifyDTO employeePasswordModifyDTO);

    // 分页查询
    Page<EmployeePO> pageQuery(EmployeePageDTO employeePageDTO);

    // 根据用户名查询员工
    @Select("select * from employee where username=#{username}")
    EmployeePO getByName(String username);

    // 添加员工
    void insert(EmployeePO employeePO);

    // 根据id查询员工
    @Select("select * from employee where id = #{id}")
    EmployeePO getById(Long id);

    // 修改员工  ：修改时要思考，是否有可能传过来的对象中，存在某些字段为null或者‘’，那么这些字段应该如何处理？
    @Update("update employee set id_number=#{idNumber}, name=#{name}, phone=#{phone}, sex=#{sex}, username=#{username} where id=#{id}")
    void updateById(EmployeePO employeePO);

    // 修改员工状态
    @Update("update employee set status=#{status} where id=#{id}")
    void updateStatus(Integer status, Integer id);
}
