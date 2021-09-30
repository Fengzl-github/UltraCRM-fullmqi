package com.cn.mqi.words.sens;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.cn.mqi.sqlutil.Fun_sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.utils.MyString;

/**
 * 针对关键词集合的处理
 * 
 * @author GuaiWenWo
 *
 */
@Component
public class fun_word_table {

	@Autowired
	@Qualifier("crmJdbc")
	private JdbcTemplate crmJdbc;

	public static fun_word_table instance;

	@PostConstruct
	public void init() {
		instance = this;
	}

	/**
	 * 词库集合管理表
	 */
	private static String m_strTableName = "T_WORDS_TABLES";

	/**
	 * 模板表
	 */
	private static String m_strTableNameTemp = "T_WORDS_TEMP";

	/**
	 * 产生更新的库
	 */
	private static String m_strTableNameUpdate = "T_WORDS_UPDATE";

	/*
	 * private fun_word_table() { };
	 * 
	 * public static fun_word_table getInstance() { return SingletonHelp.instance;
	 * };
	 * 
	 * private static class SingletonHelp { static fun_word_table instance = new
	 * fun_word_table(); }
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
		strRetJson = Fun_sql.getListLog(instance.crmJdbc, strTablePageJson, strSearchSql, getOrderBy(), "词库集合");

		return strRetJson;
	}

	/**
	 * 获取原生查询sql语句
	 * 
	 * @param
	 * @return
	 */
	private static String getSqlCheck(String strSearchFilterJson) {

		JSONObject parseObject = JSONObject.parseObject(strSearchFilterJson);

		String strWID = parseObject.getString("WID");
		String strProjName = parseObject.getString("PROJ_NAME");

		// 查询条件
		String strFilter = "1=1";
		if (strWID.length() > 0) {
			strFilter = MyString.Format("{0} AND W_ID LIKE '%{1}%'", strFilter, strWID);
		}

		if (strProjName.length() > 0) {
			strFilter = MyString.Format("{0} AND W_NAME LIKE '%{1}%'", strFilter, strProjName);
		}

		// 获取sql条件
		String strSql = MyString.Format("SELECT * FROM {0} WHERE {1}", m_strTableName, strFilter);

		return strSql;
	}

	/**
	 * 统一排序条件
	 * 
	 * @return
	 */
	private static String getOrderBy() {
		// String strOrderBy = "ORDER BY SDATE DESC,STIME DESC,WID DESC";
		String strOrderBy = "ORDER BY W_ID DESC";
		return strOrderBy;
	}

	/**
	 * 保存数据
	 * 
	 * @param strData
	 * @param nCMD
	 * @return
	 */
	public static String savaData(String strData, int nCMD) {

		String strRet = "";
		String strTableName = m_strTableName;
		JSONObject parseObject = JSONObject.parseObject(strData);
		String strWID = parseObject.getString("WID");

		// 检测数据是否存在
		List<Map<String, Object>> queryForList = instance.crmJdbc.queryForList(MyString.Format("SELECT * FROM {0} WHERE WID='{1}'", strTableName, strWID));
		int rc = queryForList.size();
		if (rc == 1 && nCMD == 0) { // nCMD == 0 代表新增
			strRet = MyString.Format("FAIL:当前唯一编号已经存在,请不要重复添加", strTableName);
			return strRet;
		}

		// 写库
		/*
		 * if (nCMD == 0) { // 代表新增 Functions.ht_SaveEx("SDATE",
		 * DateTime.Now().ToString("yyyyMMdd"), htRet); Functions.ht_SaveEx("STIME",
		 * DateTime.Now().ToString("HHmmss"), htRet); Functions.ht_SaveEx("GHID", "0",
		 * htRet);
		 * 
		 * rc = pTable.my_odbc_addnew(strTableName, htRet); } else { rc =
		 * pTable.my_odbc_update(strTableName, htRet, myString.Format("WID='{0}'",
		 * strWID)); }
		 */

		if (rc <= 0) {
			strRet = MyString.Format("FAIL:当前词库写库失败,请重试", strTableName);
			return strRet;
		}

		// 根据模板表复制新的词库表
		/*
		 * String strTableNameWord = WordBannerService.getTableName(strWID); if
		 * (pTable.my_odbc_findTable(strTableNameWord) == false) {
		 * Functions.dt_CopyTable_stru(m_strTableNameTemp, strTableNameWord,
		 * pmSys.conn_crm, "KEYWORD"); }
		 */

		// 更新临时缓存
//		upWordTable(pTable,strWID);

		return strRet;
	}

	/**
	 * 删除数据
	 * 
	 * @return
	 */
	public static String delData(String strWID) {

		String strRet = "";

//		int rc = pTable.my_odbc_delete(m_strTableName, MyString.Format("WID='{0}'", strWID));
		instance.crmJdbc.execute(MyString.Format("WID='{0}'", strWID));
		/*
		 * if (rc < 0) { strRet = MyString.Format("FAIL:删除当前词库数据失败,请重试",
		 * m_strTableName); return strRet; }
		 */

//		upWordTable(pTable,strWID);

		return strRet;
	}

	/**
	 * 将产生更新的数据写入更新表,用于服务自动检测来更新缓存
	 * 
	 * @param pTable
	 * @param strWID
	 */
	/*
	 * private static void upWordTable(my_odbc pTable,String strWID) {
	 * 
	 * String strSql=MyString.Format("SELECT * FROM {0} WHERE WID='{1}'",
	 * m_strTableNameUpdate,strWID); int rc=pTable.my_odbc_find(strSql);
	 * pTable.my_odbc_disconnect(); if (rc==1) { //如果检测到当前id存在,说明还未做更新,就不必在做了
	 * return; }
	 * 
	 * HashMap htRet=new HashMap(); Functions.ht_Save("WID", strWID, htRet);
	 * Functions.ht_SaveEx("SDATE", DateTime.Now().ToString("yyyyMMdd"), htRet);
	 * Functions.ht_SaveEx("STIME", DateTime.Now().ToString("HHmmss"), htRet);
	 * Functions.ht_SaveEx("GHID", "0", htRet);
	 * 
	 * pTable.my_odbc_addnew(m_strTableNameUpdate,htRet);
	 * pTable.my_odbc_disconnect(); }
	 */

	/**
	 * 检测是否有更新数据,如果有则重新加载缓存
	 */
	public void updateWordsCache() {

		List<Map<String, Object>> queryForList = crmJdbc.queryForList(MyString.Format("SELECT WID FROM {0}", m_strTableNameUpdate));

		if (queryForList.size() == 0) {
			return;
		}

		for (int i = 0; i < queryForList.size(); i++) {

			String strWIDValue = String.valueOf(queryForList.get(i).get("WID"));
			// 先删除数据
//			pTable.my_odbc_delete(m_strTableNameUpdate, MyString.Format("WID='{0}'", strWIDValue));
			// 在更新缓存
			WordBannerService.getWordBannerService().init(strWIDValue);

			System.out.println("更新缓存;WID=" + strWIDValue);
		}
	}

}
