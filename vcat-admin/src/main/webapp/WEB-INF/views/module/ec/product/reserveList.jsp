<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>商品上架预告列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                fitColumns : true,
                url : ctx + '/ec/product/reserveData?'+$('#searchForm').serialize(),
                columns:[[
                    {field:'productName',title:'商品名称',formatter:function(value,row){
                        return "<span style='width:100%' title='" + row.product.name + "'>" + row.product.name + "</span>";
                    }},
                    {field:'startTime',title:'预购开始时间'},
                    {field:'endTime',title:'预购结束时间'},
                    {field:'oper',title:'操作'
                        ,formatter: function(value,row){
                            return '<a href="' + ctx + '/ec/product/reserveForm?id=' + row.id + '">修改</a>';
                        }
                    }
                ]]
            }));
        });
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="reserve" action="${ctx}/ec/product/reserveData" method="post" class="breadcrumb form-search">
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                查询条件
            </div>
        </div>
        <div class="panel-body">
            <label>商品名称：</label>
            <form:input path="product.name" htmlEscape="false" maxlength="50" placeholder="商品名称"/>&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid(ctx + '/ec/product/reserveData')"/>
        </div>
    </div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable"></table>
</body>
</html>