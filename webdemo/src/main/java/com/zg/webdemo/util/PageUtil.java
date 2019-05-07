package com.zg.webdemo.util;

import com.github.pagehelper.PageInfo;
import com.zg.webdemo.entity.PageEntity;

import java.util.List;

/**
 * 分页工具类
 * @Class Name PageUtil
 * @author zhujie
 * @Create In 2016年9月23日
 */
public class PageUtil {

	public static long COUNT_ZERO = 0;
	

	
	public static <T> void convertPage(List<T> listT, PageEntity page) {
		PageInfo<T> resultPage = new PageInfo<T>(listT);
		page.setTotalResultSize(new Long(resultPage.getTotal()).intValue());
		page.setTotalPageSize(resultPage.getPages());
	}

	
}
