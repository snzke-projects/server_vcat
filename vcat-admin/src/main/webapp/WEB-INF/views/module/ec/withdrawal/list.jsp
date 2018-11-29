<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>提现管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/withdrawal/export");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/withdrawal/list");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
        });
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
						if(0 == withdrawal.withdrawalStatus){
							$('.handler').show();
						}else{
							$('.handlerNoteText').show();
							$('#handlerNoteText').val(withdrawal.note);

						}
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
								}else{
                                    $('#bankName').html("无");
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
		function handler(id,shopId,status){
			if("" == $('#handlerNote').val()){
				alertx("请填写提现说明！",function(){
					$('#handlerNote').focus();
				});
				return;
			}
            loading('正在提交，请稍等...');
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/withdrawal/handler",
				data : {
					id : id,
					withdrawalStatus : status,
					note : $('#handlerNote').val(),
					shopId : shopId
				},
				success : function(){
					try{
						alertx("处理成功！",function(){
							$('#searchForm').submit();
						});
					}catch(e){
						console.log("处理失败："+e.message);
						return doError(e);
					}
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function handlerSucc(id,shopId,accountName){
			confirmx("确认已将提现金额转入到 "+accountName+" 账户中",function(){
				handler(id,shopId,1);
			});
		}
		function handlerFailure(id,shopId){
			confirmx("确认驳回该提现申请？",function(){
				handler(id,shopId,2);
			});
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
        function batchHandler(){
            if($('#contentTable :checked').length == 0){
                top.$.jBox.tip("请选择需要处理的提现单！");
                return false;
            }
            confirmx("确认批量处理提现单？",function (){
                loading("正在批量处理提现单，请稍候...");
                showLayer();
                $.ajax({
                    type : 'POST',
                    url : ctx + "/ec/withdrawal/batchHandler?" + $('#contentTable :checked').serialize(),
                    success : function(){
                        try{
                            top.$.jBox.tip("批量处理提现单成功！");
                            location.href = location.href;
                        }catch(e){
                            console.log("批量处理提现单失败：" + e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }
        function clearForm(){
            $('#gatewayType').val("");
            $('#s2id_gatewayType .select2-chosen').html("");
            $('#keyword').val("");
            $('#minAmount').val("");
            $('#maxAmount').val("");
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="withdrawal" action="${ctx}/ec/withdrawal/list" method="post" class="form-search" onsubmit="return checkForm()">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <input id="type" name="type" type="hidden" value="${type}"/>
            <label>账户类型：</label>
            <form:select id="gatewayType" path="sqlMap['gatewayType']" cssClass="input-small">
                <option value="">全部</option>
                <form:options items="${gatewayTypeList}" itemLabel="name" itemValue="id"></form:options>
            </form:select>
            <c:if test="${type eq 'historyWithdrawalList'}">
                <label>提现状态：</label>
                <form:select id="withdrawalStatus" path="withdrawalStatus" cssClass="input-small">
                    <option value="">全部</option>
                    <form:option value="1">提现完成</form:option>
                    <form:option value="2">提现失败</form:option>
                </form:select>
            </c:if>
			<label>关键字：</label>
			<form:input id="keyword" path="sqlMap['keyword']" htmlEscape="false" maxlength="50" class="input-large"/>
			<label>提现金额：</label>
			<input id="minAmount" name="sqlMap['minAmount']" value="${withdrawal.sqlMap['minAmount']}" maxlength="10" class="input-small" type="text" placeholder="元"/>&nbsp;至
			<input id="maxAmount" name="sqlMap['maxAmount']" value="${withdrawal.sqlMap['maxAmount']}" maxlength="10" class="input-small" type="text" placeholder="元"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
            &nbsp;<input id="btnBatchHandler" class="btn btn-primary" type="button" value="批量处理完成" onclick="batchHandler()"/>
            &nbsp;<input id="btnBatchHandler" class="btn" type="button" value="清空" onclick="clearForm()"/>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-hover">
	<thead>
	<tr>
        <th style="width: 30px;">
            <a href="javascript:void(0)" onclick="$(':checkbox').attr('checked',true)" ondblclick="$(':checkbox').attr('checked',false)" title="全选/双击清空">选择</a>
        </th>
		<th>小店昵称</th>
		<th width="80px">金额</th>
		<th width="160px">申请时间</th>
		<th width="80px">账户类型</th>
		<th width="160px">账户号码</th>
		<th width="100px">账户姓名</th>
        <th width="120px">开户银行</th>
		<%--<th width="20%">开户行地址</th>--%>
		<th width="100px">状态</th>
		<th width="100px">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="withdrawal">
		<tr>
            <td><input type="checkbox" name="withdrawalIds" value="${withdrawal.id}"></td>
			<td>${withdrawal.shop.name}</td>
			<td>${withdrawal.amount}</td>
			<td><fmt:formatDate value="${withdrawal.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>${withdrawal.account.gateWay.name}</td>
			<td>${withdrawal.account.accountNumber}</td>
			<td>${withdrawal.account.accountName}</td>
            <td>
                <c:if test="${withdrawal.account.bank.name ne ''}">
                    ${withdrawal.account.bank.name}
                </c:if>
                <c:if test="${fn:trim(withdrawal.account.bank) eq ''}">
                    无
                </c:if>
            </td>
			<%--<td>${fns:abbr(withdrawal.account.bankAddress,50)}</td>--%>
			<td style="color: ${withdrawal.withdrawalStatusColor}">${withdrawal.withdrawalStatusLabel}</td>
			<td>
				<a href="javascript:void(0);" onclick="showWithdrawal('${withdrawal.id}')">查看</a>
				<c:if test="${withdrawal.WITHDRAWAL_STATUS_UNTREATED eq withdrawal.withdrawalStatus}">
					<shiro:hasPermission name="ec:finance:withdrawal:handler">
						<a href="javascript:void(0)" onclick="showWithdrawal('${withdrawal.id}')">处理</a>
					</shiro:hasPermission>
				</c:if>
			</td>
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
                    <td>开户行地址：</td>
                    <td colspan="3"><span id="bankAddress"></span></td>
				</tr>
				<tr class="handlerNoteText hide">
					<td colspan="4">
						<textarea id="handlerNoteText" cols="10" rows="3" style="resize:none;width:95%;" readonly></textarea>
					</td>
				</tr>
				<shiro:hasPermission name="ec:finance:withdrawal:handler">
				<tr class="handler hide">
					<td>提现说明：</td>
                    <td colspan="3"><textarea id="handlerNote" cols="10" rows="3" style="resize:none;width:95%;"  placeholder="如驳回提现申请，系统将发送该说明给提现店铺，请勿随意填写。"></textarea></td>
				</tr>
				</shiro:hasPermission>
				</tbody>
			</table>
			<shiro:hasPermission name="ec:finance:withdrawal:handler">
				<div class="form-actions handler hide" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="withdrawalId"/>
					<input type="hidden" id="withdrawalShopId"/>
					<input onclick="handlerSucc($('#withdrawalId').val(),$('#withdrawalShopId').val(),$('#accountName').html())" class="btn btn-success" type="button" value="转账完成"/>
					<input onclick="handlerFailure($('#withdrawalId').val(),$('#withdrawalShopId').val())" class="btn btn-danger" type="button" value="驳回"/>
				</div>
			</shiro:hasPermission>
		</div>
	</div>
</div>
</body>
</html>