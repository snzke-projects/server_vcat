<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>优惠卷列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                fitColumns : true,
                url : ctx + '/ec/promotion/listData?'+$('#searchForm').serialize(),
                columns:[[
                    {field:'name',title:'优惠卷名称'},
                    {field:'freeCount',title:'份数'},
                    {field:'buyCount',title:'使用条件'},
                    {field:'note',title:'使用备注'
                        ,formatter: function (value,row){
                            return "购买【" + row.buyCount + "】个商品，赠送【" + row.freeCount + "】个";
                        }
                    },
                    {field:'isActivate',title:'激活状态'
                        ,styler: function (value,row){
                            return "color: " + (value === 1 ? "green" : "red");
                        }
                        ,formatter: function (value,row){
                            return value === 1 ? "已激活" : "未激活";
                        }
                    },
                    {field:'operBy',title:'最后操作人'
                        ,formatter: function (value,row){
                            return row.operBy.name;
                        }
                    },
                    {field:'operDate',title:'最后操作时间'},
                    {field:'createDate',title:'创建时间'},
                    {field:'oper',title:'操作'
                        ,formatter: function(value,row){
                            var html = '<a href="' + ctx + '/ec/promotion/form?id=' + row.id + '">修改</a>';
                            html += '<a href="javascript:void(0)" onclick="stopBubble();activate(\'' + row.id + '\',' + (row.isActivate === 1 ? 0 : 1) + ')"> ' + (1 === row.isActivate ? "取消" : "") + '激活</a>';
                            return html;
                        }
                    }
                ]]
            }));
        });
        function clearForm(){
            $('#keyWord').val('');
        }
        function activate(id,isActivate){
            var label = 1 === isActivate ? "" : "取消";
            confirmx("确认<span style='color: red'>" + label + "激活</span>该优惠卷？",function (){
                $.get(ctx + "/ec/promotion/activate?id=" + id + "&isActivate=" + isActivate,function(){
                    alertx(label + "激活成功！",function(){
                        reloadGrid('/ec/promotion/listData');
                    });
                });
            });
        }
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/ec/promotion/list">优惠卷列表</a></li>
        <li><a href="${ctx}/ec/promotion/form?id=${promotion.id}"><shiro:hasPermission name="ec:promotion:edit">${not empty promotion.id?'修改':'设置'}</shiro:hasPermission>优惠卷</a></li>
    </ul>
	<form:form id="searchForm" modelAttribute="promotion" action="${ctx}/ec/promotion/list" method="post" class="breadcrumb form-search">
        <label>关键字：</label>
        <form:input id="keyWord" path="sqlMap['keyWord']" maxlength="50" class="input-large"/>&nbsp;
        <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/promotion/listData')"/>
        <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
	</form:form>
    <sys:message content="${message}"/>
	<table id="contentTable"></table>
</body>
</html>