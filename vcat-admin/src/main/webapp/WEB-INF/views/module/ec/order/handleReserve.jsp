<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单清理库存</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            closeTip();
            $("#contentTable").datagrid(getDatagridOption({
                url : ctx + '/ec/order/clearInventoryLogData?id=${orderReserve.orderId}',
                frozenColumns:[[
                    {field:'operDate',title:'操作时间',width:140},
                    {field:'operBy',title:'操作人',width:100,formatter: function(value,row){return row.operBy.name;}}
                ]],
                columns:[[
                    {field:'clearInventory',title:'清理库存',width:100},
                    {field:'express',title:'快递公司',width:100,formatter: function(value,row){return row.express.name;}},
                    {field:'shippingDate',title:'发货日期',width:140},
                    {field:'shippingNumber',title:'运单号',width:100},
                    {field:'deliveryName',title:'收货人',width:100},
                    {field:'deliveryPhone',title:'收货电话',width:100},
                    {field:'detailAddress',title:'收货地址',width:200},
                    {field:'note',title:'备注',width:300}
                ]]
            }));
            $("#clearForm").validate({
                submitHandler: function(form){
                    loading('正在提交，请稍等...');
                    form.submit();
                },
            });
        });
	</script>
</head>
<body>
    <table class="table table-bordered">
        <tr>
            <th width="200px"><span class="pull-right">订单号：</span></th>
            <td><a href="javascript:void(0)" onclick="showViewPage(ctx+'/ec/order/form?id=${orderReserve.orderId}&isView=true&banBack=true','${orderReserve.orderNumber} 订单详情')">${orderReserve.orderNumber}</a></td>
        </tr>
        <tr>
            <th><span class="pull-right">订单商品：</span></th>
            <td><a href="javascript:void(0)" onclick="showViewPage(ctx+'/ec/product/form?id=${orderReserve.productId}&isView=true','${orderReserve.productName} 商品详情')">${orderReserve.productName}</a></td>
        </tr>
        <tr>
            <th><span class="pull-right">订单总库存：</span></th>
            <td>${orderReserve.orderInventory}</td>
        </tr>
        <tr>
            <th><span class="pull-right">已售出库存：</span></th>
            <td>${orderReserve.salesInventory}</td>
        </tr>
        <tr>
            <th><span class="pull-right">已清理库存：</span></th>
            <td>${orderReserve.clearInventory}</td>
        </tr>
        <tr>
            <th><span class="pull-right">剩余库存：</span></th>
            <td>${orderReserve.lastInventory}</td>
        </tr>
        <tr>
            <th><span class="pull-right">小店名称：</span></th>
            <td>${orderReserve.shopName}</td>
        </tr>
        <tr>
            <th><span class="pull-right">小店手机号：</span></th>
            <td>${orderReserve.shopPhone}</td>
        </tr>
    </table>
    <hr>
    <label>库存清理记录：</label>
	<table id="contentTable" width="100%"></table>
    <div class="form-actions">
        <input class="btn btn-primary" type="button" value="清理库存" onclick="showDialog('clearDialog')"/>&nbsp;
        <input id="btnCancel" class="btn" type="button" value="关 闭" onclick="window.parent.window.jBox.close()"/>
    </div>
    <div id="clearDialog" class="vcat-dialog" style="width: 80%;">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    库存清理详情
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('clearDialog')"></a>
                </div>
            </div>
            <div class="panel-body">
                <form id="clearForm" action="${ctx}/ec/order/saveClearInventory">
                    <table class="table table-bordered table-hover">
                        <tr>
                            <th width="15%"><span class="pull-right">收货人：</span></th>
                            <td width="35%"><input type="text" id="deliveryName" name="deliveryName" value="${orderReserve.deliveryName}" class="required"/></td>
                            <th width="15%"><span class="pull-right">收货电话：</span></th>
                            <td width="35%"><input type="text" id="deliveryPhone" name="deliveryPhone" value="${orderReserve.deliveryPhone}" class="required"/></td>
                        </tr>
                        <tr>
                            <th><span class="pull-right">收货地址：</span></th>
                            <td colspan="3"><input type="text" id="detailAddress" name="detailAddress" value="${orderReserve.detailAddress}" class="input-xxlarge required"/></td>
                        </tr>
                        <tr>
                            <th><span class="pull-right">清理库存：</span></th>
                            <td colspan="3">
                                <input type="text" id="clearInventory" name="clearInventory" value="${orderReserve.lastInventory}" min="0" max="${orderReserve.lastInventory}" class="required digits"/>
                                <span class="help-inline">该数字不能大于剩余库存【${orderReserve.lastInventory}】</span>
                            </td>
                        </tr>
                        <tr>
                            <th><span class="pull-right">物流公司：</span></th>
                            <td>
                                <select id="express" name="express.id" class="input-large">
                                    <c:forEach items="${expressList}" var="express">
                                        <option value="${express.id}">${express.name}</option>
                                    </c:forEach>
                                </select>
                            </td>
                            <th><span class="pull-right">发货日期：</span></th>
                            <td><input type="text" id="shippingDate" name="shippingDate" onclick="new WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate required"/></td>
                        </tr>
                        <tr>
                            <th><span class="pull-right">物流单号：</span></th>
                            <td colspan="3"><input type="text" id="shippingNumber" name="shippingNumber" class="required"/></td>
                        </tr>
                        <tr>
                            <th><span class="pull-right">备注：</span></th>
                            <td colspan="3"><textarea name="note" cols="10" rows="3" style="resize:none;width:90%;" class="required"></textarea></td>
                        </tr>
                    </table>
                    <div class="form-actions">
                        <input type="hidden" name="orderId" value="${orderReserve.orderId}"/>
                        <input id="btnSave" class="btn btn-primary" type="submit" value="保存"/>
                        <input id="btnRe" class="btn" type="button" value="返回" onclick="showDialog('clearDialog')"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>