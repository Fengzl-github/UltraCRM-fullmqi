package com.cn.mqi.words.sens;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.utils.MyString;
import com.cn.mqi.config.DataSourceConfig;
import com.cn.mqi.words.sensitive.KWSeeker;
import com.cn.mqi.words.sensitive.KeyWord;
import com.cn.mqi.words.sensitive.SensitiveWordResult;
import com.cn.mqi.words.sensitive.SimpleKWSeekerProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词或关键词过滤
 * 
 * @author GuaiWenWo
 *
 */
@Slf4j
public class WordBannerService implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Autowired
	@Qualifier("crmJdbc")
    private JdbcTemplate crmJdbc;
	/**
	 * 敏感词或关键词的模板表
	 */
	private static String m_TableNameTemp = "T_WORDS_TEMP";

	/**
	 * 生成每一张对应关系表前缀表名称 即:表前缀+表ID
	 */
	private static String m_TableNameTempName = "T_WORDS_";

	/**
	 * 关键词或敏感词管理表
	 */
	private static String m_TableNameTables = "T_WORDS_TABLES";

	/**
	 * 关键词或敏感词更新变化表,便于同步缓存数据
	 */
	private static String m_TableNameUpdate = " T_WORDS_UPDATE";

	/**
	 * 默认当前对象单例模式
	 */
	public static WordBannerService DEFAULT;

	/**
	 * 缓存存储 关键词或敏感词
	 */
	public static SimpleKWSeekerProcessor processor;

	
	
	private WordBannerService() {};
	
	public static synchronized WordBannerService getWordBannerService() {
		
		System.out.println("初始化单例............");
		if (DEFAULT == null) {
			DEFAULT = new WordBannerService();
		}
		
		return DEFAULT;
	}
	
	
	
	
	/**
	 * 缓存关键词数据初始化
	 * 
	 * @return
	 */
	public String init() {
		return init("");
	}

	

	public String init(String strWIDValue) {
		
		if (crmJdbc == null) {
	        crmJdbc = DataSourceConfig.getCrmJdbc();
		}
		
		String strRet = "";

		// 也允许单个程序处理缓存服务
		String strFilter = "1=1";
		if (MyString.isNotEmpty(strWIDValue)) {
			strFilter = MyString.Format("W_ID='{0}'", strWIDValue);
		}

		// 初始化缓存对象
		processor = SimpleKWSeekerProcessor.newInstance();

		// 读取关联表
		String strSql = MyString.Format("SELECT W_ID FROM {0} WHERE W_STATUS=0 AND {1} ORDER BY CREATE_TIME ASC", m_TableNameTables, strFilter);

		List<Map<String, Object>> queryForList = crmJdbc.queryForList(strSql);
		

		if (queryForList.size() <= 0) {
//			fun_main.Logging_monitor_msg("关键词/敏感词监控不执行", "原因:当前不存在可监控的关键词表数据,SQL:" + strSql);
			strRet = MyString.Format("FAIL;更新缓存失败,原因:请确认当前词库是否启动服务或在管理表中是否合理存在");

			// 如果检测不到合法数据,直接移除缓存
			if (strWIDValue.length() > 0) {
				processor.removeKWSeeker(strWIDValue);
				System.out.println("移除缓存,W_ID=" + strWIDValue);
			}

			return strRet;
		}

		// 循环每个要监控的表
		for (Map<String, Object> stringObjectMap : queryForList) {
			Object object = stringObjectMap.get("W_ID");
			String strWID = String.valueOf(object);
			String strTableName = getTableName(strWID);

			boolean existTable = isExistTable(crmJdbc, strTableName);
			if (!existTable) {
//				fun_main.Logging_monitor_msg("关键词/敏感词监控失败", "原因:当前表" + strTableName + "不存在");
				strRet = MyString.Format("FAIL;更新缓存失败,原因:原因:当前表" + strTableName + "不存在");
				continue;
			}

			// 读取要监听的数据
			String strSqlWord = MyString.Format("SELECT KEY_WORD FROM {0}", strTableName);
			List<Map<String, Object>> queryForList2 = crmJdbc.queryForList(strSqlWord);
			log.info("关键字 {} 有{} 条数据需要加入缓存", strWID, queryForList2.size());
			// 将数据全都加入缓存
			processor.addWords(strWID, queryForList2, "KEY_WORD");
		}


		return strRet;
	}

	/**
	 * 根据WID返回指定表
	 * 
	 * @param strWID
	 * @return
	 */
	public static String getTableName(String strWID) {
		return m_TableNameTempName + strWID;
	}

	/**
	 * 可以临时加入自定义的关键词
	 * 
	 */
	public void put(String strWordKey, String strWordValue) {
		KWSeeker seeker = processor.getKWSeeker(strWordKey);
		seeker.addWord(new KeyWord(strWordValue));
	}

	/**
	 * 可以临时加入自定义的关键词
	 * 
	 */
	public void remove(String strWordKey, String strWordValue) {
		KWSeeker seeker = processor.getKWSeeker(strWordKey);
		seeker.remove(strWordValue);
	}

	/**
	 * 自定义增量或减量指定关键词,暂时没用到
	 * 
	 * @param strWordKey
	 * @param dtAddWord
	 * @param dtDelWord
	 * @param strFieldName
	 */
	/*public void putAddOrDel(String strWordKey, DataTable dtAddWord, DataTable dtDelWord, String strFieldName) {
		KWSeeker seeker = processor.getKWSeeker(strWordKey);
		seeker.addOrDelWord(dtAddWord, dtDelWord, strFieldName);
	}*/

	/**
	 * 针对关键词做检索识别 当前仅用于测试页面调用,正式对外调用接口是单独写的
	 * 
	 * @param strWord
	 * @param strWID
	 * @return
	 */
	public static String testWord(String strWord, String strWID) {
		if (MyString.isEmpty(strWord) || MyString.isEmpty(strWID)) {
			return "FAIL:请求参数不合法";
		}

		if (processor == null) {
			return MyString.Format("FAIL:当前服务缓存还未能加载完成,不予执行", strWID);
		}

		KWSeeker kw = processor.getKWSeeker(strWID);
		if (kw == null) {
			return MyString.Format("FAIL:当前WID={0}的缓存数据不存在,不予执行", strWID);
		}

		long startTime = System.currentTimeMillis(); // 执行方法

		List<SensitiveWordResult> lstWords = kw.findWords(strWord);
		System.out.println("返回敏感词及下标：" + lstWords);
		String strHLight = kw.htmlHighlightWords(strWord);
		System.out.println("html高亮：" + strHLight);
		String strReplace = kw.replaceWords(strWord);
		System.out.println("字符替换：" + strReplace);

		long endTime = System.currentTimeMillis();
		System.out.println("执行时间:" + (endTime - startTime));

		Map<String, Object> htRet = new HashMap<>();
		htRet.put("WRET", getListSensitiveWordResult(lstWords));
		htRet.put("WHLIGHT", strHLight);
		htRet.put("WREPLACE", strReplace);
		htRet.put("WTIME", (endTime - startTime));

		return JSONObject.toJSONString(htRet);
	}

	/**
	 * 读取识别出的关键词,暂时拼接在一块
	 * 
	 * @param lstWords
	 * @return
	 */
	public static String getListSensitiveWordResult(List<SensitiveWordResult> lstWords) {

		StringBuilder sbText = new StringBuilder();
		for (int i = 0; i < lstWords.size(); i++) {
			SensitiveWordResult sw = lstWords.get(i);
			if (sbText.length() > 0) {
				sbText.append("|");
			}
			sbText.append(sw.getWord());
		}

		return sbText.toString();
	}

	/**
	 * 当前类初始化 读取敏感词文件读取到缓存中
	 * 
	 * @return
	 */
//	public static boolean init() {
//		boolean bFlag = false;
//		try {
//			DEFAULT = new WordBannerFilter(new BufferedReader(
//					new InputStreamReader(new FileInputStream(SENSI_WORD_LOCAL_PATH), StandardCharsets.UTF_8)));
//
//			bFlag = true;
//		} catch (FileNotFoundException e) {
//
//		}
//		return bFlag;
//	}

	/**
	 * 用流方式读取txt文件,并存储
	 * 
	 * @param reader
	 */
//	public WordBannerFilter(BufferedReader reader) {
//		try {
//			List<String> dataList = new ArrayList<String>();
//			String line1 = "";
//
//			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//				// 修复jdk的bug,在读取第一行的时候,会多一个空字节,导致后续数据出现问题
//				char s = line.trim().charAt(0);
//				if (s == 65279 && line.length() > 1) {
//					line = line.substring(1);
//				}
//
//				dataList.add(line);
//			}
//			processor = SimpleKWSeekerProcessor.newInstance();
//			processor.addWords("default", dataList);
//			processor.addWords("default1", dataList);
//
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 对句子进行敏感词过滤
	 */
	/*
	 * public BannerResp process(String strSentence, boolean isSEncry) { BannerResp
	 * resp = new BannerResp(); KWSeeker seeker =
	 * processor.getKWSeeker(SENSI_WORD_AREA); strSentence =
	 * strSentence.replace(" ", ""); List<SensitiveWordResult> words =
	 * seeker.findWords(strSentence);
	 * 
	 * if (!words.isEmpty()) { resp.setHasSensitiveWords(true); for
	 * (SensitiveWordResult result : words) {
	 * resp.addSensitiveWord(result.getWord()); }
	 * 
	 * if (isSEncry) { // 将关键词设置***
	 * resp.setFilterStr(seeker.replaceWords(strSentence)); }
	 * resp.setFilterStr(strSentence);
	 * 
	 * } else { resp.setFilterStr(strSentence); } return resp; }
	 */
	
	
	/**
	 * 检测表是否存在
	 * @param jdbcTemplate
	 * @param tableName
	 * @return
	 */
	public static boolean isExistTable(JdbcTemplate jdbcTemplate, String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            rs = metaData.getTables(null, null, tableName, types);
            if (rs.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
