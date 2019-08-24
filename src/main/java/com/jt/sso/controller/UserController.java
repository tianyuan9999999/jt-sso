package com.jt.sso.controller;

import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.jt.common.po.User;
import com.jt.common.vo.SysResult;
import com.jt.sso.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	private JedisCluster JedisCluster;
	
	
	@RequestMapping("/cheak/{param}/{type}")
	@ResponseBody
	public MappingJacksonValue findCheakUser(@PathVariable String param,@PathVariable Integer type,String callback){
		
		boolean flag=userService.findCheckUser(param, type);
		MappingJacksonValue value=new MappingJacksonValue(SysResult.oK(flag));
		
		value.setJsonpFunction(callback);
		return value;
		
	}
	
	@RequestMapping("/register")
	@ResponseBody
	public SysResult findUserByUP(User user){
		try {
			String token=userService.findUserByUP(user);
			if(StringUtils.isEmpty(token)){
				throw new RuntimeException();
			}
			return SysResult.oK(token);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return SysResult.build(201,"ÓÃ»§µÇÂ½Ê§°Ü");
	}
	
	@RequestMapping("/query/{token}")
	@ResponseBody
	public MappingJacksonValue findUserByToken(@PathVariable String token,String callback){
		String userJSON=JedisCluster.get(token);
		MappingJacksonValue jacksonValue=null;
		if(StringUtils.isEmpty(userJSON)){
			jacksonValue=new MappingJacksonValue(SysResult.build(201,"²éÑ¯Ê§°Ü"));
		}else{
			jacksonValue=new MappingJacksonValue(SysResult.oK(userJSON));
		}
		jacksonValue.setJsonpFunction(callback);
		
		
		return jacksonValue;
		
		
	}
	
	
}
