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
		function showLevel(levelTr,id){
			$('#levelId').val(id);
			$('#levelName').val(levelTr.find("td :eq(0)").html());
			$('#minExp').val(levelTr.find("td :eq(1)").html());
			$('#maxExp').val(levelTr.find("td :eq(2)").html());
            levelUrlReadyImg(levelTr.find("img").attr("src"));
			showDialog('levelDialog');
		}
        function toAdd(){
            $('#levelId').val('');
            $('#levelName').val('');
            $('#minExp').val('');
            $('#maxExp').val('2147483648');
            levelUrlClearImg();
            showDialog('levelDialog');
        }
		function updateLevelExp(id){
            var name = $('#levelName').val();
            var url = $('#levelUrl').val();
			var minExp = $('#minExp').val();
			var maxExp = $('#maxExp').val();
            if(checkNull("levelName","等级名称")){return false;}
			if(!isNull(minExp) && !isInteger(minExp)){
				alertx("最小经验值填写不正确！",function(){
					$('#minExp').focus();
				});
				return;
			}
			if(!isNull(maxExp) && !isInteger(maxExp)){
				alertx("最大经验值填写不正确！",function(){
					$('#maxExp').focus();
				});
				return;
			}
            if(isNull($('#levelUrl').val())){
                alertx("请上传等级图片！",function() {
                    $('#levelUrl_image_upload_button').trigger("click");
                });
                return false;
            }
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/updateLevel",
				data : {
					id : id,
                    name : name,
                    url : url,
					minExp : minExp,
					maxExp : maxExp
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
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
				<th width="10%">等级名称</th>
				<th width="10%">最小经验值</th>
				<th width="10%">最大经验值</th>
				<th width="10%">等级图标</th>
				<th width="10%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="level" varStatus="status">
			<tr id="level_${status.index}">
				<td>${level.name}</td>
				<td>${level.minExp}</td>
				<td>${level.maxExp}</td>
				<td><img src="${level.urlPath}" alt="等级图标"></td>
				<td>
					<a href="javascript:void(0);" onclick="showLevel($(this).parent().parent(),'${level.id}')">修改经验值</a>
                    <a href="javascript:void(0);" onclick="confirmx('确认删除等级${level.name}',function(){location.href=ctx+'/ec/customer/deleteLevel?id=${level.id}&name=${level.name}';});">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
    <button class="btn" onclick="toAdd()">添加等级</button>
	<div id="levelDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info"><!-- 买家详情 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					等级经验设置
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
				<table class="table table-responsive table-hover">
					<tbody>
					<tr>
						<td width="20%">等级名称：</td>
						<td width="80%">
                            <input type="text" class="input-small" id="levelName"/>
                        </td>
					</tr>
                    <tr>
                        <td>等级图标：</td>
                        <td>
                            <div class="controls">
                                <sys:imageUpload id="levelUrl" widthScale="41" heightScale="26"></sys:imageUpload>
                            </div>
                        </td>
                    </tr>
					<tr>
						<td>最小经验值：</td>
						<td><input type="text" class="input-small" id="minExp"/></td>
					</tr>
                    <tr>
                        <td>最大经验值：</td>
                        <td><input type="text" class="input-small" id="maxExp"/></td>
                    </tr>
					</tbody>
				</table>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="levelId"/>
					<input onclick="updateLevelExp($('#levelId').val())" class="btn btn-success" type="button" value="保 存"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>