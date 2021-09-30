package com.cn.mqi.words.processor;

import com.cn.mqi.words.sensitive.KeyWord;
import org.apache.commons.lang3.StringUtils;



/**
 * 高亮的方式。

 *
 */
public class HTMLFragment extends AbstractFragment {

    /**
     * 开口标记
     */
    private String open;

    /**
     * 封闭标记
     */
    private String close;

    /**
     * 初始化开闭标签
     * 
     * @param open 开始标签
     * @param close 结束标签
     */
    public HTMLFragment(String open, String close) {
        this.open = StringUtils.trimToEmpty(open);
        this.close = StringUtils.trimToEmpty(close);
    }

    @Override
    public String format(KeyWord word) {
        return open + word.getWord() + close;
    }
}
