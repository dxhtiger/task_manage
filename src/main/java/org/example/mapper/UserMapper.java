package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Users;

import java.util.List;

@Mapper
public interface UserMapper {

    // 查询所有未删除用户
    @Select("SELECT * FROM users WHERE is_deleted = 0")
    List<Users> selectAll();

    // 根据ID查询
    @Select("SELECT * FROM users WHERE id = #{id} AND is_deleted = 0")
    Users selectById(Long id);

    // 根据用户名查询
    @Select("SELECT * FROM users WHERE username = #{username} AND is_deleted = 0")
    Users selectByUsername(String username);

    // 插入用户
    @Insert("INSERT INTO users(username, password, email, phone, role, created_at, updated_at, is_deleted) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(Users user);

    // 更新用户信息（根据 id）
    int updateUser(Users user);

    // 修改密码
    @Update("UPDATE users SET password=#{password}, updated_at=NOW() WHERE id=#{id} AND is_deleted=0")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    // 逻辑删除用户
    @Update("UPDATE users SET is_deleted=1, updated_at=NOW() WHERE id=#{id}")
    int deleteById(Long id);

    //根据邮箱查找用户
    @Select("SELECT * FROM users WHERE email = #{email}")
    Users selectByEmail(String email);
}
