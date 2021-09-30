package com.cn.mqi.words.sensitive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 敏感词管理器
 * 
 *
 */
public class KWSeekerManage {

    /**
     * 敏感词模块. key为模块名，value为对应的敏感词搜索器
     */
    protected Map<String, KWSeeker> seekers = new ConcurrentHashMap<String, KWSeeker>();

    /**
     * 初始化
     */
    public KWSeekerManage() {
    }

    /**
     * 
     * @param seekers 寻找者
     */
    public KWSeekerManage(Map<String, KWSeeker> seekers) {
        this.seekers.putAll(seekers);
    }

    /**
     * 获取一个敏感词搜索器
     * 
     * @param wordType 单词类型
     * @return 返回 KWSeeker
     */
    public KWSeeker getKWSeeker(String wordType) {
        return seekers.get(wordType);
    }

    /**
     * 加入一个敏感词搜索器
     * 
     * @param wordType 单词类型
     * @param kwSeeker kv 寻找者
     */
    public void putKWSeeker(String wordType, KWSeeker kwSeeker) {
        seekers.put(wordType, kwSeeker);
    }

    /**
     * 移除一个敏感词搜索器
     * 
     * @param wordType 单词类型
     */
    public void removeKWSeeker(String wordType) {
        seekers.remove(wordType);
    }

    /**
     * 清除空搜索器
     */
    public void clear() {
        seekers.clear();
    }

}
