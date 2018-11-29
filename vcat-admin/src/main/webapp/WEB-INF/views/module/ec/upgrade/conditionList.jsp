<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>升级规则列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                fitColumns : true,
                url : ctx + '/ec/upgrade/conditionListData',
                columns:[[
                    {field:'periodLabel',title:'销售额累积时限'},
                    {field:'amount',title:'升级额定销售额（元）'},
                    {field:'productShow',title:'商品',formatter:function(value,row){return '<span title="' + value + '">' + abbr(value,100) + '</span>'}},
                    {field:'oper',title:'操作',width:60
                        ,formatter: function(value,row){
                            return '<a href="${ctx}/ec/upgrade/conditionForm?id=' + row.id + '">修改</a>';
                        }
                    }
                ]]
            }));
        });
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/ec/upgrade/conditionList">升级规则列表</a></li>
        <li><a href="${ctx}/ec/upgrade/conditionForm">设置升级规则</a></li>
    </ul>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                升级规则列表
            </div>
        </div>
        <div class="panel-body">
            <sys:message content="${message}"/>
            <table id="contentTable"></table>
        </div>
    </div>
</body>
</html>