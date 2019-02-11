package com.spring.sys.service;



import com.spring.sys.pojo.SysLogInfo;

import java.util.List;
import java.util.Map;

/**
 * 系统日志
 * @author bigcatpan
 * @since V1.0
 * @date 2018/4/10 19:56
 */
public interface SysLogService {

	List<SysLogInfo> queryList(Map<String, Object> map);
	
	void save(SysLogInfo sysLog);
	
}
