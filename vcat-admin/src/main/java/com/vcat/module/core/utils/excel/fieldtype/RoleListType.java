package com.vcat.module.core.utils.excel.fieldtype;

import java.util.List;

import com.google.common.collect.Lists;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.Collections3;
import com.vcat.module.core.entity.Role;
import com.vcat.module.core.utils.UserUtils;

/**
 * 字段类型转换
 */
public class RoleListType {	
	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		List<Role> roleList = Lists.newArrayList();
		List<Role> allRoleList = findAllRole();
		for (String s : StringUtils.split(val, ",")){
			for (Role e : allRoleList){
				if (StringUtils.trimToEmpty(s).equals(e.getName())){
					roleList.add(e);
				}
			}
		}
		return roleList.size()>0?roleList:null;
	}
	
	public static List<Role> findAllRole() {
		return UserUtils.getRoleList();
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null){
			@SuppressWarnings("unchecked")
			List<Role> roleList = (List<Role>)val;
			return Collections3.extractToString(roleList, "name", ", ");
		}
		return "";
	}
	
}
