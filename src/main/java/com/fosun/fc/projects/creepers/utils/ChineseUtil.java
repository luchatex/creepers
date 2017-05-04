package com.fosun.fc.projects.creepers.utils;

import org.apache.commons.lang.math.RandomUtils;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.BaseConstant.TaskListType;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 
 * <p>
 * description:中文处理工具包
 * </p>
 * 
 * @author MaXin
 * @since 2016-12-6 09:58:16
 * @see
 */
public class ChineseUtil {

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     * 
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strResult = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strResult != null && strResult.length > 0) {
                        pinyinName.append(strResult[0].charAt(0));
                    } else {
                        pinyinName.append(nameChar[i]);
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        if (pinyinName.toString().contains("u:")) {
            return pinyinName.toString().replaceAll("u:", "v");
        } else {
            return pinyinName.toString();
        }
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     * 
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        // long start = System.currentTimeMillis();
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strResult = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strResult != null && strResult.length > 0) {
                        pinyinName.append(strResult[0]);
                    } else {
                        pinyinName.append(nameChar[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        // long end = System.currentTimeMillis();
        // System.out.println("cost time : " + (end - start));
        if (pinyinName.toString().contains("u:")) {
            return pinyinName.toString().replaceAll("u:", "v");
        } else {
            return pinyinName.toString();
        }
    }

    public static void main(String[] args) {
        TaskListType[] list = BaseConstant.TaskListType.values();

        for (int i = 0; i < 100; i++) {
            System.out.println(i + ":" + converterToSpell("注册人名称)）" + list[RandomUtils.nextInt(list.length)].getNameChinese()));
        }
        // System.out.println(converterToSpell("注册人名称"));
    }
}
