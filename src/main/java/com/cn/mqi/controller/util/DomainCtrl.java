package com.cn.mqi.controller.util;

import com.cn.common.vo.ResCode;
import com.cn.common.vo.ResResult;
import com.cn.mqi.base.PmSys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *@Author fengzhilong
 *@Date 2021/10/8 16:06
 *@Desc
 **/
@RestController
@RequestMapping("/util")
public class DomainCtrl {

    @Value("${server.domain}")
    private String serverDomain;


    @PostMapping("/getServerDomainAndPort")
    public ResResult getServerDomainAndPort() {

        return ResCode.OK.putData("domain", serverDomain)
                .putData("port", PmSys.Server_Port);

    }

}
