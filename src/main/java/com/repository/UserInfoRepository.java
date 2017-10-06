package com.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.entity.UserInfo;

public interface UserInfoRepository extends CrudRepository<UserInfo,Long> {
    /**通过username查找用户信息;*/  
    public UserInfo findByUsername(String username);  
}
