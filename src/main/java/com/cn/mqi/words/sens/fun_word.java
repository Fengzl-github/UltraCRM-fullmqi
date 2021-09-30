package com.cn.mqi.words.sens;

import javax.annotation.PostConstruct;

import com.cn.mqi.sqlutil.Fun_sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.utils.MyString;

/**
 * 读取关键词的通用类
 * 
 * @author GuaiWenWo
 *
 */
@Component
public class fun_word {

	@Autowired
	@Qualifier("crmJdbc")
	private JdbcTemplate crmJdbc;

	public static fun_word instance;

	@PostConstruct
	public void init() {
		instance = this;
	}

	/*
	 * private fun_word() {};
	 * 
	 * public static fun_word getInstance() { return SingletonHelp.instance; };
	 * 
	 * private static class SingletonHelp { static fun_word instance = new
	 * fun_word(); }
	 */

	/**
	 * 读取列表
	 * 
	 * @param strPostData
	 * @return
	 */
	public static String getListLog(String strPostData) {
		String strRetJson = "";
		JSONObject parseObject = JSONObject.parseObject(strPostData);
		String strSearchFilterJson = parseObject.getString("SEARCH_FILTER"); // 查询条件
		String strTablePageJson = parseObject.getString("TABLE_PAGE"); // 翻页条件

		String strSearchSql = getSqlCheck(strSearchFilterJson);
		strRetJson = Fun_sql.getListLog(instance.crmJdbc, strTablePageJson, strSearchSql, getOrderBy(), "关键词");

		return strRetJson;
	}

	/**
	 * 获取原生查询sql语句
	 * 
	 * @return
	 */
	private static String getSqlCheck(String strSearchFilterJson) {

		JSONObject htSearchRet = JSONObject.parseObject(strSearchFilterJson);

		String strWID = htSearchRet.getString("WID");
		String strKeyWord = htSearchRet.getString("KEYWORD");
		String strTableName = WordBannerService.getTableName(strWID);// 合成表名

		// 查询条件
		String strFilter = "1=1";
		if (strKeyWord.length() > 0) {
			strFilter = MyString.Format("KEY_WORD LIKE '%{0}%'", strKeyWord);
		}

		// 获取sql条件
		String strSql = MyString.Format("SELECT * FROM {0} WHERE {1}", strTableName, strFilter);

		return strSql;
	}

	/**
	 * 保存数据
	 * 
	 * @param strData
	 * @param strWID
	 * @return
	 */
	public static String savaData(String strData, String strWID) {

		String strRet = "";
		String strTableName = WordBannerService.getTableName(strWID);
		// 先检测表是否存在
		if (false) {
			strRet = MyString.Format("FAIL:未能检测到表{0}的存在", strTableName);
			return strRet;
		}

		JSONObject parseObject = JSONObject.parseObject(strData);
		String strKeyWord = parseObject.getString("KEYWORD");
		// 检测数据是否存在
		/*
		 * int rc = pTable
		 * .my_odbc_find(MyString.Format("SELECT * FROM {0} WHERE KEYWORD='{1}'",
		 * strTableName, strKeyWord)); if (rc == 1) { strRet =
		 * MyString.Format("FAIL:当前关键词已经存在,请不要重复添加", strTableName); return strRet; }
		 */

		// 赋默认值
		/*
		 * Functions.ht_SaveEx("SDATE", DateTime.Now().ToString("yyyyMMdd"), htRet);
		 * Functions.ht_SaveEx("STIME", DateTime.Now().ToString("HHmmss"), htRet);
		 * Functions.ht_SaveEx("GHID", "0", htRet);
		 * 
		 * // 写库 rc = pTable.my_odbc_addnew(strTableName, htRet); if (rc <= 0) { strRet
		 * = MyString.Format("FAIL:当前关键词写库失败", strTableName); return strRet; }
		 */

		return strRet;
	}

	/**
	 * 删除数据
	 * 
	 * @param strWID
	 * @return
	 */
	public static String delData(String strKeyWord, String strWID) {

		String strRet = "";
		String strTableName = WordBannerService.getTableName(strWID);
		// 先检测表是否存在
		/*
		 * if (pTable.my_odbc_findTable(strTableName) == false) {
		 * pTable.my_odbc_disconnect(); strRet = MyString.Format("FAIL:未能检测到表{0}的存在",
		 * strTableName); return strRet; }
		 */

//		int rc = pTable.my_odbc_delete(strTableName, MyString.Format("KEYWORD='{0}'", strKeyWord));
		/*
		 * if (rc < 0) { strRet = MyString.Format("FAIL:删除当前关键词失败,请重试", strTableName);
		 * return strRet; }
		 */

		return strRet;
	}

	/**
	 * 统一排序条件
	 * 
	 * @return
	 */
	private static String getOrderBy() {
		String strOrderBy = "ORDER BY CREATE_TIME DESC";
		return strOrderBy;
	}

}