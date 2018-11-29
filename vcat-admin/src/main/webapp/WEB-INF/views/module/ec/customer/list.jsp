<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>小店列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function(){
            $("#btnExport").click(function(){
                top.$.jBox.confirm("确认导出？","系统提示",function(v){
                    if(v=="ok"){
                        $("#searchForm").attr("action",ctx + "/ec/customer/exportCustomerList");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action",ctx + "/ec/customer/sellerList");
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
		function showDetail(id){
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/get",
				data : {
					id : id
				},
				success : function(customer){
					try{
						$('#customerDialog .panel-body span').html("未填写");
						$('#avatarUrl').attr("src","");
						$('#userName').html(customer.userName);
						$('#phoneNumber').html(customer.phoneNumber);
						$('#avatarUrl').attr("src",customer.avatarUrl);
						$('#idCard').html(customer.idCard);
                        showDialog('customerDialog');
					}catch(e){
						console.log("获取退款信息失败："+e.message);
						return doError(e);
					}
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function showWithdrawalLog(id){
			top.$.jBox.open("iframe:${ctx}/ec/customer/withdrawalLog?shop.id="+id, "提现历史",$(top.document).width()-220,$(top.document).height()-180,{
				buttons: {"确定": true},
				loaded: function () {
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		}
        function showTeam(id,name){
            top.$.jBox.open("iframe:${ctx}/ec/customer/team?id="+id, name+"的战队成员",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
		function showRefundLog(id){
			top.$.jBox.open("iframe:${ctx}/ec/customer/refundLog?customer.id="+id, "退款历史",$(top.document).width()-220,$(top.document).height()-180,{
				buttons: {"确定": true},
				loaded: function () {
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		}
		function createRefundLogTRHTML(refund){
			var trHTML = "<tr>";
			trHTML += "<td>"+refund.applyTime+"</td>";
			trHTML += "<td>"+refund.product.name+"</td>";
			trHTML += "<td>"+refund.quantity+"</td>";
			trHTML += "<td>"+refund.amount+"</td>";
			trHTML += "<td><pre style='font-size: 13px;'>"+refund.returnReason+"</pre></td>";
			trHTML += "<td style='color: "+refund.refundStatusColor+"'>"+refund.refundStatusLabel+"</td>";
			return trHTML + "</tr>";
		}
        function showUpgradeRequestDialog(shopId){
            $.get(ctx + "/ec/customer/getUpgradeRequest?shop.id=" + shopId,function (upgradeRequest){
                if(upgradeRequest){
                    $('#upgradeRequestId').val(upgradeRequest.id);
                    $('#shopName').html(upgradeRequest.shop.customer.userName);
                    $('#shopPhone').html(upgradeRequest.shop.customer.phoneNumber);
                    if(upgradeRequest.originalParent){
                        $('#originalParentName').html(upgradeRequest.originalParent.customer.userName);
                        $('#originalParentPhone').html(upgradeRequest.originalParent.customer.phoneNumber);
                        $('#originalParentTR').show();
                    }else{
                        $('#originalParentTR').hide();
                    }
                    if(upgradeRequest.status === 0){
                        $('.approval').show();
                    }else{
                        $('.approval').hide();
                    }
                    if(upgradeRequest.logList && upgradeRequest.logList.length && upgradeRequest.logList.length > 0){
                        $("#upgradeRequestLogTable tr:not(:first)").remove();
                        for(var i = 0; i < upgradeRequest.logList.length; i++){
                            var log = upgradeRequest.logList[i];
                            var html = "<tr>";
                            html += '<td>' + log.note + '</td>';
                            html += '<td style="color: ' + log.statusLabelColor + '">' + log.statusLabel + '</td>';
                            html += '<td>' + (log.operBy ? log.operBy.name : upgradeRequest.shop.customer.userName) + '</td>';
                            html += '<td>' + log.operDate + '</td>';
                            html += "</tr>";
                            $("#upgradeRequestLogTable tr:last").after(html);
                        }
                    }
                    showDialog("upgradeRequestDialog");
                }
            });
        }
        function approvalUpgradeRequest(id, status, msg){
            var approvalNote = $('#approvalNote').val();
            if(status === 2 && isNull(approvalNote)){
                alertx("请输入拒绝原因！",function (){
                    $('#approvalNote').focus();
                });
                return false;
            }
            confirmx("确认"+msg+"该升级申请！？",function(){
                $.get(ctx + "/ec/customer/approvalUpgradeRequest",{
                    id:id,
                    status:status,
                    "sqlMap['note']":$('#approvalNote').val()
                },function(){
                    alertx(msg+"该升级申请成功！",function(){
                        $("#searchForm").submit();
                    });
                });
            });
        }
        function addUpgradeRequest(id, name){
            confirmx("确认升级["+name+"]店铺为钻石小店！？",function(){
                $.get(ctx + "/ec/customer/addUpgradeRequest",{
                    shopId: id,
                    shopName: name
                },function(){
                    showTip("["+name+"]店铺升级成功！");
                    $("#searchForm").submit();
                });
            });
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="customer" action="${ctx}/ec/customer/sellerList" method="post" class="form-search" onsubmit="loading()">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <input name="type" type="hidden" value="${type}"/>
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <label>注册来源：</label>
            <form:select path="registered" cssStyle="width: 110px;">
                <form:option value="">全部</form:option>
                <form:option value="0">微信注册</form:option>
                <form:option value="1">客户端注册</form:option>
                <form:option value="2">网页接收邀请</form:option>
            </form:select>
            <label>用户等级：</label>
            <form:select path="shop.advancedShop" cssStyle="width: 80px;">
                <form:option value="">全部</form:option>
                <form:option value="0">特约小店</form:option>
                <form:option value="1">钻石小店</form:option>
            </form:select>
            <label>用户升级审核状态：</label>
            <form:select path="sqlMap['hasUpgrade']" cssStyle="width: 110px;">
                <form:option value="">全部</form:option>
                <form:option value="no">未申请</form:option>
                <form:option value="yes">申请待审核</form:option>
                <form:option value="success">审核通过</form:option>
                <form:option value="fail">审核拒绝</form:option>
            </form:select>
            <label>排序：</label>
            <form:select path="sqlMap['orderBy']" class="input-medium">
                <form:option value="">默认排序</form:option>
                <form:option value="1">有效订单数量降序</form:option>
                <c:if test="${'sellerList' eq customer.sqlMap['type']}">
                    <form:option value="2">总计销售额降序</form:option>
                    <form:option value="3">可提现金额降序</form:option>
                    <form:option value="4">提现中金额降序</form:option>
                    <form:option value="5">已提现金额降序</form:option>
                    <form:option value="8">推荐店铺降序</form:option>
                    <form:option value="9">注册时间降序</form:option>
                </c:if>
                <c:if test="${'buyerList' eq customer.sqlMap['type']}">
                    <form:option value="6">退款次数降序</form:option>
                    <form:option value="7">购买总额降序</form:option>
                </c:if>
            </form:select>
            <div style="margin-top: 5px;">
                <label>关键字：</label>
                <form:input path="sqlMap['keyWord']" htmlEscape="false" maxlength="50" class="input-medium" placeholder="用户名、手机号"/>
                <c:if test="${'sellerList' eq customer.sqlMap['type']}">
                    <label>店铺号：</label>
                    <form:input path="shop.shopNum" htmlEscape="false" maxlength="50" class="input-small" placeholder="店铺号"/>
                </c:if>
                <label>注册时间：</label>
                <input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                       value="${customer.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="注册时间大于该时间"/>
                <label style="margin-left: 0px;">至</label>
                <input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="Wdate" style="width:135px;"
                       value="${customer.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="注册时间小于该时间"/>
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
                <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
                <input id="btnSaveOrder" class="btn btn-primary" type="button" value="保存排序"/>
            </div>
		</div>
	</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
                <c:if test="${'sellerList' eq customer.sqlMap['type']}">
                    <th>店铺号</th>
                </c:if>
				<th>用户名</th>
				<th width="100px">手机号</th>
                <th width="100px">注册来源</th>
				<th width="100px">有效订单数量</th>
				<c:if test="${'sellerList' eq customer.sqlMap['type']}">
					<th width="80px">总计销售额</th>
					<th width="80px">可提现金额</th>
					<th width="80px">提现中金额</th>
					<th width="80px">已提现金额</th>
                    <th width="140px">注册时间</th>
                    <th width="80px">用户等级</th>
                    <th width="80px">审核状态</th>
				</c:if>
				<c:if test="${'buyerList' eq customer.sqlMap['type']}">
					<th width="100px">退款次数</th>
					<th width="10px">购买总额</th>
				</c:if>
				<th width="140px">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="seller">
			<tr>
                <c:if test="${'sellerList' eq customer.sqlMap['type']}">
                    <td>${seller.shop.shopNum}</td>
                </c:if>
				<td>${seller.userName}</td>
				<td>${seller.phoneNumber}</td>
                <td>${seller.from}</td>
				<td>${seller.orderCount}</td>
				<c:if test="${'sellerList' eq customer.sqlMap['type']}">
					<td>${seller.shop.fund.totalSale}</td>
					<td>${seller.shop.fund.loadAvailableFund + seller.shop.fund.saleAvailableFund + seller.shop.fund.shareAvailableFund + seller.shop.fund.bonusAvailableFund}</td>
					<td>${seller.shop.fund.loadProcessingFund + seller.shop.fund.saleProcessingFund + seller.shop.fund.shareProcessingFund + seller.shop.fund.bonusProcessingFund}</td>
					<td>${seller.shop.fund.loadUsedFund + seller.shop.fund.saleUsedFund + seller.shop.fund.shareUsedFund + seller.shop.fund.bonusUsedFund}</td>
                    <td><fmt:formatDate value="${seller.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td style="color: ${seller.shop.advancedShop == 1 ? 'orange' : 'cornflowerblue'}">${seller.shop.advancedShop == 1 ? '钻石小店' : '特约小店'}</td>
                    <td style="color: ${seller.shop.upgradeRequest.statusLabelColor}">${seller.shop.upgradeRequest != null ? seller.shop.upgradeRequest.statusLabel : '未申请'}</td>
				</c:if>
				<c:if test="${'buyerList' eq customer.sqlMap['type']}">
					<td>${seller.refundTimes}</td>
					<td>${seller.purchasesAmount}</td>
				</c:if>
				<td>
					<a href="javascript:void(0);" onclick="showDetail('${seller.id}')">详情</a>
					<c:if test="${'sellerList' eq customer.sqlMap['type']}">
                        <c:if test="${seller.hasTeamMember}">
                            <a href="javascript:void(0);" onclick="showTeam('${seller.id}','${seller.userName}')">战队</a>
                        </c:if>
                        <c:if test="${seller.hasWithdrawal}">
                            <a href="javascript:void(0);" onclick="showWithdrawalLog('${seller.id}')">提现历史</a>
                        </c:if>
                        <c:if test="${seller.shop.upgradeRequest != null}">
                            <a href="javascript:void(0);" onclick="showUpgradeRequestDialog('${seller.id}')">${seller.shop.upgradeRequest.status == 0 ? "审核申请" : "申请历史"}</a>
                        </c:if>
                        <c:if test="${seller.shop.upgradeRequest == null}">
                            <a href="javascript:void(0);" onclick="addUpgradeRequest('${seller.id}','${seller.userName}')">升级</a>
                        </c:if>
					</c:if>
					<c:if test="${'buyerList' eq customer.sqlMap['type']}">
                        <c:if test="${seller.hasReturn}">
						    <a href="javascript:void(0);" onclick="showRefundLog('${seller.id}')">退款历史</a>
                        </c:if>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>

	<div id="customerDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info"><!-- 买家详情 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					用户详情
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-bordered table-hover">
					<tr>
						<td width="20%">用户名：</td>
						<td width="30%"><span id="userName"></span></td>
						<td width="20%">头像：</td>
						<td width="30%"><img id="avatarUrl" src="" width="50px" height="50px" alt="无头像" /></td>
					</tr>
					<tr>
						<td>手机号：</td>
						<td><span id="phoneNumber"></span></td>
                        <td>身份证号：</td>
                        <td><span id="idCard"></span></td>
					</tr>
				</table>
			</div>
		</div>
	</div>

    <div id="upgradeRequestDialog" class="vcat-dialog" style="width: 50%;">
        <div class="panel panel-info"><!-- 买家详情 -->
            <div class="panel-heading">
                <div class="panel-title" style="font-size: 14px;">
                    店铺升级申请单
                    <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
                </div>
            </div>
            <div class="panel-body">
                <label style="font-weight: bold">申请详细信息：</label>
                <table class="table table-bordered">
                    <tr>
                        <th width="20%"><span class="pull-right">申请店铺名：</span></th>
                        <td width="30%"><span id="shopName"></span></td>
                        <th width="20%"><span class="pull-right">店铺手机号：</span></th>
                        <td width="30%"><span id="shopPhone"></span></td>
                    </tr>
                    <tr id="originalParentTR">
                        <th><span class="pull-right">上家店铺名：</span></th>
                        <td><span id="originalParentName"></span></td>
                        <th><span class="pull-right">上家店铺手机号：</span></th>
                        <td><span id="originalParentPhone"></span></td>
                    </tr>
                </table>
                <label style="font-weight: bold">申请审核日志：</label>
                <table id="upgradeRequestLogTable" class="table table-bordered">
                    <tr>
                        <th>备注</th>
                        <th width="80px">审核状态</th>
                        <th width="80px">操作人</th>
                        <th width="140px">操作时间</th>
                    </tr>
                </table>
                <shiro:hasPermission name="ec:customer:approvalUpgradeRequest">
                <label class="approval hide">拒绝原因：</label>
                <textarea id="approvalNote" style="width:98%;" rows="3" class="approval hide" placeholder="拒绝申请时必填"></textarea>
                <div class="form-actions approval hide" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
                    <input type="hidden" id="upgradeRequestId"/>
                    <input onclick="approvalUpgradeRequest($('#upgradeRequestId').val(),1,'通过')" class="btn btn-success" type="button" value="通过"/>
                    <input onclick="approvalUpgradeRequest($('#upgradeRequestId').val(),2,'拒绝')" class="btn btn-danger" type="button" value="拒绝"/>
                </div>
                </shiro:hasPermission>
            </div>
        </div>
    </div>
</body>
</html>