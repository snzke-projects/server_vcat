package com.vcat.module.core.security.shiro;

import com.vcat.module.core.utils.UserUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 验证当前用户是否为指定用户标签
 */
public class IsUserTag extends TagSupport {
	private static final long serialVersionUID = 1L;

    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void verifyAttributes() throws JspException {
        String loginName = getName();

        if (loginName == null || loginName.length() == 0) {
            String msg = "The 'name' tag attribute must be set.";
            throw new JspException(msg);
        }
    }

    public int doStartTag() throws JspException {
        verifyAttributes();

        String loginName = getName();

        if (UserUtils.isUser(loginName)) {
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            return TagSupport.SKIP_BODY;
        }
    }
}
