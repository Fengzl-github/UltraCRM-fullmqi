package com.cn.mqi.jwt;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * 设计验证token的规则
 * @author GuaiWenWo
 *
 */
public class PmJwtToken {

	/**
	 * 合法验证的token规则的秘钥
	 */
	public static String jwtTokenSecret="B15FF867ADE345638E7CD86225668AE7";
	
	/**
	 * 设置新的token的秘钥
	 * 1.每次启动程序token都会发生改变
	 * 2.每天凌晨定时器会触发token改变,最多维持24小时,此项规则暂时应用于 当前论坛程序中,其他程序采用要根据自己实际情况做处理
	 */
	public static void setNewTokenSecret() {
		jwtTokenSecret=UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
	
	/**
	 * 生成唯一TOKEN信息
	 * @param strUid
	 * @param strLoginInfo
	 * @return
	 */
	public static String getJwtToken(String strUid,String strLoginInfo) {
		/* 可设置失效日期的处理
        //设置签名过期时间  5秒
		    Date iatDate=new Date();
	        Calendar nowTime=Calendar.getInstance();
	        nowTime.add(Calendar.SECOND,5);
	        Date expiresDate=nowTime.getTime();
		    token =JWT.create().withClaim("pmAgent", strLoginInfo)
				.withAudience(strUid)
				.withIssuedAt(iatDate)
				.withExpiresAt(expiresDate)
				.sign(Algorithm.HMAC256(jwtTokenSecret));*/
		
		//设置token 4个小时过期,4小时必须重登陆
	    Date iatDate=new Date();
        Calendar nowTime=Calendar.getInstance();
        nowTime.add(Calendar.HOUR,4);
        Date expiresDate=nowTime.getTime();
		
		String token =JWT.create().withClaim("pmAgent", strLoginInfo)
																.withAudience(strUid)
																.withIssuedAt(iatDate)
																.withExpiresAt(expiresDate)
																.sign(Algorithm.HMAC256(jwtTokenSecret));
		
		return token;
	}
	
	//用于软件加密授权的license
	public static String getLicenseJwtToken(String strLoginInfo,String strSecret) {	
		String token =JWT.create().withClaim("fsinfo", strLoginInfo)
																.withAudience("fstoken")
																.sign(Algorithm.HMAC256(strSecret));
		
		token=token.replace(".", "").toUpperCase();
		
		return token;
	}
	
	
	/**
	 * 根据TOKEN获取客户的基本信息
	 * @param token
	 * @return
	 */
	public static String getPmAgent(String token) {
		String strPmAgent="";
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtTokenSecret)).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			// e.printStackTrace();
			// token 校验失败, 抛出Token验证非法异常
			return strPmAgent;
		}

		Map<String, Claim> claims = jwt.getClaims();
		Claim user_id_claim = claims.get("pmAgent");
		strPmAgent=user_id_claim.asString();
	
		return strPmAgent;
	}
	
}
