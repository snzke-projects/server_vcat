/*!
 * 通用公共方法
 */
$(document).ready(function () {
    try {
        // 链接去掉虚框
        $("a").bind("focus", function () {
            if (this.blur) {
                this.blur()
            }
            ;
        });

        //所有下拉框使用select2
        $("select").select2();

        //扩展弹出框可拖拽插件
        $('.vcat-dialog').each(function () {
            if (this.id) {
                $("#" + this.id).drap({drapMove: ".panel-heading", isLimit: true});
            }
        });

        //关闭请稍候Tip控件
        closeTip();

        //配置getJSON函数异常处理
        $.ajaxSetup({
            error: function (e) {
                doError(e);
                return false;
            }
        });
    } catch (e) {
        // blank
    }
});

// 引入js和css文件
function include(id, path, file) {
    if (document.getElementById(id) == null) {
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++) {
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
            var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
            var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
            document.write("<" + tag + (i == 0 ? " id=" + id : "") + attr + link + "></" + tag + ">");
        }
    }
}

// 获取URL地址参数
function getQueryString(name, url) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    if (!url || url == "") {
        url = window.location.search;
    } else {
        url = url.substring(url.indexOf("?"));
    }
    r = url.substr(1).match(reg)
    if (r != null) return unescape(r[2]);
    return null;
}

//获取字典标签
function getDictLabel(data, value, defaultValue) {
    for (var i = 0; i < data.length; i++) {
        var row = data[i];
        if (row.value == value) {
            return row.label;
        }
    }
    return defaultValue;
}

// 打开一个窗体
function windowOpen(url, name, width, height) {
    var top = parseInt((window.screen.height - height) / 2, 10), left = parseInt((window.screen.width - width) / 2, 10),
        options = "location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes," +
            "resizable=yes,scrollbars=yes," + "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left;
    window.open(url, name, options);
}

// 恢复提示框显示
function resetTip() {
    top.$.jBox.tip.mess = null;
}

// 关闭提示框
function closeTip() {
    top.$.jBox.closeTip();
}

//显示提示框
function showTip(mess, type, timeout, lazytime) {
    resetTip();
    setTimeout(function () {
        top.$.jBox.tip(mess, (type == undefined || type == '' ? 'info' : type), {
            opacity: 0,
            timeout: timeout == undefined ? 2000 : timeout
        });
    }, lazytime == undefined ? 500 : lazytime);
}

// 显示加载框
function loading(mess) {
    if (mess == undefined || mess == "") {
        mess = "正在提交，请稍等...";
    }
    resetTip();
    top.$.jBox.tip(mess, 'loading', {opacity: 0});
}

// 警告对话框
function alertx(mess, closed, width) {
    top.$.jBox.info(mess, '提示', {
        closed: function () {
            if (typeof closed == 'function') {
                closed();
            }
        }, width: width
    });
    top.$('.jbox-body .jbox-icon').css('top', '55px');
}

// 确认对话框
function confirmx(mess, href, closed, width) {
    top.$.jBox.confirm(mess, '系统提示', function (v, h, f) {
        if (v == 'ok') {
            if (typeof href == 'function') {
                href();
            } else {
                resetTip(); //loading();
                location = href;
            }
        }
    }, {
        buttonsFocus: 1, closed: function () {
            if (typeof closed == 'function') {
                closed();
            }
        }, width: width
    });
    top.$('.jbox-body .jbox-icon').css('top', '55px');
    return false;
}

// 提示输入对话框
function promptx(title, lable, href, closed) {
    top.$.jBox("<div class='form-search' style='padding:20px;text-align:center;'>" + lable + "：<input type='text' id='txt' name='txt'/></div>", {
        title: title, submit: function (v, h, f) {
            if (f.txt == '') {
                top.$.jBox.tip("请输入" + lable + "。", 'error');
                return false;
            }
            if (typeof href == 'function') {
                href();
            } else {
                resetTip(); //loading();
                location = href + encodeURIComponent(f.txt);
            }
        }, closed: function () {
            if (typeof closed == 'function') {
                closed();
            }
        }
    });
    return false;
}

// 添加TAB页面
function addTabPage(title, url, closeable, $this, refresh) {
    top.$.fn.jerichoTab.addTab({
        tabFirer: $this,
        title: title,
        closeable: closeable == undefined,
        data: {
            dataType: 'iframe',
            dataLink: url
        }
    }).loadData(refresh != undefined);
}

// cookie操作
function cookie(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        var path = options.path ? '; path=' + options.path : '';
        var domain = options.domain ? '; domain=' + options.domain : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
}

// 数值前补零
function pad(num, n) {
    var len = num.toString().length;
    while (len < n) {
        num = "0" + num;
        len++;
    }
    return num;
}

// 转换为日期
function strToDate(date) {
    return new Date(date.replace(/-/g, "/"));
}

// 日期加减
function addDate(date, dadd) {
    date = date.valueOf();
    date = date + dadd * 24 * 60 * 60 * 1000;
    return new Date(date);
}

//截取字符串，区别汉字和英文
function abbr(name, maxLength) {
    if (!maxLength) {
        maxLength = 20;
    }
    if (name == null || name.length < 1) {
        return "";
    }
    var w = 0;//字符串长度，一个汉字长度为2
    var s = 0;//汉字个数
    var p = false;//判断字符串当前循环的前一个字符是否为汉字
    var b = false;//判断字符串当前循环的字符是否为汉字
    var nameSub;
    for (var i = 0; i < name.length; i++) {
        if (i > 1 && b == false) {
            p = false;
        }
        if (i > 1 && b == true) {
            p = true;
        }
        var c = name.charCodeAt(i);
        //单字节加1
        if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
            w++;
            b = false;
        } else {
            w += 2;
            s++;
            b = true;
        }
        if (w > maxLength && i <= name.length - 1) {
            if (b == true && p == true) {
                nameSub = name.substring(0, i - 2) + "...";
            }
            if (b == false && p == false) {
                nameSub = name.substring(0, i - 3) + "...";
            }
            if (b == true && p == false) {
                nameSub = name.substring(0, i - 2) + "...";
            }
            if (p == true) {
                nameSub = name.substring(0, i - 2) + "...";
            }
            break;
        }
    }
    if (w <= maxLength) {
        return name;
    }
    return nameSub;
}

/**
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q) 可以用 1-2 个占位符
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * eg:
 * (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 * (new Date()).pattern("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04
 * (new Date()).pattern("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04
 * (new Date()).pattern("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04
 * (new Date()).pattern("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */
Date.prototype.pattern = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()
        //毫秒
    };
    var week = {
        "0": "/u65e5",
        "1": "/u4e00",
        "2": "/u4e8c",
        "3": "/u4e09",
        "4": "/u56db",
        "5": "/u4e94",
        "6": "/u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f" : "/u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}
/**
 * 日期天数加减函数
 * @param d
 * @returns {Date}
 */
Date.prototype.addDays = function (d) {
    this.setDate(this.getDate() + d);
    return this;
};
/**
 * 日期天数加减函数
 * @param d
 * @returns {Date}
 */
Date.prototype.addWeeks = function (w) {
    this.addDays(w * 7);
    return this;
};

Date.prototype.addMonths = function (m) {
    var d = this.getDate();
    this.setMonth(this.getMonth() + m);
    if(this.getDate() < d){
        this.setDate(0);
    }
    return this;
};

Date.prototype.addYears = function (y) {
    var m = this.getMonth();
    this.setFullYear(this.getFullYear() + y);
    if (m < this.getMonth()) {
        this.setDate(0);
    }
    return this;
};

/**
 * 判断对象是否为函数
 * @param func 需要判断的对象
 * @returns {boolean}
 */
function isFunction(func) {
    return "function" === typeof(func);
}

/**
 * 判断对象是否为数组
 * @param array 需要判断的对象
 * @returns {boolean}
 */
function isArray(array) {
    return Object.prototype.toString.call(array) === '[object Array]';
}

/**
 * 初始化遮罩层
 */
function initLayer() {
    // 加载遮罩层DIV
    var layerDIVHTML = "<div id='layerDIV'></div>";
    $("body").append(layerDIVHTML);
}

/**
 * 显示化遮罩层
 * @param func 遮罩层点击回调函数
 */
function showLayer(func) {
    if ($('#layerDIV').length == 0) {
        initLayer();
    }
    if ($('#layerDIV').hasClass('layer')) {
        return;
    }
    if (isFunction(func)) {
        $('#layerDIV').bind("click", function () {
            if (func.call(this)) {
                $('#layerDIV').unbind("click");
            }
        });
    }
    $('#layerDIV').addClass('layer');
}

/**
 * 隐藏化遮罩层
 */
function hideLayer() {
    if ($('#layerDIV').length == 0) {
        initLayer();
    }
    $('#layerDIV').removeClass('layer');
    return true;
}

/**
 * 显示窗口
 * @id 需要显示的窗口ID
 */
function showDialog(id, title) {
    if (undefined == window.dialogIds) {
        window.dialogIds = "";
    }
    if ($('#' + id).is(":hidden")) {
        window.dialogIds += "|" + id;
        if ("string" === typeof(title)) {
            $('#' + id + ' .panel-title').text(title);
            if ($('#' + id + ' .panel-title .icon-remove').length == 0) {
                $('#' + id + ' .panel-title').append('<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents(\'.vcat-dialog\').attr(\'id\'))"></a>');
            }
        }
        showLayer(function () {
            return hideDialog(id, false);
        });
        $('#' + id).center();
        $('#' + id).show('slow');
    } else {
        hideDialog(id, true);
    }
}

/**
 * 隐藏窗口
 * @param id 需要隐藏的窗口ID
 * @param fromClose 标识该函数调用者是否来自于常规关闭按钮
 * @returns boolean
 */
function hideDialog(id, fromClose) {
    var idArray = window.dialogIds.split("|");
    if (idArray.length == 2) {
        window.dialogIds = "";
        $('#' + idArray[1]).hide('slow');
        return hideLayer();
    }
    if (fromClose) {
        window.dialogIds = window.dialogIds.replace("|" + id, "");
        $('#' + id).hide('slow');
    } else if (idArray.length > 2) {
        var lastId = idArray[idArray.length - 1];
        $('#' + lastId).hide('slow');
        window.dialogIds = window.dialogIds.replace("|" + lastId, "");
    }
    return false;
}

/**
 * 处理页面异常以及AJAX异常
 * @param error 可为页面异常以及AJAX错误请求对象
 * @returns {boolean}
 */
function doError(error) {
    try {
        if (isNull(error)) {
            return false;
        }

        //清除正在显示的Tip
        top.$.jBox.closeTip();
        /**
         * 获取错误信息HTML
         * @param text 错误
         * @returns {*}
         */
        function replaceErrorMsgHTML(text) {
            text = text.replace(/\r\n/g, "<BR>");
            text = text.replace(/\n/g, "<BR>");
            text = text.replace(/\t/g, "<SPAN>　　</SPAN>");
            text = "<SPAN STYLE='COLOR:RED'>" + text + "</SPAN>";
            return text;
        }

        // 判断是否为AJAX请求异常
        if (!isNull(error.responseText)) {
            var errorRow = error.responseText.substring(error.responseText.indexOf('\tat '), error.responseText.indexOf(")"));
            var errorRowLeft = errorRow.split("(")[0];
            var pointArray = errorRowLeft.split(".");
            var methodName = pointArray[pointArray.length - 1];
            var className = pointArray[pointArray.length - 2];
            var rowNum = "Native Method";
            var rowArray = errorRow.split(":");
            if (!isNull(rowArray) && rowArray.length > 0) {
                rowNum = rowArray[rowArray.length - 1];
            }
            var text = error.responseText;
            if (text.indexOf("java.lang.NullPointerException") >= 0 && text.indexOf("java.lang.NullPointerException") < text.indexOf("\tat ")) {
                text = "空指针异常！";
            } else if (text.indexOf("Exception:") > 0) {
                // 匹配行开头为"at "的字符
                text = text.substring(text.indexOf("Exception:") + 10, text.indexOf("\tat "));
            }
            text = "程序出现错误，请联系管理员：\r\n\t" + text;
            if (text.lastIndexOf("\r") != text.length - 1 && text.lastIndexOf("\n") != text.length - 1) {
                text += "\r\n";
            }
            text += "错误源自：\r\n\t" + className + " 类 第 " + rowNum + " 行 " + methodName + " 方法";
            alertx(replaceErrorMsgHTML(text), null, 700);
            return false;
        }
        // 判断是否为JS异常
        if (!isNull(error.message)) {
            var errorRow = error.stack.substring(error.stack.indexOf('at '), error.stack.indexOf(")"));
            var errorFunctionName = errorRow.substring(errorRow.lastIndexOf('\.') + 1, errorRow.indexOf("(") - 1);
            var functionRow = errorRow.split(":");
            var msg = "本页面代码执行出错，请联系管理员：\r\n\t" + error.message;
            msg += "\r\n错误源自：\r\n\t本页面代码第 " + functionRow[functionRow.length - 2] + " 行 " + errorFunctionName + " 方法";
            alertx(replaceErrorMsgHTML(msg), null, 700);
            return false;
        }
        showLayer();
        confirmx(replaceErrorMsgHTML("连接服务器失败，点击【确定】刷新页面！"), location.href, function () {
            hideLayer();
        });
        return false;
    } catch (e) {
        alertx("系统出现错误，请联系管理员：<br><span>　　</span>" + e.message);
        return false;
    }
}

/**
 * 将此字符串转换为Jquery选择器可识别的字符串（转义）
 * @returns {string}
 */
String.prototype.toJQS = function () {
    var str = this;
    str = str.replace(/:/g, "\\:");
    str = str.replace(/\./g, "\\.");
    str = str.replace(/\//g, "\\/");
    str = str.replace(/\$/g, "\\$");
    str = str.replace(/\[/g, "\\[");
    str = str.replace(/'/g, "\\'");
    str = str.replace(/"/g, "\\\"");
    str = str.replace(/\*/g, "\\\*");
    return str.replace(/\]/g, "\\]");
}

/**
 * 检查指定ID Input值是否为空，如果为空则返回真，并提示输入
 * @param id
 * @param title
 * @returns {boolean}
 */
function checkNull(id, title) {
    if (isNull(id) || isNull(title)) {
        return true;
    }
    var target = $("#" + id);
    if (isNull(target.val())) {
        alertx("请填写" + title + "！", function () {
            target.focus();
        });
        return true;
    }
    return false;
}

/**
 * 查看详情页面
 * @param url
 * @param title
 */
function showViewPage(url, title, closed) {
    top.$.jBox.open("iframe:" + url, title, $(top.document).width() - 220, $(top.document).height() - 180, {
        buttons: {"确定": true},
        closed: function () {
            if (typeof closed == 'function') {
                closed();
            }
        }
    });
}

/**
 * 阻止事件冒泡
 * @param e
 */
function stopBubble(e) {
    // 如果传入了事件对象，那么就是非ie浏览器
    if (e && e.stopPropagation) {
        //因此它支持W3C的stopPropagation()方法
        e.stopPropagation();
    } else {
        //否则我们使用ie的方法来取消事件冒泡
        window.event.cancelBubble = true;
    }
}

/**
 * 自定义数据加载函数执行方式
 * 删除datagrid自带分页参数，改为后台框架分页参数
 * 当加载出现异常时，调用自定义处理异常的函数
 * data 查询参数（包含分页参数）
 * success 嵌入数据函数
 * error 异常函数
 * @returns {boolean}
 */
function datagridAjaxLoader(data, success, error) {
    data.pageNo = data.page;    // 初始化框架分页参数
    data.pageSize = data.rows;
    delete data.page;          // 删除easyui自带分页参数
    delete data.rows;
    var opts = $(this).datagrid("options");
    if (!opts.url)return false;// 如果url为空，则跳过数据请求
    $.ajax({
        type: opts.method,
        url: opts.url,
        data: data,
        dataType: "json",
        success: function (data) {
            if (!data.rows)data.rows = [] // 如果数据为空，则初始化为空数组，避免jQuery抛出异常
            success(data);
        }, error: function () {
            error.apply(this, arguments);
            doError.apply(this, arguments);
        }
    });
}

/**
 * 获取datagrid默认参数
 * @param option
 * @returns {}
 */
function getDatagridOption(option) {
    var dOption = {
        url: '',                    // 默认不加载
        method: 'get',
        pageSize: 20,               // 默认单页数量限制
        pageList: [5, 10, 15, 20, 25, 30, 40, 50, 70, 100], // 单页数量限制下拉框数组
        fitColumns: false,          // 是否自适应宽度
        singleSelect: false,        // 是否单选
        showFooter: false,          // 是否显示页脚
        pagination: true,           // 是否显示分页
        rownumbers: true,           // 是否显示行号
        autoRowHeight: false,       // 是否自动设定行高，不设定可提高加载速度
        loader: datagridAjaxLoader // 默认加载方法
    };
    return !isNull(option) ? $.extend(dOption, option) : dOption;
}

/**
 * 获取 DataGrid 选中项的ID URL参数字符串
 * @param gridSelector DataGrid的jQuery选择器字符串
 * @param idFieldName id的属性名
 * @param idParamName id的参数名
 * @returns {}
 */
function getCheckIdArrayParamString(gridSelector, idFieldName, idParamName) {
    idParamName = idParamName ? idParamName : "";
    var checkedBox = $(gridSelector).datagrid('getChecked');
    if (checkedBox.length == 0) {
        alertx("请勾选目标！");
        throw new Error("未勾选目标，阻止代码继续执行！");
    }
    var orderIdArray = [];
    $.each(checkedBox, function (index, item) {
        orderIdArray.push(idParamName + "=" + eval("item." + idFieldName));
    });
    return orderIdArray.join("&");
}

/**
 * 获取表单中带参数的url
 * @param url
 * @param formSelector 默认为#searchForm
 */
function getFormParamUrl(url, formSelector) {
    if (!url)return "";
    if (url.indexOf(ctx) == 0)return url;
    if (!formSelector)formSelector = "#searchForm";
    return ctx + url + (url.indexOf("?") > 0 ? "" : "?") + (url.indexOf("&") > 0 || url.indexOf("&") > 0 ? "&" : "") + $(formSelector).serialize();
}

/**
 * 刷新 DataGrid
 * @param url
 * @param formSelector 默认为#searchForm
 * @param gridSelector 默认为#contentTable
 */
function reloadGrid(url, formSelector, gridSelector) {
    if (!url)return;
    url = getFormParamUrl(url, formSelector);
    if (!gridSelector) {
        gridSelector = "#contentTable";
    }
    $(gridSelector).datagrid('loadData', {total: 0, rows: []});
    $(gridSelector).datagrid('reload', url);
}

/**
 * 扩展jQuery居中函数
 * @returns {jQuery}
 */
jQuery.fn.center = function () {
    this.css("position", "absolute");
    this.css("top", ($(window).height() - this.height()) / 2 + $(window).scrollTop() + "px");
    this.css("left", ($(window).width() - this.width()) / 2 + $(window).scrollLeft() + "px");
    return this;
}

/**
 * 为数字增加精确的四舍五入方法，修复JS中Float精度丢失的问题
 * @param d
 * @returns {string}
 */
Number.prototype.toRound = function(d){
    var s = this + "";
    if (!d)d = 0;
    if (s.indexOf(".") == -1)s += ".";
    s += new Array(d + 1).join("0");
    if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (d + 1) + "})?)\\d*$").test(s)) {
        var s = "0" + RegExp.$2, pm = RegExp.$1, a = RegExp.$3.length, b = true;
        if (a == d + 2) {
            a = s.match(/\d/g);
            if (parseInt(a[a.length - 1]) > 4) {
                for (var i = a.length - 2; i >= 0; i--) {
                    a[i] = parseInt(a[i]) + 1;
                    if (a[i] == 10) {
                        a[i] = 0;
                        b = i != 1;
                    } else break;
                }
            }
            s = a.join("").replace(new RegExp("(\\d+)(\\d{" + d + "})\\d$"), "$1.$2");
        }
        if (b)s = s.substr(1);
        return (pm + s).replace(/\.$/, "");
    }
    return this + "";
};


/**
 * 精确加法
 * @param arg
 * @returns {number}
 */
Number.prototype.add = function(arg) {
    var r1, r2, m;
    try{r1 = this.toString().split(".")[1].length;}catch(e){r1 = 0;}
    try{r2 = arg.toString().split(".")[1].length;}catch(e){r2 = 0;}
    m = Math.pow(10, Math.max(r1, r2));
    return (this * m + arg * m) / m;
};

/**
 * 精确减法
 * @param arg
 */
Number.prototype.sub = function(arg) {
    return this.add(-arg);
};

/**
 * 精确乘法
 * @param arg
 * @returns {number}
 */
Number.prototype.mul = function(arg) {
    var m = 0, s1 = this.toString(), s2 = arg.toString();
    try{m += s1.split(".")[1].length;}catch(e){}
    try{m += s2.split(".")[1].length;} catch(e){}
    return Number(s1.replace(".", "")) * Number(s2.replace(".", ""))/ Math.pow(10, m);
};

/**
 * 精确除法
 * @param arg
 * @returns {number}
 */
Number.prototype.div = function(arg) {
    var t1 = 0, t2 = 0, r1, r2;
    try {t1 = this.toString().split(".")[1].length;}catch(e){}
    try {t2 = arg.toString().split(".")[1].length;}catch(e){}
    with (Math) {
        r1 = Number(this.toString().replace(".", ""));
        r2 = Number(arg.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
};

/**
 * 获取文件大小显示Label
 * @param sizeString
 * @returns {*}
 */
function getSizeLabel(sizeString){
    if(isNull(sizeString))return "未知大小";
    var size = parseInt(sizeString);
    var label;
    if (size < 1024) {
        label = size + "KB";
    }
    if (size > 1024 * 1024) {
        label = size / (1024 * 1024) + "MB";
    }
    return label;
}

/**
 * 获取URL中参数值
 * @param sizeString
 * @returns {*}
 */
function getUrlParam(url,name) {
    var reg = new RegExp("(^|&|[?])" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
    var r = url.match(reg);  // 匹配目标参数
    if (r != null) return decodeURIComponent(r[2]); return null; // 返回参数值
}

/**
 * 获取父级激活窗口
 * @returns {*}
 */
function getOpenWindow(){
    if (top.mainFrame.cmsMainFrame){
        return top.mainFrame.cmsMainFrame;
    }else if($('.curholder',parent.document).find('iframe').length > 0){ // 如果开启了页签模式，则获取父窗口的激活iframe中的articleSelect
        return eval("parent.document." + $('.curholder',parent.document).find('iframe').attr('id'));
    }else{
        return top.mainFrame;
    }
}
