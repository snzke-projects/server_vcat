<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分享活动列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		function checkActivate(id){
			confirmx("激活后商品将自动上架，且激活后不能删除该分享活动，确认激活？",function (){
				$.ajax({
					type : 'POST',
					url : ctx + "/ec/product/activateShare",
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
        function showLog(id,title){
            top.$.jBox.open("iframe:${ctx}/ec/product/shareLog?id="+id, title + " 活动详情",$(top.document).width()-220,$(top.document).height()-180,{
                buttons: {"确定": true},
                loaded: function () {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
        function showPie(id,title){
            $.ajax({
                type : "GET",
                url : ctx + "/ec/product/sharePieChart?id=" + id,
                success : function(data){
                    title = title + " 分享情况表";
                    flushPie(title,data);
                    showDialog("pieDialog",title);
                },
                error: function(XMLHttpRequest) {
                    return doError(XMLHttpRequest);
                }
            });
        }
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="share" action="${ctx}/ec/product/shareList" method="post" class="breadcrumb form-search">
	<div class="panel panel-info">
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				查询条件
			</div>
		</div>
		<div class="panel-body">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<label>商品分类：</label>
			<sys:treeselect id="category" name="product.category.id" value="${share.product.category.id}" labelName="product.category.name" labelValue="${share.product.category.name}"
							title="栏目" url="/ec/category/treeData" module="product" notAllowSelectRoot="false" cssClass="input-small"/>
			<label>关键字：</label>
			<form:input path="title" htmlEscape="false" maxlength="50" placeholder="标题、商品名称、分类名称"/>&nbsp;
			<label>活动时间：</label>
			<input id="st" name="sqlMap['st']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${share.sqlMap['st']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="活动开始时间大于该时间"/>
			<label>至</label>
			<input id="et" name="sqlMap['et']" type="text" readonly="readonly" maxlength="16" class="input-medium Wdate"
				   value="${share.sqlMap['et']}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" title="活动结束时间小于该时间"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
			<span class="help-inline" style="color:red">分享活动需在活动开始前一天之前激活！</span>
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-bordered table-condensed table-hover">
	<thead>
	<tr>
		<th width="5%">商品所属分类</th>
		<th width="20%">商品名称</th>
		<th width="20%">标题</th>
		<th width="8%">开始时间</th>
		<th width="8%">结束时间</th>
		<th width="5%">总分享次数</th>
        <th width="5%">已分享次数</th>
		<th width="5%">单次奖励金额(元)</th>
		<th width="5%">激活状态</th>
		<th width="5%">活动状态</th>
		<th width="8%">操作</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="share">
		<tr>
			<td><a href="javascript:void(0);" onclick="$('#categoryId').val('${share.product.category.id}');$('#categoryName').val('${share.product.category.name}');$('#searchForm').submit();return false;">${share.product.category.name}</a></td>
			<td><a href="javascript:void(0);" onclick="$('#title').val('${share.product.name}');$('#searchForm').submit();return false;">${fns:abbr(share.product.name,50)}</a></td>
			<td>
                <c:if test="${0 eq share.isActivate}"><a href="${ctx}/ec/product/shareForm?id=${share.id}">${fns:abbr(share.title,50)}</a></c:if>
                <c:if test="${1 eq share.isActivate}"><a href="javascript:void(0);" onclick="showLog('${share.id}','${share.title}')">${fns:abbr(share.title,50)}</a></c:if>
            </td>
			<td><fmt:formatDate value="${share.startTime}" pattern="yyyy-MM-dd HH:mm"/></td>
			<td><fmt:formatDate value="${share.endTime}" pattern="yyyy-MM-dd HH:mm"/></td>
			<td>${share.availableShare}</td>
            <td>${share.sharedCount}</td>
			<td>${share.fund}</td>
			<td style="color:<c:if test="${0 eq share.isActivate}">red</c:if><c:if test="${0 ne share.isActivate}">green</c:if>">${fns:getDictLabel(share.isActivate,'ec_activate' , '未激活')}</td>
			<td style="color:<c:if test="${'未开始' eq share.status}">red</c:if><c:if test="${'进行中' eq share.status}">orange</c:if><c:if test="${'已结束' eq share.status}">green</c:if>">${share.status}</td>
			<td>
				<shiro:hasPermission name="ec:product:edit">
					<c:if test="${0 eq share.isActivate}"><a href="javascript:void(0);" onclick="checkActivate('${share.id}')">激活</a></c:if>
					<a href="${ctx}/ec/product/shareForm?id=${share.id}">修改</a>
					<c:if test="${0 eq share.isActivate}"><a href="${ctx}/ec/product/deleteShare?id=${share.id}" onclick="return confirmx('确定要删除 ${share.title} 活动？',this.href);">删除</a></c:if>
				</shiro:hasPermission>
                <c:if test="${1 eq share.isActivate}">
                    <a href="javascript:void(0);" onclick="showLog('${share.id}','${share.title}')">查看</a>
                    <a href="javascript:void(0);" onclick="showPie('${share.id}','${share.title}')">饼图</a>
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
                分享情况表
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