package com.flycms.interceptor;

import java.util.List;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flycms.core.utils.*;
import com.flycms.module.admin.model.Admin;
import com.flycms.module.admin.model.Permission;
import com.flycms.module.admin.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


 
/**
 * 
 * 开发公司：开源之家<br/>
 * 版权：28844.com<br/>
 * <p>
 * 
 * 权限拦截器
 * 
 * <p>
 * 
 * 区分　责任人　日期　　　　说明<br/>
 * 创建　孙开飞　2017年5月26日 　<br/>
 * <p>
 * *******
 * <p>
 * @author sunkaifei
 * @email  admin@28844.com
 * @version 1.0,2017年5月26日 <br/>
 * 
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
	private static Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);
	@Autowired
	private PermissionService permissionService;
	/*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
            Exception arg3) throws Exception {
        // TODO Auto-generated method stub
 
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
            ModelAndView arg3) throws Exception {
        // TODO Auto-generated method stub
 
    }
 
    /*
     * (non-Javadoc)
     * 拦截mvc.xml配置的/member/**路径的请求
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        //请求的路径
        String contextPath=request.getServletPath();
        String  url = request.getScheme() +"://" + request.getServerName() + ":" +request.getServerPort()  + request.getServletPath();
        if (request.getQueryString() != null){
        	url += "?" + request.getQueryString();
        }
		Admin admin = AdminSessionUtils.getLoginMember(request);
        //这里可以根据session的用户来判断角色的权限，根据权限来重定向不同的页面
        if(null != admin || isLoginRequest(request, response)){// && isEnabled()
        	List<Permission> permissions = permissionService.findPermissionByUserId(admin.getId());
			for (Permission permission : permissions) {
				if(CheckUrlUtils.match(permission.getActionKey(), contextPath)) {
					return Boolean.TRUE;
				}
			}
			//没有权限情况下转到无权限403页面
			response.sendRedirect("/403");
			return Boolean.FALSE ;
        }
        // ajax请求
    	if (FilterUtils.isAjax(request)) {
			Map<String,String> resultMap = new HashMap<String, String>();
			logger.debug("当前用户没有登录，并且是Ajax请求！",getClass());
			resultMap.put("login_status", "300");
			resultMap.put("message", "\u5F53\u524D\u7528\u6237\u6743\u9650\u4E0D\u8DB3\u6216\u8005\u6CA1\u6709\u767B\u5F55\uFF0C\u8BF7\u8054\u7CFB\u7BA1\u7406\u5458\uFF01");//当前用户没有登录！
			FilterUtils.out(response, resultMap);
			return Boolean.FALSE ;
		}else{
			if(!"".equals(url) && url!=null){
				response.sendRedirect("/system/login?redirectUrl=" + URLEncoder.encode(url,"UTF-8"));
				return Boolean.FALSE ;
			}
		}
    	//未登录状态下返回到登录页面
    	response.sendRedirect("/system/login");
    	return Boolean.FALSE ;
    }

	private boolean isLoginRequest(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return false;
	}
}