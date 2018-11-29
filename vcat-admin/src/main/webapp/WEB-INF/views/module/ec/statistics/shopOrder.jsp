<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>店铺订单列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/order/exportOrderList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/statistics/shopOrder");
                    }
                });
            });
            $("#contentTable").datagrid(getDatagridOption({
                frozenColumns:[[
                    {field:'orderType',title:'订单类型',width:100},
                    {field:'orderNumber',title:'订单号',width:160
                        ,formatter: function(value,row){
                        return '<a href="' + ctx + '/ec/order/form?id=' + row.id + '&isView=true" onclick="stopBubble()">' + value + '</a>';
                    }
                    }
                ]],
                columns:[[
                    {field:'addDate',title:'下单日期',width:140},
                    {field:'supplierName',title:'供应商',width:200},
                    {field:'shopName',title:'小店名称',width:60},
                    {field:'buyerUserName',title:'买家名称',width:100,formatter: function(value,row){return row.buyer.userName;}},
                    {field:'productName',title:'商品名称',width:400},
                    {field:'paymentGatewayName',title:'支付方式',width:60,formatter: function(value,row){return row.payment && row.payment.gateway ? row.payment.gateway.name : "无";}},
                    {field:'totalPrice',title:'订单金额',width:60},
                    {field:'paymentPrice',title:'支付金额',width:60,formatter: function(value,row){return row.payment ? row.payment.amount : "无";}},
                    {field:'paymentDate',title:'支付日期',width:140,formatter: function(value,row){return row.payment ? row.payment.paymentDate : "无";}},
                    {field:'hasReturn',title:'包含退货',width:60
                        ,styler: function(value,row){
                        return 'color:' + (value ? 'red' : 'green') + ';';
                    }
                        ,formatter: function(value,row){
                        return value ? '包含' : '不包含';
                    }
                    },
                    {field:'paymentStatusLabel',title:'支付状态',width:80,styler: function(value,row){return 'color:' + row.paymentStatusColor;}},
                    {field:'orderStatusLabel',title:'订单状态',width:100,styler: function(value,row){return 'color:' + row.orderStatusColor;}},
                    {field:'shippingStatusLabel',title:'发货状态',width:60,styler: function(value,row){return 'color:' + row.shippingStatusColor;}}
                    ,{field:'oper',title:'操作',width:60
                        ,formatter: function(value,row){
                            var html = '<a href="' + ctx + '/ec/order/form?isView=true&id=' + row.id + '">查看</a>';
                            if(row.shippingStatus == '1'){
                                html += ' <a href="javascript:void(0)" onclick="stopBubble();showExpressInfo(\'' + row.shipping.express.code + '\',\'' + row.shipping.number + '\',false)">物流</a>';
                            }
                            return html;
                        }
                    }
                ]]
            }));
            $('.${order.sqlMap.type}').addClass("active");
            reload();
        });
        function sub(paymentStatus,orderStatus,shippingStatus,type,tab){
            $("#paymentStatus").val(paymentStatus);
            $("#orderStatus").val(orderStatus);
            $("#shippingStatus").val(shippingStatus);
            $("#type").val(type);

            $('.order-tab').removeClass("active");
            $(tab).parent().addClass("active");

            reload();
            return false;
        }
        function reload(){
            reloadGrid('/ec/statistics/productOrderData');
        }
        function clearForm(){
            $('#orderType').val("");
            $('#orderType').select2();
            $('#distributionId').val("");
            $('#distributionId').select2();
            $('#orderBy').val("");
            $('#orderBy').select2();
            $('#ost').val("");
            $('#oet').val("");
            $('#sst').val("");
            $('#set').val("");
            $('#keyWord').val("");
            $(':checkbox:checked').attr("checked",false);
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="order-tab all"><a href="javascript:void(0)" onclick="sub('','','','all',this)">全部订单</a></li>
    <li class="order-tab toPay"><a href="javascript:void(0)" onclick="sub(0,0,0,'toPay',this)">未支付</a></li>
    <li class="order-tab paid"><a href="javascript:void(0)" onclick="sub(1,'','','paid',this)">已支付</a></li>
    <li class="order-tab toHandler"><a href="javascript:void(0)" onclick="sub(1,0,0,'toHandler',this)">待确认</a></li>
    <li class="order-tab toBeShipped"><a href="javascript:void(0)" onclick="sub(1,1,0,'toBeShipped',this)">待发货</a></li>
    <li class="order-tab shipped"><a href="javascript:void(0)" onclick="sub(1,1,1,'shipped',this)">已发货</a></li>
    <li class="order-tab completed"><a href="javascript:void(0)" onclick="sub(1,2,2,'completed',this)">已完成</a></li>
    <li class="order-tab toConfirmReturn"><a href="javascript:void(0)" onclick="sub(1,'','','toConfirmReturn',this)">退货(待确认)</a></li>
    <li class="order-tab toConfirmReceivingReturn"><a href="javascript:void(0)" onclick="sub(1,'','','toConfirmReceivingReturn',this)">退货(待收货)</a></li>
    <li class="order-tab onRefund"><a href="javascript:void(0)" onclick="sub(1,'','','onRefund',this)">退款中</a></li>
    <li class="order-tab refundCompleted"><a href="javascript:void(0)" onclick="sub(1,2,'','refundCompleted',this)">退货(退款)完成</a></li>
    <li class="order-tab cancelled"><a href="javascript:void(0)" onclick="sub('',3,'','cancelled',this)">已取消</a></li>
</ul>
<form:form id="searchForm" modelAttribute="order" action="${ctx}/ec/statistics/shopOrder" method="get" class="breadcrumb form-search">
    <input id="paymentStatus" name="paymentStatus" type="hidden" value="${order.paymentStatus}"/>
    <input id="orderStatus" name="orderStatus" type="hidden" value="${order.orderStatus}"/>
    <input id="shippingStatus" name="shippingStatus" type="hidden" value="${order.shippingStatus}"/>
    <input id="view" name="sqlMap['view']" type="hidden" value="${order.sqlMap.view}"/>
    <input id="type" name="sqlMap['type']" type="hidden" value="${order.sqlMap.type}"/>
    <input id="shopId" name="sqlMap['shopId']" type="hidden" value="${order.sqlMap.shopId}">
    <input id="shopName" name="sqlMap['shopName']" type="hidden" value="${order.sqlMap.shopName}">
    <label>店铺：${order.sqlMap.shopName}</label>
    <label>订单类型：</label>
    <form:select path="orderType" class="input-small">
        <form:option value="" label="全部"/>
        <form:options items="${fns:getDictList('ec_order_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
    </form:select>
    <label>包含退货：</label>
    <form:select path="sqlMap['hasReturn']" class="input-small">
        <form:option value="" label="全部"/>
        <form:option value="1" label="包含"/>
        <form:option value="2" label="不包含"/>
    </form:select>
    <label>关键字：</label>
    <form:input id="keyWord" path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-xlarge" placeholder="订单号、小店(买家)名称、客户手机号"/>&nbsp;
    <label>排序规则：</label>
    <form:select id="orderBy" path="sqlMap['orderBy']" cssClass="input-small">
        <form:option value="default">默认排序</form:option>
        <form:option value="g1u">下单时间升序</form:option>
        <form:option value="g1d">下单时间降序</form:option>
        <form:option value="g2u">供货商升序</form:option>
        <form:option value="g3u">小店名称升序</form:option>
        <form:option value="g4u">买家名称升序</form:option>
        <form:option value="g5u">订单金额升序</form:option>
        <form:option value="g5d">订单金额降序</form:option>
        <form:option value="g7u">支付状态升序</form:option>
        <form:option value="g6u">订单状态升序</form:option>
        <form:option value="g8u">发货状态升序</form:option>
    </form:select>
    <br>
    <div style="margin-top: 10px;">
        <label>下单时间：</label>
        <input id="ost" name="sqlMap['ost']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
               value="${order.sqlMap['ost']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
        <label>至&nbsp;&nbsp;</label>
        <input id="oet" name="sqlMap['oet']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
               value="${order.sqlMap['oet']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
        <label>发货时间：</label>
        <input id="sst" name="sqlMap['sst']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
               value="${order.sqlMap['sst']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
        <label>至&nbsp;&nbsp;</label>
        <input id="set" name="sqlMap['set']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width: 130px;"
               value="${order.sqlMap['set']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
        <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
        <input id="btnClear" class="btn btn-default" type="button" value="清空" onclick="clearForm()"/>
    </div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" style="width: 99%;"></table>
<jsp:include page="../express/dialog.jsp"></jsp:include>
</body>
</html>