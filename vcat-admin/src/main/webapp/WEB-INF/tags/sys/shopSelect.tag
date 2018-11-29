<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="id"%>
<%@ attribute name="name" type="java.lang.String" required="false" description="name"%>
<%@ attribute name="value" type="java.lang.String" required="false" description="已选数据ID(已'|'分隔)"%>
<%@ attribute name="queryParam" type="java.lang.String" required="false" description="查询参数（URL后缀：aka061=sss&aka062=0）"%>
<%@ attribute name="radio" type="java.lang.String" required="false" description="是否单选(默认true)"%>
<%@ attribute name="onClick" type="java.lang.String" required="false" description="选择按钮点击事件"%>
<%@ attribute name="callback" type="java.lang.String" required="false" description="点击确定时需要回调的函数名称，回调函数会默认传入一个Array，包含当前选中的所有数据"%>
<%@ attribute name="showClearButton" type="java.lang.Boolean" required="false" description="是否显示清空按钮"%>
<script type="text/javascript">
    var ${id}SelectRadio = "${radio}";
    var ${id}ShowClearButton = "${showClearButton}";
    if("" != ${id}SelectRadio && "true" != ${id}SelectRadio.toLowerCase() && "1" != ${id}SelectRadio){
        ${id}SelectRadio = false;
    }else{
        ${id}SelectRadio = true;
    }
    if("" != ${id}ShowClearButton && "true" != ${id}ShowClearButton.toLowerCase() && "1" != ${id}ShowClearButton){
        ${id}ShowClearButton = false;
    }else{
        ${id}ShowClearButton = true;
    }
    var ${id}SelectCallback = eval('${callback}');
    var ${id}QueryParam = "${queryParam}";
    var ${id}SelectedIds = "";
    $(function(){
        if("" != $("#${id}").val()){
            ${id}SelectedIds = $("#${id}").val();
            ${id}RefreshData();
        }
        $("#${id}CallbackButton").click(function(){
            var onClick = '${onClick}';
            if(!isNull(onClick) && !eval(onClick)){
                return;
            }
            var iframeWidth = $(top.document).width() * 0.8;
            var iframeHeight = $(top.document).height() * 0.6;

            top.$.jBox.open("iframe:${ctx}/ec/customer/select?idsKey=${id}SelectedIds&radio="+${id}SelectRadio+"&showClearButton="+${id}ShowClearButton+"&selectedIds="+encodeURIComponent(${id}SelectedIds)+"&"+${id}QueryParam
                    , "选择用户",iframeWidth,iframeHeight,{
                buttons: {"确定":"ok",
                    <c:if test="${null == showClearButton || showClearButton}">"清除已选":"clear",</c:if>
                    "关闭":true},
                submit : function(buttonId,iframeDom){
                    if(buttonId == "ok"){
                        return ${id}DoCallBack(iframeDom);
                    }else if(buttonId == "clear"){
                        $("#${id}").val("");
                        ${id}SelectedIds = "";
                        $("#${id}DataUL").html("");
                    }
                },loaded: function (h) {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        });
    });

    function ${id}RefreshData(){
        ${id}DoCallBack(this);
    }

    function ${id}GetDataList(ids){
        var dataList = null;
        $.ajax({
            url:ctx+"/ec/customer/findListByIds?ids="+ids,
            async:false,
            success:function(dataResult){
                dataList = dataResult;
            }
        });
        return dataList;
    }

    /**
     * 执行回调函数
     * @param callBackThisLink 回调函数中this指针的对象
     * @returns {*}
     */
    function ${id}DoCallBack(callBackThisLink){
        var dataArray = ${id}GetDataList(${id}SelectedIds);

        ${id}ShowData(dataArray);

        if(isNull(dataArray)){
            return true;
        }

        if(typeof(${id}SelectCallback) == "function"){
            var callBackArg = ${id}SelectRadio && !isNull(dataArray) && dataArray.length > 0 ? dataArray[0] : dataArray;
            return ${id}SelectCallback.call(callBackThisLink,callBackArg);
        }

        return true;
    }

    /**
     * 显示已选数据
     * @param dataList
     */
    function ${id}ShowData(dataList){
        var dataIds = new Array();
        for(var i in dataList){
            dataIds.push(dataList[i].id);
        }
        $("#${id}").val(dataIds.join("|"));

        ${id}UpdateUL(dataList);
    }

    /**
     * 清除已选数据集合
     * @returns {boolean}
     */
    function ${id}ClearDataList(){
        $('#${id}DataUL').html('');
        return false;
    }

    /**
     * 删除已选数据
     * @param trashDOM
     * @param id
     */
    function ${id}DeleteData(trashDOM,id){
        ${id}SelectedIds = ${id}SelectedIds.replace('\|'+id,'');
        $(trashDOM).parent().remove();
        ${id}RefreshData();
    }

    /**
     * 更新已选列表
     * @param dataList
     */
    function ${id}UpdateUL(dataList){
        var html = "";
        var selectedIds = "";
        for(var i in dataList){
            var data = dataList[i];
            if(data){
                var liHtml = '<li title="'+data.name+'" style="margin-top: 5px">';
                liHtml += '<a href="javascript:void(0)" onclick="showViewPage(ctx + \'/ec/customer/form?id=' + data.id + '&isView=true\',\'' + data.userName + ' 用户详情\')" title="' + data.userName + '">' + data.userName + '</a>';
                liHtml += '&nbsp;&nbsp;<a href="javascript:void(0)" class="icon-trash" onclick="${id}DeleteData(this,\''+data.id+'\')"></a>';
                liHtml += '</li>';
                html += liHtml;

                selectedIds += "|" + data.id;
            }
        }
        ${id}SelectedIds = selectedIds;
        $('#${id}DataUL').html(html);
    }
</script>
<input type="hidden" id="${id}" name="${name}" value="${value}"/>
<ul id="${id}DataUL" style="list-style: none;"></ul>
<a id="${id}CallbackButton" href="javascript:void(0)" class="btn">选择用户</a>
