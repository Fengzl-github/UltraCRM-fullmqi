package com.cn.mqi.sqlutil;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.common.utils.MyString;

/**
 * 针对当前查询sql语句通用函数 主要针对分页
 * 
 * @author GuaiWenWo
 *
 */
@Slf4j
public class Fun_sql {

	public static String getListLog(JdbcTemplate jdbcTemplate, String strTablePageJson, String strSearchSql, String strOrderBy) {
		return getListLog(jdbcTemplate, strTablePageJson, strSearchSql, strOrderBy, "");
	}

	/**
	 * 翻页sql语句查询
	 * 
	 * @param strTablePageJson
	 * @param strSearchSql
	 * @param strOrderBy
	 * @return
	 */
	public static String getListLog(JdbcTemplate jdbcTemplate, String strTablePageJson, String strSearchSql, String strOrderBy, String strLogDes) {
		String strRetJson = "";

		JSONObject htPageRet = JSONObject.parseObject(strTablePageJson);

		// 获取页码
		int nPageSize = htPageRet.getIntValue("pageSize");
		int nPageNum = htPageRet.getIntValue("currentPage");
		if (nPageSize <= 0) {
			nPageSize = 10;
		}
		if (nPageNum <= 0) {
			nPageNum = 1;
		}

		int nStartPage = (nPageNum - 1) * nPageSize + 1;
		int nEndPage = nPageNum * nPageSize;

		strRetJson = getSqlData(jdbcTemplate, strSearchSql, strOrderBy, nStartPage, nEndPage, nPageSize, strLogDes);

		return strRetJson;
	}

	/**
	 * 去数据库集合查询
	 * 
	 * @param strSearchSql
	 * @param strOrderBy
	 * @param nStartPage
	 * @param nEndPage
	 * @param nPageSize
	 * @return
	 */
	private static String getSqlData(JdbcTemplate jdbcTemplate, String strSearchSql, String strOrderBy, int nStartPage, int nEndPage, int nPageSize, String strLogDes) {

		String strJson = "";

		String strSqlCnt = MyString.Format("SELECT COUNT(*) AS CNT FROM ({0})", strSearchSql);
		String strSqlData = "";

		strSqlCnt = MyString.Format("SELECT COUNT(*) AS CNT FROM ({0}) AS TT", strSearchSql);
		strSqlData = MyString.Format("SELECT * FROM ({0}) AS TT {1} LIMIT {2},{3}", strSearchSql, strOrderBy, nStartPage - 1, nPageSize);

		log.info("查询{},总数量:{}", strLogDes, strSqlCnt);
		log.info("查询{},数据:{}", strLogDes, strSqlData);

		Map<String, Object> pmRes = jdbcTemplate.queryForMap(strSqlCnt);
		List<Map<String, Object>> pmList = jdbcTemplate.queryForList(strSqlData);

		int nTotal = Integer.parseInt(String.valueOf(pmRes.get("CNT")));
		String strData = "[]";
		if (pmList.size() > 0) {
			strData = JSONObject.toJSONStringWithDateFormat(pmList, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
		}
		strJson = "{\"total\":" + nTotal + ",\"data\":" + strData + "}";

		return strJson;
	}

}
