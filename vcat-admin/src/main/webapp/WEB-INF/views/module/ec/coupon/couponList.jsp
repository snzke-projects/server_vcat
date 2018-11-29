<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>券列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function activate(id){
			confirmx("激活后立即生效且激活后不能修改、删除该券，确认激活？",function (){
				$.ajax({
					type : 'POST',
					url : ctx + "/ec/coupon/activateCoupon",
					data : {id : id},
					success : function(){
						try{
							alertx("激活成功！",function(){
								location.href = location.href;
							});
						}catch(e){
							console.log("激活失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
        function showLog(id){
            top.$.jBox.open("iframe:${ctx}/ec/coupon/couponLog?id="+id, "券详情",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
        function showPie(id){
            $.ajax({
                type : "GET",
                url : ctx + "/ec/coupon/couponPieChart?id=" + id,
                success : function(data){
                    flushPie("卷发放情况表",data);
                    showDialog("pieDialog","卷发放情况表");
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="coupon" action="${ctx}/ec/coupon/couponList" method="post" class="breadcrumb form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<label>活动时间：</label>
			<input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${coupon.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="活动开始时间大于该时间"/>
			<label>至</label>
			<input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${coupon.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="活动结束时间小于该时间"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-condensed table-hover">
	<thead>
	<tr>
        <th width="8%">名称</th>
        <th width="5%">额度</th>
        <th width="8%">类型</th>
		<th width="5%">总数量</th>
        <th width="5%">已发放次数</th>
        <th width="8%">开始时间</th>
        <th width="8%">结束时间</th>
		<th width="5%">激活状态</th>
		<th width="5%">活动状态</th>
		<th width="8%">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="coupon">
		<tr>
            <td>${coupon.name}</td>
            <td>${coupon.fund}</td>
            <td>${fns:getDictLabel(coupon.type, 'ec_coupon_type', '未知类型')}</td>
            <td>${coupon.total}</td>
            <td>${coupon.couponUsedCount}</td>
			<td><fmt:formatDate value="${coupon.startTime}" pattern="yyyy-MM-dd HH:mm"/></td>
			<td><fmt:formatDate value="${coupon.endTime}" pattern="yyyy-MM-dd HH:mm"/></td>
			<td style="color:${coupon.activateColor}">${fns:getDictLabel(coupon.isActivate,'ec_activate' , '未激活')}</td>
			<td style="color:${coupon.statusColor}">${coupon.status}</td>
			<td>
				<shiro:hasPermission name="ec:coupon:edit">
					<c:if test="${0 eq coupon.isActivate}"><a href="javascript:void(0);" onclick="activate('${coupon.id}')">激活</a></c:if>
					<c:if test="${0 eq coupon.isActivate}"><a href="${ctx}/ec/coupon/couponForm?id=${coupon.id}">修改</a></c:if>
					<c:if test="${0 eq coupon.isActivate}"><a href="${ctx}/ec/coupon/deleteCoupon?id=${coupon.id}" onclick="return confirmx('确定要删除该邀请活动？',this.href);">删除</a></c:if>
				</shiro:hasPermission>
                <c:if test="${1 eq coupon.isActivate}">
                    <a href="javascript:void(0);" onclick="showLog('${coupon.id}')">查看</a>
                    <a href="javascript:void(0);" onclick="showPie('${coupon.id}')">饼图</a>
                </c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
<div id="pieDialog" class="vcat-dialog" style="width: 50%;">
    <div class="panel panel-info"><!-- 退货单 -->
        <div class="panel-heading">
            <div class="panel-title">
                发放情况表
                <a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('pieDialog')"></a>
            </div>
        </div>
        <div class="panel-body">
            <charts:pie id="pie" unit="次"></charts:pie>
        </div>
    </div>
</div>
</body>
</html>