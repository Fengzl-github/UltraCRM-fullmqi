package com.cn.mqi.base;

/**
 *@Author fengzhilong
 *@Date 2021/9/23 16:23
 *@Desc 系统参数设置
 **/
public class PmSys {

    /**
     * 端口参数
     */
    public static Integer Server_Port = 6022;

    /**
     * 数据库参数, 默认分callthink和callthink_crm 两个数据库,如果需要多个,需要单独添加
     * callthink为用户菜单角色等信息库-相当于共用库,callthink_crm为项目业务库
     */
    public static String data_type; //callthink数据库类型 MySQL,Oracle
    public static String data_host; //callthink服务器名或ip + 端口
    public static String data_base; //callthink数据库名
    public static String data_uid;  //callthink用户名
    public static String data_pwd;  //callthink密码

    public static String data_type_crm; //crm数据库类型 MySQL,Oracle
    public static String data_host_crm; //crm服务器名或ip + 端口
    public static String data_base_crm; //crm数据库名
    public static String data_uid_crm; //crm用户名
    public static String data_pwd_crm; //crm密码

    /**
     * 超级管理员信息
     */
    public static String login_uid; //登录账号
    public static String login_pwd; //登录密码

    /**
     * 日志文件存储路径
     */
    public static String log_file_path;


}
