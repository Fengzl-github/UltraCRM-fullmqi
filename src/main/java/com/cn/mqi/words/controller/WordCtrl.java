package com.cn.mqi.words.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.utils.MyString;
import com.cn.mqi.words.sens.WordBannerService;
import com.cn.mqi.words.sens.fun_word;
import com.cn.mqi.words.sens.fun_word_table;
import org.springframework.web.bind.annotation.*;

/**
 *@Author fengzhilong
 *@Date 2021/9/28 16:23
 *@Desc
 **/
@RestController
@RequestMapping("/word")
public class WordCtrl {



    @RequestMapping(value = "/set_data/{sName}", method = RequestMethod.POST)
    public String set_config(@PathVariable String sName, @RequestBody String strPostData) {
        String strReturn = "FAIL:设置失败";
        JSONObject parseObject = JSONObject.parseObject(strPostData);
        if (sName.equals("word_list")) { //获取指定关键词/敏感词数据
            String strRet = fun_word.getListLog(strPostData);
            return strRet;
        }
        else if (sName.equals("testword")) // 检测词语
        {
            String strWord = parseObject.getString("DATA");
            String strWID = parseObject.getString("WID");
            strReturn = WordBannerService.testWord(strWord, strWID);

        } else if (sName.equals("save_word")) {// 保存关键词

            String strData = parseObject.getString("DATA");
            String strWID = parseObject.getString("WID");
            strReturn = fun_word.savaData(strData, strWID);
            strReturn = strReturn.length() > 0 ? strReturn : "OK;保存成功";

        }
        else if (sName.equals("del_word")) {//删除关键字
            String strData = parseObject.getString("DATA");
            String strWID = parseObject.getString("WID");
            strReturn = fun_word.delData(strData, strWID);
            strReturn = strReturn.length() > 0 ? strReturn : "OK;删除成功";
        }
        else if (sName.equals("refresh_word")) {//刷新词库的缓存,便于接口能够识别和检测
            String strWID = parseObject.getString("WID");
            if (MyString.isEmpty(strWID)) {
                strReturn="FAIL;更新缓存失败,原因:当前传递参数WID不合法";
            }
            else {
                strReturn=WordBannerService.getWordBannerService().init(strWID);
            }

            strReturn = strReturn.length() > 0 ? strReturn : "OK;刷新缓存成功";
        }
        else if (sName.equals("word_table_list")) {//读取词库集合
            String strRet = fun_word_table.getListLog(strPostData);
            return strRet;
        }
        else if (sName.equals("save_word_table")) {// 保存词库数据

            String strData = parseObject.getString("DATA");

            int nCMD=Integer.parseInt(parseObject.getString("CMD"));
            strReturn = fun_word_table.savaData(strData, nCMD);
            strReturn = strReturn.length() > 0 ? strReturn : "OK;保存成功";

        }
        else if (sName.equals("del_word_table")) {//删除词库数据
            String strData = parseObject.getString("DATA");
            strReturn = fun_word_table.delData(strData);
            strReturn = strReturn.length() > 0 ? strReturn : "OK;删除成功";
        }

        return strReturn;
    }
}
