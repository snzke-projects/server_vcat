<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设置限购商品</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var selectedItemIds;
		$(function() {
			$("#productType").focus();
			$("#inputForm").validate({
				submitHandler: function(){
					if(validateForm()){
						loading('正在提交，请稍等...');
                        $.post(ctx+'/ec/product/saveLimit?'+$('#inputForm').serialize(),function(){
                            alertx("保存成功",function(){
                               location.href = ctx + "/ec/product/limitList";
                            });
                        });
					}
				}
			});
            $('.itemTable').datagrid(getDatagridOption({
                fitColumns:true,
                pagination:false,
                columns:[[
                    {field:'product.name',title:'商品名称',width:300,formatter: function(value,row){
                        return abbr(row.product.name,50);
                    }},
                    {field:'name',title:'规格名称',width:100},
                    {field:'retailPrice',title:'售价',width:80},
                    {field:'purchasePrice',title:'结算价',width:80},
                    {field:'saleEarning',title:'佣金',width:80},
                    {field:'bonusEarning',title:'分红',width:80},
                    {field:'firstBonusEarning',title:'一级团队分红',width:80},
                    {field:'secondBonusEarning',title:'二级团队分红',width:80},
                    {field:'point',title:'扣点',width:80},
                    {field:'couponValue',title:'可使用V猫币',width:80},
                    {field:'inventory',title:'库存',width:80}
                ]]
            }));
            updateTable('${productLimit.productItem.id}');
		});
		function validateForm(){
            if(!isInteger($('#interval').val())){
                alertx("限购天数格式不正确！",function(){
                    $("#interval").focus();
                });
                return false;
            }
            if(!isNumber($('#times').val())){
                alertx("限购次数格式不正确！",function(){
                    $("#times").focus();
                });
                return false;
            }

			if(new Date($('#startTime').val().replace(/-/g,"/")).getTime() >= new Date($('#endTime').val().replace(/-/g,"/")).getTime()){
				alertx("限购结束时间必须大于开始时间！",function(){
					$("#startTime").trigger("click");
				});
				return false;
			}

			return true;
		}
        function selectItem(){
            var url = ctx + "/ec/product/selectItem?";
            url += "radio=" + ('${productLimit.id}' != '');
            url += "&selectedIds=" + "${productLimit.productItem.id}";
            url += "&sqlMap[\'onlyTopic\']=" + ($('#productType').val() == '6');
            top.$.jBox.open("iframe:" + url, "选择限购商品规格",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": 'ok',"关闭": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                },submit : function(buttonId){
                    if(buttonId == "ok"){
                        updateTable(selectedItemIds);
                    }
                }
            });
        }
        function updateTable(ids){
            $.get(ctx + "/ec/product/getProductItemListByIds?ids="+ids,function(selectedProductItemArray){
                $('#productItemIds').val(ids);
                if(selectedProductItemArray && selectedProductItemArray.length > 0){
                    $('.itemTable').datagrid('loadData', { total: selectedProductItemArray.length, rows: selectedProductItemArray});
                }
            });
        }
	</script>
</head>
<body>
	<form:form id="inputForm" modelAttribute="productLimit" action="${ctx}/ec/product/saveForecast" method="post" class="form-horizontal">
		<form:hidden path="id"/>
        <input type="hidden" id="productItemIds" name="productItem.id" value="${productLimit.productItem.id}">
		<sys:message content="${message}"/>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					设置限购商品
				</div>
			</div>
			<div class="panel-body">
				<div class="tab-content">
                    <div class="control-group">
                        <label class="control-label">限购区域：</label>
                        <div class="controls">
                            <form:select path="productType" cssClass="input-small required">
                                <form:option value="" label="请选择"></form:option>
                                <form:options items="${fns:getDictList('ec_product_limit_type')}" itemValue="value" itemLabel="label"></form:options>
                            </form:select>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">限购目标：</label>
                        <div class="controls">
                            <form:select path="userType" cssClass="input-small required">
                                <form:option value="" label="请选择"></form:option>
                                <form:options items="${fns:getDictList('ec_product_limit_target')}" itemValue="value" itemLabel="label"></form:options>
                            </form:select>
                        </div>
                    </div>
					<div class="control-group">
						<label class="control-label">限购商品规格：</label>
						<div class="controls">
                            <input onclick="selectItem()" class="btn" type="button" value="选择规格"></input>
                            <table class="itemTable" style="width: auto;margin-top: 5px;"></table>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">限购次数：</label>
						<div class="controls">
							<form:input path="interval" cssClass="input-mini interval required"></form:input><span class="interval">&nbsp;天</span>
                            <form:input path="times" cssClass="input-mini required"></form:input>&nbsp;次
                            <span class="help-inline">（每1个用户ID规定时间内限购同一规格的该商品的次数）</span>
                            <span class="help-inline">（限购天数为0时则不限制天数）</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">限购有效时间：</label>
						<div class="controls">
                            <input type="text" id="startTime" name="startTime" value="<fmt:formatDate value="${productLimit.startTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly maxlength="16" class="input-date required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>&nbsp;至
                            <input type="text" id="endTime" name="endTime" value="<fmt:formatDate value="${productLimit.endTime}" pattern="yyyy-MM-dd HH:mm"/>" readonly maxlength="16" class="input-date required Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});"/>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="ec:product:limit:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>