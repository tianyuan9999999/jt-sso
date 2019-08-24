package com.jt.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.po.User;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.service.UserService;

import redis.clients.jedis.JedisCluster;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper userMapper;
	private JedisCluster JedisCluster;
	private ObjectMapper objectMapper=new ObjectMapper();
	
	@Override
	public boolean findCheckUser(String param, Integer type) {
		User user=new User();
		switch(type){
		case 1:
			user.setUsername(param);
			break;
		case 2:
			user.setPhone(param);
			break;
		case 3:
			user.setEmail(param);
			break;
		}
		int count=userMapper.selectCount(user);
		
		return count==0?false:true;
	}

	@Override
	public void saveUser(User user) {
		user.setEmail(user.getPhone());
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		userMapper.insert(user);
		
	}

	@Override
	public String findUserByUP(User user) {
	
		List<User> userList=userMapper.select(user);
		if(userList.size()==0){
			return null;
		}
		User userDB=userList.get(0);
		String token=DigestUtils.md5Hex("JT_TICKET"+System.currentTimeMillis()+user.getUsername());
		
		try {
			String userJSON=objectMapper.writeValueAsString(userDB);
			JedisCluster.setex(token,3600*24*7,userJSON);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return token;
	}

}
