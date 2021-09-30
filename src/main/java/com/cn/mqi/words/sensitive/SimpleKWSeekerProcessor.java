package com.cn.mqi.words.sensitive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 简单的敏感词处理器，从配置文件读取敏感词初始化
 */
public class SimpleKWSeekerProcessor extends KWSeekerManage {

	private static volatile SimpleKWSeekerProcessor instance;

	/**
	 * 获取实例
	 * 
	 * @return newInstance
	 */
	public static SimpleKWSeekerProcessor newInstance() {
		if (null == instance) {
			synchronized (SimpleKWSeekerProcessor.class) {
				instance = new SimpleKWSeekerProcessor();
			}
		}
		return instance;
	}

	/**
	 * 初始化敏感词对象集合
	 * 
	 * @param strKey   唯一值,便于识别多种类型关键字的唯一标识
	 * @param dataList 唯一值标记关键词下的 关键词集合体
	 */
	public void addWords(String strKey, List<String> dataList) {
		Map<String, KWSeeker> seekers = new HashMap<String, KWSeeker>();
		Set<KeyWord> kws;

		kws = new HashSet<KeyWord>();
		for (String word : dataList) {
			kws.add(new KeyWord(word));
		}
		seekers.put(strKey, new KWSeeker(kws));
		this.seekers.putAll(seekers);
	}

	/**
	 * 初始化敏感词对象集合
	 * 
	 * @param strKey   唯一值,便于识别多种类型关键字的唯一标识
	 * @param dtList 唯一值标记关键词下的 关键词集合体
	 * @param strFieldValue  要识别的关键字段
	 *	 */
	public void addWords(String strKey, List<Map<String, Object>> dtList,String strFieldValue) {
		Map<String, KWSeeker> seekers = new HashMap<String, KWSeeker>();
		Set<KeyWord> kws;

		kws = new HashSet<KeyWord>();
		
		int nSizeCnt=dtList.size();
		for (int i = 0; i < nSizeCnt; i++) {
			kws.add(new KeyWord(String.valueOf(dtList.get(i).get(strFieldValue))));
		}

		seekers.put(strKey, new KWSeeker(kws));
		this.seekers.putAll(seekers);
		
	}
	
	public void removeWords (String strKey) {
		this.seekers.remove(strKey);
	}
	
	
	/*  *//**
			 * 私有构造器
			 */
	/*
	 * private SimpleKWSeekerProcessor() { initialize(); }
	 * 
	 *//**
		 * 初始化敏感词
		 *//*
			 * private void initialize() { Map<String, String> map =
			 * Config.newInstance().getAll(); Set<Entry<String, String>> entrySet =
			 * map.entrySet();
			 * 
			 * Map<String, KWSeeker> seekers = new HashMap<String, KWSeeker>(); Set<KeyWord>
			 * kws;
			 * 
			 * for (Entry<String, String> entry : entrySet) { String[] words =
			 * entry.getValue().split(","); kws = new HashSet<KeyWord>(); for (String word :
			 * words) { kws.add(new KeyWord(word)); } seekers.put(entry.getKey(), new
			 * KWSeeker(kws)); } this.seekers.putAll(seekers); }
			 */
}
