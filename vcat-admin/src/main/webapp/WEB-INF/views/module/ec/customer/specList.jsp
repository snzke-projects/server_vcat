<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>卖家等级列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function showSpec(specTr,id,preTrId,nextTrId){
			$('#specId').val(id);
			$('#preTrId').val(preTrId);
			$('#nextTrId').val(nextTrId);
			$('#saleVolume').val(specTr.find("td :eq(0)").html());
			$('#withdrawal').val(specTr.find("td :eq(1)").html());
			showDialog('specDialog');
		}
		function updateSpec(id){
			var saleVolume = $('#saleVolume').val();
			var withdrawal = $('#withdrawal').val();
			var preTrId = $('#preTrId').val();
			var nextTrId = $('#nextTrId').val();
			var preSaleVolume = $('#'+preTrId).length > 0 ? parseInt($('#'+preTrId+' td :eq(0)').html()) : 0;
			var nextSaleVolume = $('#'+nextTrId).length > 0 ? parseInt($('#'+nextTrId+' td :eq(0)').html()) : 99999999999;
			var preWithdrawal = $('#'+preTrId).length > 0 ? parseInt($('#'+preTrId+' td :eq(1)').html()) : 0;
			var nextWithdrawal = $('#'+nextTrId).length > 0 ? parseInt($('#'+nextTrId+' td :eq(1)').html()) : 99999999999;
			if(!isNull(saleVolume) && !isDecimal(saleVolume) && parseInt(saleVolume) <= 0){
				alertx("销售金额填写不正确！",function(){
					$('#saleVolume').focus();
				});
				return;
			}
			if(!isNull(saleVolume) && parseInt(saleVolume) <= preSaleVolume){
				alertx("销售金额不能小于等于" + preSaleVolume + "！",function(){
					$('#saleVolume').focus();
				});
				return;
			}
			if(!isNull(saleVolume) && parseInt(saleVolume) >= nextSaleVolume){
				alertx("销售金额不能大于等于 "+nextSaleVolume+"！",function(){
					$('#saleVolume').focus();
				});
				return;
			}
			if(!isNull(withdrawal) && !isDecimal(withdrawal) && parseInt(withdrawal) <= 0){
				alertx("提现金额填写不正确！",function(){
					$('#withdrawal').focus();
				});
				return;
			}
			if(!isNull(withdrawal) && parseInt(withdrawal) <= preWithdrawal){
				alertx("提现金额不能小于等于 "+preWithdrawal+"！",function(){
					$('#withdrawal').focus();
				});
				return;
			}
			if(!isNull(withdrawal) && parseInt(withdrawal) >= nextWithdrawal){
				alertx("提现金额不能大于等于 "+nextWithdrawal+"！",function(){
					$('#withdrawal').focus();
				});
				return;
			}
			if(parseInt(withdrawal) >= parseInt(saleVolume)){
				alertx("提现金额不能大于销售金额！",function(){
					$('#withdrawal').focus();
				});
				return;
			}
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/saveSpec",
				data : {
					id : id,
					saleVolume : saleVolume,
					withdrawal : withdrawal
				},
				success : function(){
					alertx("保存成功！",function(){
						location.href = location.href;
					});
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function deleteSpec(id){
			confirmx("确认删除该上架奖励提现费率？",function(){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/customer/deleteSpec",
					data : {id : id},
					success : function(){
						alertx("删除成功！",function(){
							location.href = location.href;
						});
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
		function toAddSpec(){
			$('#saleVolume').val("");
			$('#withdrawal').val("");
			$('#specId').val("");
			showDialog('specDialog');
		}
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
				<th width="40%">销售金额</th>
				<th width="40%">可提现金额</th>
				<th width="20%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="spec" varStatus="status">
			<tr id="spec_${status.index}">
				<td>${spec.saleVolume}</td>
				<td>${spec.withdrawal}</td>
				<td>
					<a href="javascript:void(0);" onclick="showSpec($(this).parent().parent(),'${spec.id}','spec_${status.index - 1}','spec_${status.index+1}')">修改</a>
					<a href="javascript:void(0);" onclick="deleteSpec('${spec.id}')">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<button class="btn btn-success" onclick="toAddSpec()">添加</button>

	<div id="specDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info"><!-- 上架奖励提现率 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					上架奖励提现率设置
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
				<span>当小店销售金额达到</span>
				<input type="text" id="saleVolume" name="saleVolume" style="width:50px;"/>
				<span>元时，该小店获得</span>
				<input type="text" id="withdrawal" name="withdrawal" style="width:50px;"/>
				<span>可提现金额（元）</span>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="specId"/>
					<input type="hidden" id="preTrId"/>
					<input type="hidden" id="nextTrId"/>
					<input onclick="updateSpec($('#specId').val())" class="btn btn-success" type="button" value="保 存"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>