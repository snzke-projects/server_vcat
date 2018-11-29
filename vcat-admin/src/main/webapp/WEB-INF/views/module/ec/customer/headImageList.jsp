<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>首页背景图列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">include('kindeditor_lib','${ctxStatic}/kindeditor/',['kindeditor-all-min.js']);</script>
	<script type="text/javascript">include('kindeditor_lang','${ctxStatic}/kindeditor/lang/',['zh_CN.js']);</script>
	<link href="${ctxStatic}/kindeditor/themes/default/default.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript">
        var articleUrlPrefix = '${fns:getDictSingleValue('ec_article_url_prefix','')}';
        var productUrlPrefix = '${fns:getDictSingleValue('ec_head_image_product_url_prefix','')}';
        var pageUrl;
        $(function (){
            $('#productIdCallbackButton').hide();
        });
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function showEntity(headImageTr,id,name,displayOrder,type,pageUrl){
            clearArticle();
			$('#headImageId').val(id);
			$('#name').val(name);
			$('#imageView').attr("src",headImageTr.find("td img").attr("src"));
			$('#url').val(getFileId(headImageTr.find("td img").attr("src")));
			$('#displayOrder').val(displayOrder);
            $('#headImageType').val(type);
            $('#headImageType').select2();
//            clearProductList();
            if(pageUrl && pageUrl.indexOf(articleUrlPrefix) == 0){
                var articleId = pageUrl.substring(articleUrlPrefix.length,pageUrl.length);
                $.getJSON(ctx + '/cms/article/findByIds',{ids:articleId},function (datas){
                    if(datas && datas.length == 1){
                        selectArticle(datas[0][1],datas[0][2]);
                    }
                });
            }
			showDialog('headImageDialog');
		}
		function saveEntity(id){
			var name = $("#name").val();
			var url = $("#url").val();
			var displayOrder = $("#displayOrder").val();
            var type = $("#headImageType").val();
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
            if(isNull(type)){
                alertx("请选择轮播图类型！",function(){
                    $("#headImageType").select2('open');
                });
                return false;
            }
			$.ajax({
				type : 'POST',
				url : "${ctx}/ec/customer/saveHeadImage",
				data : {
					id : id,
					name : name,
					url : url,
					displayOrder : displayOrder,
                    type : type,
                    pageUrl : encodeURIComponent(pageUrl)
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
		function activateEntity(id,activate){
			if("0" == activate){
				activate = "1";
			}else if("1" == activate){
				activate = "0";
			}
			confirmx("确认"+("0" == activate ? "取消" : "")+"激活该图片？",function(){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/customer/activateHeadImage",
					data : {
						id : id
						,isActivate : activate
					},
					success : function(){
						alertx(("0" == activate ? "取消" : "")+"激活成功！",function(){
							location.href = location.href;
						});
					},
					error: function(XMLHttpRequest) {
						return doError(XMLHttpRequest);
					}
				});
			});
		}
		function deleteEntity(id){
			confirmx("确认删除该图片？",function(){
				$.ajax({
					type : 'POST',
					url : "${ctx}/ec/customer/deleteHeadImage",
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
		function toAddEntity(){
			$('#imageView').attr("src","");
			$('#name').val("");
			$('#url').val("");
			$('#headImageId').val("");
			var maxOrder = 0;
			$('.orderTD').each(function(){
				var order = this.innerHTML;
				if(!isNull(order) && maxOrder < parseInt(order)){
					maxOrder = order;
				}
			});

			$("#displayOrder").val(parseInt(maxOrder) + 30);
			showDialog('headImageDialog');
		}
		KindEditor.ready(function(K) {
			var editor = K.editor({
				allowFileManager : false,
				uploadJson : '${ctx}/kindeditor/upload?widthScale=720&heightScale=360'
			});
			$('#image_upload_button').click(function() {
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
            clearArticle();
            pageUrl = productUrlPrefix + product.id + "&type=" + $('#productType').val();
        }
        function checkProductType(type){
            if(type && "" != type){
                $('#productIdCallbackButton').show();
                productIdQueryParam = "sqlMap[\'productType\']=" + type;
            }else{
                $('#productIdCallbackButton').hide();
            }
        }
	</script>
</head>
<body>
	<sys:message content="${message}"/>
    <form:form id="searchForm" action="${ctx}/ec/customer/headImageList" method="post" class="form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    </form:form>
	<table id="contentTable" class="table table-bordered table-hover">
		<thead>
			<tr>
                <th width="10%">类型</th>
				<th width="30%">图片名称</th>
				<th width="30%">预览</th>
				<th width="10%">排序</th>
				<th width="10%">是否激活</th>
				<th width="10%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="headImage" varStatus="status">
			<tr id="headImage_${status.index}">
                <td>${fns:getDictLabel(headImage.type,'ec_head_image_type','未知类型')}</td>
				<td>
                    <a href="javascript:void(0);" onclick="showEntity($(this).parent().parent(),'${headImage.id}','${headImage.name}','${headImage.displayOrder}','${headImage.type}','${headImage.pageUrl}')">${headImage.name}</a>
                </td>
				<td><img src="${headImage.urlPath}" alt="${headImage.name}" style="width: 360px;height: 144px;"></td>
				<td class="orderTD">${headImage.displayOrder}</td>
				<td style="color: ${headImage.activateColor}">${headImage.activateLabel}</td>
				<td>
					<a href="javascript:void(0);" onclick="activateEntity('${headImage.id}','${headImage.isActivate}')"><c:if test="${'1' eq headImage.isActivate}">取消</c:if>激活</a>
					<a href="javascript:void(0);" onclick="showEntity($(this).parent().parent(),'${headImage.id}','${headImage.name}','${headImage.displayOrder}','${headImage.type}','${headImage.pageUrl}')">修改</a>
					<a href="javascript:void(0);" onclick="deleteEntity('${headImage.id}')">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<button class="btn btn-success" onclick="toAddEntity()">添加</button>

	<div id="headImageDialog" class="vcat-dialog" style="width: 50%;">
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
                            <select id="headImageType" class="input-medium">
                                <option value="">请选择</option>
                                <c:forEach items="${fns:getDictList('ec_head_image_type')}" var="imageType">
                                    <option value="${imageType.value}">${imageType.label}</option>
                                </c:forEach>
                            </select>
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
                            <img src="" id="imageView" style="width: 360px;height: 180px;"/>
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
                    <tr>
                        <td>图片指向文章：</td>
                        <td>
                            <input type="hidden" id="articleId" name="articleId"/>
                            <ol id="articleSelectList"></ol>
                            <a id="articleButton" href="javascript:" class="btn">选择文章链接</a>
                            <script type="text/javascript">
                                var articleSelect = {};
                                function selectArticle(id,title){
                                    articleSelect.id = id;
                                    articleSelect.title = title;
                                    pageUrl = articleUrlPrefix + id;
                                    productIdClearProductList();
                                    articleSelectRefresh();
                                }
                                function articleSelectRefresh(){
                                    $("#articleSelectList").children().remove();
                                    if(articleSelect.id){
                                        $("#articleSelectList").append("<li>"+articleSelect.title+"&nbsp;&nbsp;<a href=\"javascript:\" onclick=\"articleSelect = {};$(this).parent().remove();pageUrl=''\">×</a></li>");
                                        $('#articleId').val(articleSelect.id);
                                    }
                                }
                                $("#articleButton").click(function(){
                                    var systemCategoryId = '${fns:getDictSingleValue('ec_system_article_category_id', '1')}';
                                    top.$.jBox.open("iframe:${ctx}/cms/article/selectList?pageSize=10&category.id="+systemCategoryId+"&lockCategory=1&selectType=radio&callBackFuncName=selectArticle", "选择文章",$(top.document).width()-220,$(top.document).height()-180,{
                                        buttons:{"确定":true}, loaded:function(h){
                                            $(".jbox-content", top.document).css("overflow-y","hidden");
                                        }
                                    });
                                });
                                function clearArticle(){
                                    pageUrl = "";
                                    articleSelect = {};
                                    articleSelectRefresh();
                                }
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td>图片指向商品：</td>
                        <td>
                            <select id="productType" class="input-medium" onchange="checkProductType(this.value)">
                                <option value="">请选择商品类型</option>
                                <option value="2">非抵扣商品</option>
                                <option value="3">全额抵扣商品</option>
                                <option value="4">部分抵扣商品</option>
                            </select>
                            <sys:productSelect id="productId" name="productId" callback="setProduct"></sys:productSelect>
                        </td>
                    </tr>
                </table>
				<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
					<input type="hidden" id="headImageId"/>
					<input onclick="saveEntity($('#headImageId').val())" class="btn btn-success" type="button" value="保 存"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>