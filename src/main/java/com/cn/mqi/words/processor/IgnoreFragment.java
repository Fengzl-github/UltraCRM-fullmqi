package com.cn.mqi.words.processor;


import com.cn.mqi.words.sensitive.KeyWord;

/**
 * 
 * 替换内容的片段处理方式

 */
public class IgnoreFragment extends AbstractFragment {

    private String ignore = "";

    public IgnoreFragment() {
    }

    public IgnoreFragment(String ignore) {
        this.ignore = ignore;
    }

    @Override
    public String format(KeyWord word) {
        return ignore;
    }

}
