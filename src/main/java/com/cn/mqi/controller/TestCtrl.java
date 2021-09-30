package com.cn.mqi.controller;

import com.cn.common.utils.MyString;
import com.cn.common.vo.ResCode;
import com.cn.common.vo.ResResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *@Author fengzhilong
 *@Date 2021/9/27 10:14
 *@Desc
 **/
@RestController
public class TestCtrl {

    @Autowired
    @Qualifier("callthinkJdbc")
    private JdbcTemplate callthinkJDBC;

    @Autowired
    @Qualifier("crmJdbc")
    private JdbcTemplate crmJDBC;


    @GetMapping("/getCallUser")
    public ResResult getCallUser(){

        String strSql = MyString.Format("select * from t_user ");

        List<Map<String, Object>> list = callthinkJDBC.queryForList(strSql);


        strSql = MyString.Format("select * from crm_test");


        List<Map<String, Object>> list2 = crmJDBC.queryForList(strSql);

        return ResCode.OK
                .putData("callthinkData",list)
                .putData("crmData",list2);


    }


    @GetMapping("/getCrmUser")
    public ResResult getCrmUser(String name, Integer age, HttpServletRequest request){

        System.out.println("name=="+name);
        System.out.println("age=="+age);
        System.out.println("request="+request.getParameter("name"));

        String strSql = "SELECT * FROM T_TEST";


        List<Map<String, Object>> list2 = crmJDBC.queryForList(strSql);

        return ResCode.OK
                .putData("crmData",list2);


    }
    @PostMapping("/postCrmUser")
    public ResResult postCrmUser(String name,  HttpServletRequest request){

        System.out.println("=============="+name);

        String strSql = "SELECT * FROM T_TEST";


        List<Map<String, Object>> list2 = crmJDBC.queryForList(strSql);

        return ResCode.OK
                .putData("crmData",list2);


    }

}
