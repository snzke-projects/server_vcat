/*
 用途：检查输入字符串是否为空或者全部都是空格
 输入：value 需要判断的值
 返回：为空则返回true，反之返回false
 */
function isNull(value){
    return undefined === value || null === value || new RegExp("^[ ]?$").test(value);
}

/*
 用途：检查值是否为空，如果为空则返回默认值
 输入：value 输入的值
 输入：defaultValue 默认值
 返回：如果为空则返回默认值，如果不为空则返回本身
 */
function getDefault(value , defaultValue){
    if(isNull(value)){return defaultValue;}
    return value;
}

/*
 用途：检查输入对象的值是否符合整数格式
 输入：str 输入的字符串
 返回：如果通过验证返回true,否则返回false
 */
function isInteger( str ){
    var regu = /^[-]{0,1}[0-9]{1,}$/;
    return regu.test(str);
}

/*
 用途：检查输入字符串是否符合正整数格式
 输入：s 字符串
 返回：如果通过验证返回true,否则返回false
 */
function isNumber( s ){
    var regu = "^[0-9]+$";
    var re = new RegExp(regu);
    if (s.search(re) != -1) {
        return true;
    } else {
        return false;
    }
}

/*
 用途：检查输入字符串是否是带小数的数字格式,可以是负数
 输入：s 字符串
 返回：如果通过验证返回true,否则返回false
 */
function isDecimal( str ){
    if(isInteger(str)) return true;
    var re = /^[-]{0,1}(\d+)[\.]+(\d+)$/;
    if (re.test(str)) {
        return true;
    } else {
        return false;
    }
}

/*
 用途：检查输入对象的值是否符合E-Mail格式
 输入：str 输入的字符串
 返回：如果通过验证返回true,否则返回false
 */
function isEmail( str ){
    var myReg = /^[-_A-Za-z0-9]+@([_A-Za-z0-9]+\.)+[A-Za-z0-9]{2,3}$/;
    if(myReg.test(str)) return true;
    return false;
}

/*
 用途：
    检查输入字符串是否符合金额格式
    格式定义为带小数的正数，小数点后最多三位
 输入：s 字符串
 返回：如果通过验证返回true,否则返回false
 */
function isMoney( s ){
    if(isNumber(s)){
        return true;
    }
    var regu = "^[0-9]+[\.][0-9]{0,3}$";
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    } else {
        return false;
    }
}
/*
 用途：检查输入字符串是否只由英文字母和数字和下划线组成
 输入：s 字符串
 返回：如果通过验证返回true,否则返回false
 */
function isLetter( s ){//判断是否是数字或字母
    var regu = "^[0-9a-zA-Z\_]+$";
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }else{
        return false;
    }
}

/*
 用途：检查输入字符串是否只由汉字、字母、数字组成
 输入：value 字符串
 返回：如果通过验证返回true,否则返回false
 */
function isChinaOrNumbOrLett( s ){//判断是否是汉字、字母、数字组成

    var regu = "^[0-9a-zA-Z\u4e00-\u9fa5]+$";
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }else{
        return false;
    }
}

/*
 用途：检查输入的Email信箱格式是否正确
 输入：strEmail 字符串
 返回：如果通过验证返回true,否则返回false
 */
function checkEmail(strEmail) {
//var emailReg = /^[_a-z0-9]+@([_a-z0-9]+\.)+[a-z0-9]{2,3}$/;
    var emailReg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
    if( emailReg.test(strEmail) ){
        return true;
    }else{
        alert("您输入的Email地址格式不正确！");
        return false;
    }
}