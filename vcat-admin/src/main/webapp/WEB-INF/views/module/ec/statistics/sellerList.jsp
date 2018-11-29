<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>商品卖家列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function (){
            $("#contentTable").datagrid(getDatagridOption({
                columns:[[
                    {field:'productName',title:'商品名称'},
                    {field:'sellerName',title:'用户名',width:200},
                    {field:'phone',title:'手机号',width:200},
                    {field:'isProxy',title:'是否代理',width:100,
                        styler : function (value,row){
                            return 'color:' + row.isProxyColor;
                        }
                    },
                    {field:'isSale',title:'是否销售',width:100,
                        styler : function (value,row){
                            return 'color:' + row.isSaleColor;
                        }
                    },
                    {field:'sales',title:'销量',width:100},
                    {field:'oper',title:'操作',width:60
                        ,formatter: function(value,row){
                            return '<a href="javascript:void(0)" onclick="showProductSellerOrderList(\'' + row.id + '\',\'' + row.productName + '\',\'' + row.sellerId + '\',\'' + row.sellerName + '\')">查看</a>';
                        }
                    }
                ]]
            }));
            reload();
        });
        function showProductSellerOrderList(productId,productName,sellerId,sellerName){
            stopBubble();
            showViewPage(ctx + "/ec/statistics/productOrder?"
                    + "sqlMap['supplierId']=" + $('#supplierId').val()
                    + "&sqlMap['brandId']=" + $('#brandId').val()
                    + "&sqlMap['productId']=" + $('#id').val()
                    + "&sqlMap['productName']=" + productName
                    + "&sqlMap['shopId']=" + sellerId
                    + "&sqlMap['shopName']=" + sellerName
                    + "&sqlMap['keyWord']=" + $('#keyWord').val()
                    + "&sqlMap['ost']=" + $('#ost').val()
                    + "&sqlMap['oet']=" + $('#oet').val()
                    ,"查看 " + productName + " 商品 " + sellerName + " 卖家订单列表");
        }
        function reload(){
            reloadGrid('/ec/statistics/sellerListData');
        }
        function exportTable() {
            top.$.jBox.confirm("确认导出？", "系统提示", function (v) {
                if (v == "ok") {
                    $("#searchForm").attr("action", ctx + "/ec/statistics/exportSellerList");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action", ctx + "/ec/statistics/sellerListData");
                }
            });
        }
        function clearForm(){
            $('#keyWord').val("");
            $('#ost').val("");
            $('#oet').val("");
            $('#isProxy').val("");
            $('#isProxy').select2();
            $('#isSale').val("");
            $('#isSale').select2();
        }
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="productSeller" action="${ctx}/ec/statistics/sellerListData" method="get" class="breadcrumb form-search">
    <form:hidden path="id" />
    <form:hidden path="supplierId" />
    <form:hidden path="brandId" />
    <label>关键字：</label>
    <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="用户名称、客户手机号"/>&nbsp;
    <label>下单时间：</label>
    <input id="ost" name="sqlMap['ost']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
           value="${productSeller.sqlMap['ost']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
    <label>至&nbsp;&nbsp;</label>
    <input id="oet" name="sqlMap['oet']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
           value="${productSeller.sqlMap['oet']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
    <label>是否代理：</label>
    <form:select id="isProxy" path="sqlMap['isProxy']" cssClass="input-small">
        <form:option value="">全部</form:option>
        <form:option value="1">已代理</form:option>
        <form:option value="0">未代理</form:option>
    </form:select>
    <label>是否销售：</label>
    <form:select id="isSale" path="sqlMap['isSale']" cssClass="input-small">
        <form:option value="">全部</form:option>
        <form:option value="1">已销售</form:option>
        <form:option value="0">未销售</form:option>
    </form:select>
    <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reload()"/>
    <input id="btnExport" class="btn btn-primary" type="button" value="导出" onclick="exportTable()"/>
    <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" style="width: 99%;"></table>
</body>
</html>