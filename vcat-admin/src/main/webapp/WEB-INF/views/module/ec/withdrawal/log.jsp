<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>提现管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function showWithdrawal(id){
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/withdrawal/find",
				data : {
					id : id
				},
				success : function(withdrawal){
					try{
						$('#withdrawalDialog .panel-body span').html("");
						$('#handlerNoteText').html(withdrawal.note);
						$('#withdrawalId').val(withdrawal.id);
						$('#withdrawalShopId').val(withdrawal.shopId);
						$('#withdrawalDate').html(withdrawal.createDate);
						$('#withdrawalAmount').html(withdrawal.amount);
						$('#withdrawalStatusSpan').html(withdrawal.withdrawalStatusLabel);
						$('#withdrawalStatusSpan').css("color",withdrawal.withdrawalStatusColor);

						if(!isNull(withdrawal.shop)){
							$('#withdrawalShopName').html(withdrawal.shop.name);
							if(!isNull(withdrawal.account)){
								$('#accountName').html(withdrawal.account.accountName);
								$('#accountNumber').html(withdrawal.account.accountNumber);
								$('#bankAddress').html(withdrawal.account.bankAddress);
								if(!isNull(withdrawal.account.gateWay)){
									$('#gatewayName').html(withdrawal.account.gateWay.name);
								}
								if(!isNull(withdrawal.account.bank)){
									$('#bankName').html(withdrawal.account.bank.name);
								}
							}
						}
					}catch(e){
						console.log("获取提现申请单失败："+e.message);
						return doError(e);
					}
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
			showDialog('withdrawalDialog');
		}
		function checkForm(){
			var minAmount = $('#minAmount').val();
			if("" != minAmount && !isDecimal(minAmount)){
				alertx("提现金额输入不正确！",function(){
					$('#minAmount').focus();
				});
				return false;
			}
			var maxAmount = $('#maxAmount').val();
			if("" != maxAmount && !isDecimal(maxAmount)){
				alertx("提现金额输入不正确！",function(){
					$('#maxAmount').focus();
				});
				return false;
			}
			return true;
		}
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="withdrawal" action="${ctx}/ec/customer/withdrawalLog" method="post" class="form-search" onsubmit="return checkForm()">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<form:hidden path="shop.id"></form:hidden>
			<label>关键字：</label>
			<form:input id="keyword" path="sqlMap['keyword']" htmlEscape="false" maxlength="50" class="input-large"/>
			<label>提现金额：</label>
			<input id="minAmount" name="sqlMap['minAmount']" value="${withdrawal.sqlMap['minAmount']}" maxlength="10" class="input-small" type="text" placeholder="元"/>&nbsp;至
			<input id="maxAmount" name="sqlMap['maxAmount']" value="${withdrawal.sqlMap['maxAmount']}" maxlength="10" class="input-small" type="text" placeholder="元"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
        <th width="8%">提现店铺名称</th>
        <th width="8%">提现金额</th>
        <th width="8%">申请时间</th>
        <th width="8%">提现账户类型</th>
        <th width="8%">提现开户银行</th>
        <th width="14%">提现账户号码</th>
        <th width="8%">提现账户姓名</th>
        <th width="20%">开户行地址</th>
        <th width="8%">提现状态</th>
        <th width="10%">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="withdrawal">
		<tr>
			<td>${withdrawal.shop.name}</td>
			<td>${withdrawal.amount}</td>
			<td><fmt:formatDate value="${withdrawal.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>${withdrawal.account.gateWay.name}</td>
			<td>${withdrawal.account.bank.name}</td>
			<td>${withdrawal.account.accountNumber}</td>
			<td>${withdrawal.account.accountName}</td>
			<td>${fns:abbr(withdrawal.account.bankAddress,50)}</td>
			<td style="color: ${withdrawal.withdrawalStatusColor}">${withdrawal.withdrawalStatusLabel}</td>
			<td><a href="javascript:void(0);" onclick="showWithdrawal('${withdrawal.id}')">查看</a></td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>

<div id="withdrawalDialog" class="vcat-dialog" style="width: 50%;">
	<div class="panel panel-info"><!-- 退货单 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				提现申请单
				<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('withdrawalDialog')"></a>
			</div>
		</div>
		<div class="panel-body">
			<table class="table table-responsive table-hover">
				<tbody>
				<tr>
					<td width="20%">提现店铺名称：</td>
					<td width="30%"><span id="withdrawalShopName"></span></td>
					<td width="20%">申请时间：</td>
					<td width="30%"><span id="withdrawalDate"></span></td>
				</tr>
				<tr>
					<td>提现金额：</td>
					<td><span id="withdrawalAmount"></span></td>
					<td>提现状态：</td>
					<td><span id="withdrawalStatusSpan"></span></td>
				</tr>
				<tr>
					<td>账户类型：</td>
					<td><span id="gatewayName"></span></td>
					<td>账户姓名：</td>
					<td><span id="accountName"></span></td>
				</tr>
				<tr>
					<td>银行名称：</td>
					<td><span id="bankName"></span></td>
					<td>账户号码：</td>
					<td><span id="accountNumber"></span></td>
				</tr>
				<tr>
					<td colspan="4">开户行地址：<span id="bankAddress"></span></td>
				</tr>
				<tr>
					<td colspan="4">
						<pre style="font-size: 13px;" id="handlerNoteText"></pre>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>