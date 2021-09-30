package com.cn.mqi.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时更新token的secret秘钥
 * @author GuaiWenWo
 *
 */
@Component
public class UpdateJwtTokenTimer {

	/**
	 * 每天凌晨10分时触发token改变,此刻所有的登录在发起请求都需要登录验证
	 */
    @Scheduled(cron="0 10 0 * * ?")
    public void timerCorpid() {
   	
    	try {
		     PmJwtToken.setNewTokenSecret();
		     System.out.printf("秘钥发生改变", "所有人都需要重新登录");
		} catch (Exception e) {
			
		}
    }
	
}
