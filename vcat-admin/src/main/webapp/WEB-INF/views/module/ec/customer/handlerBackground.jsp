<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>小店背景图维护</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor-all-min.js']);</script>
	<script type="text/javascript">include('kindeditor_lang','${ctxStatic}/kindeditor/lang/',['zh_CN.js']);</script>
	<link href="${ctxStatic}/kindeditor/themes/default/default.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript">
		KindEditor.ready(function(K) {
			var editor = K.editor({
				allowFileManager : false,
				uploadJson : '${ctx}/kindeditor/upload?widthScale=640&heightScale=200'
			});
			$('#image_upload_button').click(function() {
				editor.loadPlugin('image', function() {
					editor.plugin.imageDialog({
						clickFn : function(url, title, width, height, border, align) {
							$('#imageViewSpan').html("<img src='"+url+"' alt='图片' style='width: 240px;height: 102px;'/>");
							$('#imageUrl').val(getFileId(url));
							editor.hideDialog();
						}
					});
				});
			});
		});
		function getFileId(url) {
			var location = url.split("/");
			if(location.length > 5){
				return location[5];
			}
			return null;
		}
		function showImage(id,name,urlPath,displayOrder){
			$('#imageId').val(id);
			$('#imageName').val(name);
			$('#displayOrder').val(displayOrder);
			$('#imageUrl').val(getFileId(urlPath));
			$('#imageViewSpan').html("<img src='"+urlPath+"' alt='图片' style='width: 240px;height: 102px;'/>");
			showDialog('imageDialog');
		}
		function saveImage(){
			if(isNull($('#imageName').val())){
				alertx("请输入图片名称！",function(){
					$('#imageName').focus();
				});
				return;
			}
			if(isNull($('#imageUrl').val())){
				alertx("请上传图片！",function(){
					$('#image_upload_button').focus();
				});
				return;
			}
			if(isNull($('#displayOrder').val()) || !isInteger($('#displayOrder').val())){
				alertx("图片排序格式输入不正确！",function(){
					$('#imageName').focus();
				});
				return;
			}
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/handlerBackground",
				data : {
					id : $('#imageId').val(),
					name : $('#imageName').val(),
					url : $('#imageUrl').val(),
					displayOrder : $('#displayOrder').val()
				},
				success : function(){
					try{
						alertx("保存背景图片成功！",function(){
							location.href = location.href;
						});
					}catch(e){
						console.log("保存背景图片失败："+e.message);
						return doError(e);
					}
				},
				error: function(XMLHttpRequest) {
					return doError(XMLHttpRequest);
				}
			});
		}
		function activateImage(id,isActivate){
            var title = "1" == isActivate ? "取消" : "";
			confirmx("确认"+title+"激活该背景图片？",function (){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/customer/activateBackground",
					data : {
						id : id
                        ,isActivate : ("1" == isActivate ? 0 : 1)
					},
					success : function(){
						try{
							alertx(title+"激活背景图片成功！",function(){
								location.href = location.href;
							});
						}catch(e){
							console.log(title+"激活背景图片失败："+e.message);
							return doError(e);
						}
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}

        function deleteImage(id){
            confirmx("确认删除该背景图片？",function (){
                $.ajax({
                    type : 'POST',
                    url : "${ctx}/ec/customer/deleteBackground",
                    data : {
                        id : id
                    },
                    success : function(){
                        try{
                            alertx("删除背景图片成功！",function(){
                                location.href = location.href;
                            });
                        }catch(e){
                            console.log("删除背景图片失败："+e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }
		function addImage(){
			$('#imageId').val("");
			$('#imageName').val("");
			$('#displayOrder').val("");
			$('#imageUrl').val("");
			$('#imageViewSpan').html("");
			showDialog('imageDialog');
		}
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<table class="table table-hover table-bordered">
		<thead>
		<tr>
			<th width="30">图片名称</th>
			<th width="30">缩略图</th>
			<th width="30">排序</th>
			<th width="10">操作</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach items="${imageList}" var="image" varStatus="status">
				<tr>
					<td width="30">${image.name}</td>
					<td width="30"><img src="${image.urlPath}" style="width: 240px;height: 102px;"/></td>
					<td width="30">${image.displayOrder}</td>
					<td width="10">
						<a href="javascript:void(0);" onclick="showImage('${image.id}','${image.name}','${image.urlPath}','${image.displayOrder}')">修改</a>
                        <a href="javascript:void(0);" onclick="activateImage('${image.id}','${image.isActivate}')">${'1' eq image.isActivate ? '取消' : ''}激活</a>
						<a href="javascript:void(0);" onclick="deleteImage('${image.id}')">删除</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;padding: 5px 15px 5px;">
		<input onclick="addImage()" class="btn btn-success" type="button" value="添 加"/>
	</div>
	<div id="imageDialog" class="vcat-dialog">
		<div class="panel panel-info"><!-- 图片属性 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					图片属性
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('imageDialog')"></a>
				</div>
			</div>
			<div class="panel-body">
				<table id="imageTable" class="table table-responsive table-hover">
					<tbody>
						<tr>
							<td width="30%">图片名称：</td>
							<td width="70%"><input type="text" id="imageName" name="name"/></td>
						</tr>
						<tr>
							<td>图片：</td>
							<td>
								<span id="imageViewSpan"></span>
								<input type="button" id="image_upload_button" value="上传图片" />
							</td>
						</tr>
						<tr>
							<td>图片排序：</td>
							<td>
								<input type="text" id="displayOrder" name="displayOrder"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input type="hidden" id="imageId" name="id"/>
								<input type="hidden" id="imageUrl" name="url"/>
								<input onclick="saveImage()" class="btn btn-success" type="button" value="保 存"/>
								<input onclick="showDialog('imageDialog')" class="btn" type="button" value="返 回"/>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>