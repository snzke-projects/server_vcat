<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/common/express.js" type="text/javascript"></script>
<div id="expressDialog" class="vcat-dialog">
	<div class="panel panel-info"><!-- 物流动态 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				物流动态
				<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('expressDialog')"></a>
			</div>
		</div>
		<div class="panel-body">
			<table id="expressTable" class="table table-responsive table-hover">
				<tbody>
				<tr class="expressTH">
					<th width="30%">时间</th>
					<th width="70%">详情</th>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>