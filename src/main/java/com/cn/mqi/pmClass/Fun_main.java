package com.cn.mqi.pmClass;

import com.cn.common.utils.MyString;
import com.cn.mqi.base.PmSys;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

/**
 *@Author fengzhilong
 *@Date 2021/9/23 17:04
 *@Desc
 **/
@Slf4j
public class Fun_main {

    /**
     * 配置文件目录
     */
    public final static String iniFileParentPath = ".\\data";
    public final static String iniFilePath = ".\\data\\UltraCRM.ini";

    /**
     * 获取系统参数配置
     * @return 1 获取成功
     */
    public static int getParaIni() {

        log.info("配置系统参数...");
        PmSys.login_uid = "admin";
        PmSys.login_pwd = "admin";
        PmSys.data_type = "MySql";
        PmSys.data_host = "127.0.0.1:3306";
        PmSys.data_base = "CALLTHINK";
        PmSys.data_uid = "root";
        PmSys.data_pwd = "ULTRATEL";
        PmSys.data_type_crm = "MySql";
        PmSys.data_host_crm = "127.0.0.1:3306";
        PmSys.data_base_crm = "CALLTHINK_CRM";
        PmSys.data_uid_crm = "root";
        PmSys.data_pwd_crm = "ULTRATEL";

        File file = new File(iniFilePath);
        //如果配置文件不存在,创建并写入默认配置信息
        if (!file.exists()) {
            log.info("配置文件UltraCRM.ini不存在,创建");
            saveParaIni();
        } else {
            log.info("配置文件UltraCRM.ini存在,读取文件获取系统参数");
            readParaIni();
        }

        return 1;
    }


    /**
     * 读取系统Ini文件参数配置
     */
    public static void readParaIni() {
        File file = new File(iniFilePath);
        String readConfigText = readConfigText(file, "SERVER", "PORT");
        if (MyString.isNotEmpty(readConfigText)) {
            PmSys.Server_Port = Integer.parseInt(readConfigText);
        }

        PmSys.login_uid = readConfigText(file, "LOGIN", "LOGIN_UID");
//		pmSys.login_pwd = Functions.pwd_decode(strPwd, "bjqxbjqx");
        PmSys.login_pwd = readConfigText(file, "LOGIN", "LOGIN_PWD");

        PmSys.data_type = readConfigText(file, "AGENT", "DATA_TYPE");
        PmSys.data_host = readConfigText(file, "AGENT", "DATA_SRC");
        PmSys.data_base = readConfigText(file, "AGENT", "DATA_BASE");
        PmSys.data_uid = readConfigText(file, "AGENT", "DATA_UID");
        PmSys.data_pwd = readConfigText(file, "AGENT", "DATA_PWD");

        PmSys.data_type_crm = readConfigText(file, "AGENT", "DATA_TYPE_CRM");
        PmSys.data_host_crm = readConfigText(file, "AGENT", "DATA_SRC_CRM");
        PmSys.data_base_crm = readConfigText(file, "AGENT", "DATA_BASE_CRM");
        PmSys.data_uid_crm = readConfigText(file, "AGENT", "DATA_UID_CRM");
        PmSys.data_pwd_crm = readConfigText(file, "AGENT", "DATA_PWD_CRM");
    }

    /**
     * 把系统参数写入配置文件
     */
    public static void saveParaIni() {
        File file = new File(iniFileParentPath);
        if (!file.exists()) {
            log.info("创建文件目录");
            file.mkdirs();
        }
        Path path = Paths.get(iniFilePath);
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write("[SERVER]");
            bw.newLine();
            bw.write("PORT=" + PmSys.Server_Port);
            bw.newLine();
            bw.write("[LOGIN]");
            bw.newLine();
            bw.write("LOGIN_UID=" + PmSys.login_uid);
            bw.newLine();
            bw.write("LOGIN_PWD=" + PmSys.login_pwd);
            bw.newLine();
            bw.write("[AGENT]");
            bw.newLine();
            bw.write("DATA_TYPE=" + PmSys.data_type);
            bw.newLine();
            bw.write("DATA_SRC=" + PmSys.data_host);
            bw.newLine();
            bw.write("DATA_BASE=" + PmSys.data_base);
            bw.newLine();
            bw.write("DATA_UID=" + PmSys.data_uid);
            bw.newLine();
            bw.write("DATA_PWD=" + PmSys.data_pwd);
            bw.newLine();
            bw.write("DATA_TYPE_CRM=" + PmSys.data_type_crm);
            bw.newLine();
            bw.write("DATA_SRC_CRM=" + PmSys.data_host_crm);
            bw.newLine();
            bw.write("DATA_BASE_CRM=" + PmSys.data_base_crm);
            bw.newLine();
            bw.write("DATA_UID_CRM=" + PmSys.data_uid_crm);
            bw.newLine();
            bw.write("DATA_PWD_CRM=" + PmSys.data_pwd_crm);

        } catch (IOException e) {
            log.error("默认系统参数写入配置文件失败!");
            e.printStackTrace();
        }

    }


    /**
     * 获取配置文件中的参数属性值
     * @param file 文件名
     * @param title 标题 [LOGIN]
     * @param field 属性
     * @return
     */
    public static String readConfigText(File file, String title, String field) {

        BufferedReader br = null;
        String pattern = "^\\[[A-Z0-9]*\\]$";
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("[" + title + "]")) {
                    String text;
                    while ((text = br.readLine()) != null) {
                        boolean matches = Pattern.matches(pattern, text);
                        if (matches) {
                            return null;
                        }
                        if (text.contains(field)) {
                            String[] split = text.split("=");
                            if (split[0].equals(field)) {
                                if (split.length > 1)
                                    return split[1];
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


}
