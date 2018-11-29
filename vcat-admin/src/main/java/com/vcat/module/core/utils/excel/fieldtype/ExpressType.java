package com.vcat.module.core.utils.excel.fieldtype;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.entity.Express;

/**
 * 字段类型转换
 */
public class ExpressType {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		for (Express e : UserUtils.getExpressList()){
			if (StringUtils.trimToEmpty(val).equals(e.getName())){
				return e;
			}
		}
		return null;
	}

	/**
	 * 获取对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((Express)val).getName() != null){
			return ((Express)val).getName();
		}
		return "";
	}
}
