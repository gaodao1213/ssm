package com.soecode.lyf.web.citic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soecode.lyf.util.HttpKit;
import com.soecode.lyf.web.citic.bean.CiticToken;
import com.soecode.lyf.web.citic.bean.CiticUserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/citic")
public class LoginController {

	private String client_id = "20170830061623854-E5A8-B2FABDC35";
	private String client_secret = "9c25b1c7cb9641bba4cb8ce4960e24ea";
	private String token_name = "access_token";
	private String code_name = "code";
	private String user_id_name = "uid";
	private String response_type = "code";
	private String citic_host = "http://uuapitest.c.citic";
	private String grant_type = "authorization_code";
	private String redirect_uri = "";


	@RequestMapping(value = "/index")
	public ModelAndView index(
			HttpServletRequest request,
			String code,
			String toUrl){
		ModelAndView modelAndView = new ModelAndView();
		if(StringUtils.isEmpty(code)){

			//需要走oauth获取认证码code
			String backUrl = request.getRequestURL().toString();
			if(!StringUtils.isEmpty(backUrl)){
				try {
					backUrl = URLEncoder.encode(backUrl,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if(!StringUtils.isEmpty(toUrl)){
				try {
					toUrl = URLEncoder.encode(toUrl,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			StringBuilder url = new StringBuilder();
			url.append(citic_host).append("/oauthorize/authorize").append("?");
			url.append("client_id=").append(client_id);
			url.append("&response_type=").append(response_type);
			url.append("&state=").append(toUrl);
			url.append("&redirect_uri=").append(backUrl);

			modelAndView.addObject("backUrl",backUrl);
			modelAndView.setViewName("redirect:"+url.toString());
			return modelAndView;
		}else{
			//走获取access_token
			CiticToken token = getAccessToken(code);
			if(token != null){
				CiticUserInfo userInfo = getUserInfo(token.getAccess_token());
				modelAndView.addObject("userInfo",userInfo);
			}
		}
		modelAndView.setViewName("index");
		return modelAndView;
	}

	/**
	 * 获取认证code
	 * @param backUrl
	 * @param state
	 * @return
	 */
	public String getCode(String backUrl,String state){
		String url = citic_host + "/oauthorize/authorize";
		Map paramMap = new HashMap();
		paramMap.put("client_id",client_id);
		paramMap.put("redirect_uri",backUrl);
		paramMap.put("response_type",response_type);
		paramMap.put("state",state);
		String result = HttpKit.sendGet(url,paramMap);
		if(!StringUtils.isEmpty(result)){
			JSONObject jsonObject = JSON.parseObject(result);
			if(jsonObject.containsKey("code")){
				return jsonObject.getString("code");
			}
		}
		return null;
	}

	/**
	 * 获取access_token
	 * @param code
	 * @return
	 */
	public CiticToken getAccessToken(String code){
		String url = citic_host + "/oauthorize/getToken";
		Map paramMap = new HashMap();
		paramMap.put("client_id",client_id);
		paramMap.put("client_secret",client_secret);
		paramMap.put("code",code);
		paramMap.put("grant_type",grant_type);
		String result = HttpKit.sendPost(url,paramMap);
		if(!StringUtils.isEmpty(result)){
			JSONObject jsonObject = JSON.parseObject(result);
			System.out.println("tokenResult:"+jsonObject.toJSONString());
			if(jsonObject.containsKey("access_token")){
				CiticToken token = jsonObject.toJavaObject(CiticToken.class);
				return token;
			}
		}
		return null;
	}

	/**
	 * 刷新token
	 * @param refresh_token
	 * @return
	 */
	public CiticToken refreshAccessToken(String refresh_token){
		String url = citic_host + "/oauthorize/refreshToken";
		Map paramMap = new HashMap();
		paramMap.put("client_id",client_id);
		paramMap.put("client_secret",client_secret);
		paramMap.put("refresh_token",refresh_token);
		paramMap.put("grant_type","refresh_token");
		String result = HttpKit.sendPost(url,paramMap);
		if(!StringUtils.isEmpty(result)){
			JSONObject jsonObject = JSON.parseObject(result);
			System.out.println("refreshAccessToken:"+jsonObject.toJSONString());
			if(jsonObject.containsKey("access_token")){
				CiticToken token = jsonObject.toJavaObject(CiticToken.class);
				return token;
			}
		}
		return null;
	}


	/**
	 * 获取用户信息
	 * @param access_token
	 * @return
	 */
	public CiticUserInfo getUserInfo(String access_token){
		String url = citic_host + "/user/getUserInfo";
		Map paramMap = new HashMap();
		paramMap.put("client_id",client_id);
		paramMap.put("access_token",access_token);
		String result = HttpKit.sendGet(url,paramMap);
		if(!StringUtils.isEmpty(result)){
			JSONObject jsonObject = JSON.parseObject(result);
			System.out.println("userinfo:"+jsonObject.toJSONString());
			if(jsonObject.containsKey("loginName")){
				CiticUserInfo userInfo = jsonObject.toJavaObject(CiticUserInfo.class);
				return userInfo;
			}
		}
		return null;
	}


    public static void main(String[] args){
		LoginController loginController = new LoginController();
//		String code = "c70cbe30b498068d731de81d9c9b2cdf";
//		CiticToken token = loginController.getAccessToken(code);
//		System.out.println("token:"+token.getAccess_token());
//		loginController.getUserInfo(token.getAccess_token());
		CiticToken retoken = loginController.refreshAccessToken("a31a4929ec6831ad60c2b52155fd43d8");
		System.out.println("retoken:"+JSON.toJSONString(retoken));

	}

}
