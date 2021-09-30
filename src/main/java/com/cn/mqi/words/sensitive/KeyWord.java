package com.cn.mqi.words.sensitive;

import java.io.Serializable;

/**
 *@Author fengzhilong
 *@Date 2021/9/28 15:26
 *@Desc
 **/
public class KeyWord implements Serializable {
    private static final long serialVersionUID = -2409138800127133451L;

    /**
     * 关键词内容
     */
    private String word;
    /**
     * （单字符）词的前缀,支持正则
     */
    private String pre;
    /**
     * （单字符）词的后缀，支持正则
     */
    private String sufix;
    /**
     * 关键词长度
     */
    private Integer wordLength;

    public KeyWord(String word) {
        this.word = word;
        this.wordLength = word.length();
    }

    public KeyWord(String word, String pre, String sufix) {
        this(word);
        this.pre = pre;
        this.sufix = sufix;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

    public Integer getWordLength() {
        return wordLength;
    }

    public void setWordLength(Integer wordLength) {
        this.wordLength = wordLength;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyWord keyWord = (KeyWord) o;
        if (word == null) {
            if (keyWord.word != null)
                return false;
        } else if (!word.equals(keyWord.word))
            return false;
        if (wordLength != keyWord.wordLength)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        result = prime * result + wordLength;
        return result;
    }


    @Override
    public String toString() {
        return "KeyWord[word=" + word + ", wordLength=" + wordLength + "]";
    }
}
