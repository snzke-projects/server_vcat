<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推荐页列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor-all-min.js']);</script>
	<script type="text/javascript">include('kindeditor_lang','${ctxStatic}/kindeditor/lang/',['zh_CN.js']);</script>
	<link href="${ctxStatic}/kindeditor/themes/default/default.css" type="text/css" rel="stylesheet" />
    <style type="text/css">
        #categoryIdTr div{
            margin-bottom: 0px;
        }
    </style>
	<script type="text/javascript">
        var widthScale;
        var heightScale;
        var paramKey = {category : "categoryId",selection : "productId"};
        var codeMap = {"first":"1"
            <c:forEach items="${page.list}" var="page">
            <c:if test="${page.codeString != null && page.codeString != ''}">
            ,"${page.id}" : ${page.codeString}
            </c:if>
            </c:forEach>
        };
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function showEntity(featuredPageTr,id,name,displayOrder,typeShow,width,height,type){
			$('#featuredPageId').val(id);
			$('#name').val(name);
			$('#imageView').attr("src",featuredPageTr.find("td img").attr("src"));
			$('#url').val(getFileId(featuredPageTr.find("td img").attr("src")));
			$('#displayOrder').val(displayOrder);
            $('#featuredPageTypeSpan').html(typeShow);
            $('#featuredPageType').val(type);

            var code = codeMap[id];

            if(type == 'selection'){
                $('#productIdTr').show();
                $('#productTypeTr').show();
                if(!isNull(code) && !isNull(code.productId)){
                    $('#productId').val(code.productId);
                    productIdSelectedIds = code.productId;
                    productIdRefreshProduct();
                    $('#productType').val(code.type);
                }
                $('#productType').select2();
                $('#categoryIdTr').hide();
            }else if(type == 'category'){
                $('#categoryIdTr').show();
                if(!isNull(code) && !isNull(code.categoryId)){
                    $('#categoryId').val(code.categoryId);
                    $.getJSON(ctx + "/ec/category/getCategory",{id:code.categoryId},function (category){
                        $('#categoryName').val(category.name);
                    });
                }
                $('#productIdTr').hide();
                $('#productTypeTr').hide();
            }else {
                $('#categoryIdTr').hide();
                $('#productIdTr').hide();
                $('#productTypeTr').hide();
            }
            widthScale = width;
            heightScale = height;

			showDialog('featuredPageDialog');
		}
		function saveEntity(id){
			var name = $("#name").val();
			var url = $("#url").val();
			var displayOrder = $("#displayOrder").val();
			if(isNull(name)){
				alertx("图片名称不能为空！",function(){
					$("#name").focus();
				});
                return false;
			}
			if(isNull(url)){
				alertx("请上传一张图片！",function(){
					$("#image_upload_button").trigger("click");
				});
                return false;
			}
			if(isNull(displayOrder) || !isNumber(displayOrder)){
				alertx("图片排序为空或不正确！",function(){
					$("#displayOrder").focus();
				});
                return false;
			}
            var paramsUrl = "";
            paramsUrl += isNull($('#productId').val()) ? "" : "&sqlMap['productId']=" + $('#productId').val();
            paramsUrl += $('#featuredPageType').val() == 'selection' ? "&sqlMap['type']=" + $('#productType').val() : "";
            paramsUrl += isNull($('#categoryId').val()) ? "" : "&sqlMap['categoryId']=" + $('#categoryId').val();
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/saveFeaturedPage?" + paramsUrl,
				data : {
					id : id,
					name : name,
					url : url,
					displayOrder : displayOrder
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
		KindEditor.ready(function(K) {
			var editor = K.editor({
				allowFileManager : false
			});
			$('#image_upload_button').click(function() {
                editor['uploadJson'] = '${ctx}/kindeditor/upload?widthScale='+widthScale+'&heightScale='+heightScale;
				editor.loadPlugin('image', function() {
					editor.plugin.imageDialog({
						clickFn : function(url, title, width, height, border, align) {
							$('#imageView').attr("src",url);
							$('#url').val(getFileId(url));
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
        function setProduct(product){

        }
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
                <th width="100px">类型</th>
				<th>名称</th>
				<th width="100px">预览</th>
				<th width="80px">排序</th>
                <td width="100px">图片尺寸</td>
                <%--<th width="10%">是否激活</th>--%>
				<th width="60px">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="featuredPage" varStatus="status">
			<tr id="featuredPage_${status.index}">
                <td>${fns:getDictLabel(featuredPage.type,'ec_featured_page_type','未知类型')}</td>
				<td>
                    <a href="javascript:void(0);" onclick="showEntity($(this).parent().parent(),'${featuredPage.id}','${featuredPage.name}','${featuredPage.displayOrder}','${fns:getDictLabel(featuredPage.type,'ec_featured_page_type','未知类型')}','${featuredPage.width}','${featuredPage.height}','${featuredPage.type}')">${featuredPage.name}</a>
                </td>
				<td><img src="${featuredPage.url}" alt="${featuredPage.name}"></td>
				<td class="orderTD">${featuredPage.displayOrder}</td>
                <td>宽：${featuredPage.width} 高：${featuredPage.height}</td>
				<%--<td style="color: ${featuredPage.activateColor}">${featuredPage.activateLabel}</td>--%>
				<td>
					<a href="javascript:void(0);" onclick="showEntity($(this).parent().parent(),'${featuredPage.id}','${featuredPage.name}','${featuredPage.displayOrder}','${fns:getDictLabel(featuredPage.type,'ec_featured_page_type','未知类型')}','${featuredPage.width}','${featuredPage.height}','${featuredPage.type}')">修改</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>

	<div id="featuredPageDialog" class="vcat-dialog" style="width: 50%;">
		<div class="panel panel-info"><!-- 保存图片 -->
			<div class="panel-heading">
				<div class="panel-title" style="font-size: 14px;">
					保存图片
					<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog($(this).parents('.vcat-dialog').attr('id'))"></a>
				</div>
			</div>
			<div class="panel-body">
                <table class="table table-bordered">
                    <tr>
                        <td>类型：</td>
                        <td>
                            <span id="featuredPageTypeSpan"></span>
                            <input type="hidden" id="featuredPageType"/>
                        </td>
                    </tr>
                    <tr>
                        <td>图片名称：</td>
                        <td>
                            <input type="text" id="name" maxlength="50" class="input-xlarge">
                        </td>
                    </tr>
                    <tr>
                        <td>图片：</td>
                        <td>
                            <img src="" id="imageView" style="max-width: 360px;max-height: 150px;"/>
                            <input type="hidden" id="url"/>
                            <input type="button" id="image_upload_button" value="上传图片" />
                        </td>
                    </tr>
                    <tr>
                        <td>排序：</td>
                        <td>
                            <input type="text" id="displayOrder" maxlength="10" class="input-mini">
                        </td>
                    </tr>
                    <tr id="productIdTr">
                        <td>图片指向商品页面：</td>
                        <td>
                            <sys:productSelect id="productId" name="productId" callback="setProduct"></sys:productSelect>
                        </td>
                    </tr>
                    <tr id="productTypeTr">
                        <td>链接商品类型：</td>
                        <td>
                            <select id="productType" class="input-small">
                                <option value="2">V猫专享商场</option>
                                <option value="3">全额抵扣</option>
                                <option value="4">部分抵扣</option>
                            </select>
                        </td>
                    </tr>
                    <tr id="categoryIdTr">
                        <td>图片指向分类页面：</td>
                        <td>
                            <sys:treeselect id="category" name="category.id" value="" labelName="parent.name" labelValue="请选择分类"
                                            title="分类" url="/ec/category/treeData" extId=""/>
                        </td>
                    </tr>
                </table>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="featuredPageId"/>
					<input onclick="saveEntity($('#featuredPageId').val())" class="btn btn-success" type="button" value="保 存"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>