package com.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.UserInfo;
import com.repository.UserInfoRepository;
import com.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService{
	    @Resource  
	    private UserInfoRepository userInfoRepository;  
	     
	    @Override  
	    public UserInfo findByUsername(String username) {  
	       System.out.println("UserInfoServiceImpl.findByUsername()");  
	       return userInfoRepository.findByUsername(username);  
	    }  
}
