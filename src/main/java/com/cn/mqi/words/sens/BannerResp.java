
package com.cn.mqi.words.sens;

import java.util.ArrayList;
import java.util.List;

public class BannerResp {
	/**
	 * 是否有要识别的关键词
	 */
	private boolean hasSensitiveWords;
	
	private List<String> sensitiveWords = new ArrayList<String>();
	
	private String filterStr;

	public boolean isHasSensitiveWords() {
		return hasSensitiveWords;
	}

	public void setHasSensitiveWords(boolean hasSensitiveWords) {
		this.hasSensitiveWords = hasSensitiveWords;
	}

	public List<String> getSensitiveWords() {
		return sensitiveWords;
	}

	public void setSensitiveWords(List<String> sensitiveWords) {
		this.sensitiveWords = sensitiveWords;
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}
	
	public void addSensitiveWord(String str){
		sensitiveWords.add(str);
	}

	@Override
	public String toString() {
		return "BannerResp [hasSensitiveWords=" + hasSensitiveWords
				+ ", sensitiveWords=" + sensitiveWords + ", filterStr="
				+ filterStr + "]";
	}
}