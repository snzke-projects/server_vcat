<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>商品买家列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/statistics/exportBuyerList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/statistics/buyerListData");
                    }
                });
            });
            $("#contentTable").datagrid(getDatagridOption({
                columns:[[
                    {field:'productName',title:'商品名称'},
                    {field:'buyerName',title:'购买用户',width:200},
                    {field:'buyerPhone',title:'用户手机',width:200},
                    {field:'buyerRole',title:'用户身份',width:200},
                    {field:'repeatCount',title:'复购次数',width:100},
                    {field:'oper',title:'操作',width:60
                        ,formatter: function(value,row){
                            if(row.repeatCount === 0){
                                return '<a href="javascript:void(0)" onclick="showOrder(\'' + row.orderId + '\')">查看</a>';
                            }else if(row.repeatCount > 0){
                                return '<a href="javascript:void(0)" onclick="showProductBuyerOrderList(\'' + row.productName + '\',\'' + row.buyerId + '\',\'' + row.buyerName + '\')">查看</a>';
                            }else{
                                return '';
                            }
                        }
                    }
                ]]
            }));
            reloadGrid('/ec/statistics/buyerListData');
        });
        function showOrder(orderId){
            stopBubble();
            showViewPage(ctx + "/ec/order/form?isView=true&banBack=true&id=" + orderId,"查看订单详情");
        }
        function showProductBuyerOrderList(productName,buyerId,buyerName){
            stopBubble();
            showViewPage(ctx + "/ec/statistics/productOrder?"
                    + "sqlMap['supplierId']=" + $('#supplierId').val()
                    + "&sqlMap['brandId']=" + $('#brandId').val()
                    + "&sqlMap['productId']=" + $('#id').val()
                    + "&sqlMap['productName']=" + productName
                    + "&sqlMap['buyerId']=" + buyerId
                    + "&sqlMap['buyerName']=" + buyerName
                    + "&sqlMap['keyWord']=" + $('#keyWord').val()
                    + "&sqlMap['ost']=" + $('#ost').val()
                    + "&sqlMap['oet']=" + $('#oet').val()
                    ,"查看 " + productName + " 商品 " + buyerName + " 买家订单列表");
        }
        function clearForm(){
            $('#keyWord').val("");
            $('#ost').val("");
            $('#oet').val("");
            $('#role').val("");
            $('#role').select2();
        }
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="productBuyer" action="${ctx}/ec/statistics/buyerListData" method="get" class="breadcrumb form-search">
    <form:hidden path="id" />
    <form:hidden path="supplierId" />
    <form:hidden path="brandId" />
    <label>关键字：</label>
    <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="用户名称、客户手机号"/>&nbsp;
    <label>下单时间：</label>
    <input id="ost" name="sqlMap['ost']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
           value="${productBuyer.sqlMap['ost']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
    <label>至&nbsp;&nbsp;</label>
    <input id="oet" name="sqlMap['oet']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
           value="${productBuyer.sqlMap['oet']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
    <label>用户身份：</label>
    <form:select id="role" path="sqlMap['role']" cssClass="input-small">
        <form:option value="">全部</form:option>
        <form:option value="seller">店主</form:option>
        <form:option value="buyer">买家</form:option>
    </form:select>
    <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reloadGrid('/ec/statistics/buyerListData')"/>
    <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
    <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" style="width: 99%;"></table>
</body>
</html>