<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规格属性管理</title>
	<meta name="decorator" content="default"/>
	<style>.table th td{text-align: center;}</style>
	<script type="text/javascript">
		function showDetail(id,name,value){
			$("#id").val(id);
			$("#name").val(name);
			$("#value").val(value);
			showDialog('specDialog');
		}
		function saveSpec(){
			var id = $("#id").val();
			var name = $("#name").val();
			var value = $("#value").val();
			if(isNull(name)){
				alertx("属性名称不能为空",function (){
					$("#name").focus();
				});
				return false;
			}
			if(isNull(value)){
				alertx("属性值不能为空",function (){
					$("#value").focus();
				});
				return false;
			}
			value = value.replace("，",",");
			value = value.replace("|",",");
			doSaveSpec(id,name,value);
		}
		function doSaveSpec(id,name,value){
			value = filterSame(value);
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/product/saveSpec",
				data : {
					id : id,
                    "category.id" : "${spec.category.id}",
					name : name,
					value : value
				},
				success : function(){
					try{
						alertx("保存规格属性 " + name + " 成功！",function(){
							location.href = location.href;
						});
					}catch(e){
						console.log("保存规格属性 " + name + " 失败："+e.message);
						return doError(e);
					}
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function deleteSpec(id,name){
			confirmx("确认删除 " + name + " ？",function (){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/product/deleteSpec",
					data : {
						id : id
					},
					success : function(){
						try{
							alertx("删除规格属性 " + name + " 成功！",function(){
								location.href = location.href;
							});
						}catch(e){
							console.log("删除规格属性 " + name + " 失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
		function filterSame(str){
			var ar2 = str.split(",");
			var array = new Array();
			var j=0
			for(var i=0;i<ar2.length;i++){
				if((array == "" || array.toString().match(new RegExp(ar2[i],"g")) == null)&&ar2[i]!=""){
					array[j] =ar2[i];
					j++;
				}
			}
			return array.toString();
		}
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-hover">
		<tr>
			<th width="10%">规格属性名称</th>
			<th width="80%">规格属性值</th>
			<th width="10%">操作</th>
		</tr>
		<c:forEach items="${page.list}" var="entity">
			<tr>
				<td><a href="javascript:void(0)" onclick="showDetail('${entity.id}','${entity.name}','${entity.value}')">${entity.name}</a></td>
				<td>${entity.value}</td>
				<td>
					<a href="javascript:void(0)" onclick="showDetail('${entity.id}','${entity.name}','${entity.value}')">修改</a>
					<a href="javascript:void(0)" onclick="deleteSpec('${entity.id}','${entity.name}')">删除</a>
				</td>
			</tr>
		</c:forEach>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
		<input onclick="showDetail('','','')" class="btn btn-success" type="button" value="添加属性"/>
	</div>

	<div id="specDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info"><!-- 退货单 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					规格属性
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('specDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-responsive table-hover">
					<tbody>
					<tr>
						<td width="30%">规格属性名称：</td>
						<td width="70%"><input id="name" type="text" maxlength="100" style="margin-bottom: 0px"/></td>
					</tr>
					<tr>
						<td colspan="2">规格属性值：</td>
					</tr>
					<tr>
						<td colspan="2">
							<textarea id="value" cols="10" rows="3" style="resize:none;width:95%;"></textarea>
							<span class="help-inline">多个属性值以逗号[,]分隔</span>
						</td>
					</tr>
					</tbody>
				</table>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="id">
					<input onclick="saveSpec()" class="btn btn-success" type="button" value="保存"/>
					<input onclick="showDialog('specDialog')" class="btn btn-default" type="button" value="返回"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>