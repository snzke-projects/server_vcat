package com.vcat.common;

import com.vcat.common.web.BaseController;
import com.vcat.module.common.QRCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "${adminPath}/common")
public class CommonController extends BaseController {
    @ResponseBody
    @RequestMapping("getQRCodeImagePath")
    public String getQRCodeImagePath(String content) {
        return QRCodeUtil.create(content);
    }
}
