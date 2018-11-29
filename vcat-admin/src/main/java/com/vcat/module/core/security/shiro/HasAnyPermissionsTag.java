package com.vcat.module.core.security.shiro;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.utils.UserUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

/**
 * Shiro HasAnyPermissions Tag.
 */
public class HasAnyPermissionsTag extends PermissionTag {

	private static final long serialVersionUID = 1L;
	private static final String PERMISSION_NAMES_DELIMETER = ",";

	@Override
	protected boolean showTagBody(String permissionNames) {
        if(StringUtils.isEmpty(permissionNames)){
            return true;
        }
		return UserUtils.hasAnyPermission(permissionNames.split(PERMISSION_NAMES_DELIMETER));
	}

}
