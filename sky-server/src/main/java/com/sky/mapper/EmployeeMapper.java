package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePasswordModifyDTO;
import com.sky.dto.EmployeePageDTO;
import com.sky.entity.EmployeePO;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeePO> {
    // 根据id和密码修改密码
    @AutoFill(OperationType.UPDATE)
    @Update("update employee set password=#{newPassword},update_time=#{updateTime},update_user= #{updateUser} where id=#{empId}")
    void updatePassword(EmployeePasswordModifyDTO employeePasswordModifyDTO);

    // 分页查询
    Page<EmployeePO> pageQuery(EmployeePageDTO employeePageDTO);

    // 根据用户名查询员工
    @Select("select * from employee where username=#{username}")
    EmployeePO getByName(String username);

    // 添加员工
//    @AutoFill(OperationType.INSERT)
//    void insert(EmployeePO employeePO);

    // 根据id查询员工
//    @Select("select * from employee where id = #{id}")
//    EmployeePO getById(Long id);

    // 修改员工  ：修改时要思考，是否有可能传过来的对象中，存在某些字段为null或者‘’，那么这些字段应该如何处理？
//    @AutoFill(OperationType.UPDATE)
//    @Update("update employee set id_number=#{idNumber}, name=#{name}, phone=#{phone}, sex=#{sex}, username=#{username},update_user=#{updateUser}, update_time=#{updateTime} where id=#{id}")
//    void updateById(EmployeePO employeePO);

    // 修改员工状态
    @Update("update employee set status=#{status} where id=#{id}")
    void updateStatus(Integer status, Integer id);
}
