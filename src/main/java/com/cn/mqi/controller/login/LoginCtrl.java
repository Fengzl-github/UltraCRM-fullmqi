package com.cn.mqi.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.vo.ResCode;
import com.cn.common.vo.ResResult;
import com.cn.mqi.base.PmSys;
import com.cn.mqi.jwt.PmJwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *@Author fengzhilong
 *@Date 2021/9/28 14:48
 *@Desc
 **/
@Slf4j
@RestController
@RequestMapping("/sys")
public class LoginCtrl {

    @Value("${log.path}")
    private String logFilePath;

    @PostMapping("/login/verification")
    public ResResult login(String uid, String pwd) {

        if (uid.length() >= 4 && pwd.length() >= 4 && uid.equals(PmSys.login_uid) && pwd.equals(PmSys.login_pwd)) {
            Map<String, String> htRet = new HashMap<>();
            htRet.put("RET", "OK");
            htRet.put("GHID", uid);
            htRet.put("UNAME", "管理员");
            //生成唯一认证token
            String strTokenJson = JSONObject.toJSONString(htRet);
            log.info("系统登录,token:{}", strTokenJson);

            //登录成功后,保存日志的存储路径,以便websocket中使用
            PmSys.log_file_path = logFilePath;

            return ResCode.OK.msg("登录成功")
                    .putData("TOKEN", PmJwtToken.getJwtToken(uid, strTokenJson));
        } else {

            return ResCode.ERROR.msg("账号或密码不正确");
        }
    }


    /*@PostMapping("/loginverification/get_pmagent")
    public ResResult get_config(String token) {

        String strData = PmJwtToken.getPmAgent(token);

        return ResCode.OK.setData(strData);
    }*/
}
