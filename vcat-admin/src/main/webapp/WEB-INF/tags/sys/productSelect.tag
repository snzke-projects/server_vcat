<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="所选商品标签ID"%>
<%@ attribute name="name" type="java.lang.String" required="false" description="所选商品标签Name"%>
<%@ attribute name="value" type="java.lang.String" required="false" description="所选商品ID"%>
<%@ attribute name="queryParam" type="java.lang.String" required="false" description="查询参数（URL后缀：name=sss&isHot=0）"%>
<%@ attribute name="radio" type="java.lang.String" required="false" description="是否单选商品(默认true)"%>
<%@ attribute name="callback" type="java.lang.String" required="false" description="点击确定时需要回调的函数名称，回调函数会默认传入一个Array，包含当前选中的所有商品"%>
<%@ attribute name="showClearButton" type="java.lang.Boolean" required="false" description="是否显示清空按钮"%>
<script type="text/javascript">
	var ${id}ProductSelectRadio = "${radio}";
	if("" != ${id}ProductSelectRadio && "true" != ${id}ProductSelectRadio.toLowerCase() && "1" != ${id}ProductSelectRadio){
		${id}ProductSelectRadio = false;
	}else{
		${id}ProductSelectRadio = true;
	}
	var ${id}ProductSelectCallback = eval('${callback}');
    var ${id}QueryParam = "${queryParam}";
    var ${id}SelectedIds = "";
	$(function(){
		if("" != $("#${id}").val()){
            ${id}SelectedIds = $("#${id}").val();
            ${id}RefreshProduct();
		}
		$("#${id}CallbackButton").click(function(){
			var iframeWidth = $(top.document).width() * 0.8;
			var iframeHeight = $(top.document).height() * 0.6;

            var paramPath = isNull(${id}QueryParam) ? "" : "&" + ${id}QueryParam;

			top.$.jBox.open("iframe:${ctx}/ec/product/select?selectedIdsKey=${id}SelectedIds&radio="+${id}ProductSelectRadio+"&showClearButton=${showClearButton}&selectedIds=" + encodeURI(${id}SelectedIds) + paramPath, "选择商品",iframeWidth,iframeHeight,{
				buttons: {"确定":"ok",
                    <c:if test="${null == showClearButton || showClearButton}">"清除已选":"clear",</c:if>
                    "关闭":true},
				submit : function(buttonId,iframeDom){
					if(buttonId == "ok"){
						return ${id}DoCallBack(iframeDom);
					}else if(buttonId == "clear"){
						$("#${id}").val("");
                        ${id}SelectedIds = "";
						$("#${id}ProductUL").html("");
					}
				},loaded: function (h) {
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		});
	});

    function ${id}RefreshProduct(){
        ${id}DoCallBack(this);
    }

    function ${id}GetProductList(ids){
        var productList = null;
        $.ajax({
            url:ctx+"/ec/product/findListByIds?ids="+ids,
            async:false,
            success:function(products){
                productList = products;
            }
        });
        return productList;
    }

	/**
	 * 执行回调函数
	 * @param callBackThisLink 回调函数中this指针的对象
	 * @returns {*}
	 */
	function ${id}DoCallBack(callBackThisLink){
        var productArray = ${id}GetProductList(${id}SelectedIds);

        ${id}ShowProduct(productArray);

		if(isNull(productArray)){
			return true;
		}

		if(typeof(${id}ProductSelectCallback) == "function"){
			var callBackArg = ${id}ProductSelectRadio && !isNull(productArray) && productArray.length > 0 ? productArray[0] : productArray;
			return ${id}ProductSelectCallback.call(callBackThisLink,callBackArg);
		}

		return true;
	}
	/**
	 * 显示已选商品
	 * @param productList
	 */
	function ${id}ShowProduct(productList){
		var productIds = new Array();
		for(var i in productList){
            productIds.push(productList[i].id);
		}
		$("#${id}").val(productIds.join("|"));

        ${id}UpdateUL(productList);
	}

	/**
	 * 判断对象是否为空属性对象
	 * @param obj
	 * @returns {boolean}
	 */
	function ${id}IsEmptyObject(obj){
		for(var n in obj){return false}
		return true;
	}
    function ${id}ClearProductList(){
        $("#${id}").val('');
        $('#${id}ProductUL').html('');
        return false;
    }
    function ${id}DeleteProduct(trashDOM,id){
        ${id}SelectedIds = ${id}SelectedIds.replace('\|'+id,'');
        $(trashDOM).parent().remove();
        ${id}RefreshProduct();
    }

    function ${id}UpdateUL(productList){
        var html = "";
        var selectedIds = "";
        for(var i in productList){
            var product = productList[i];
            if(product){
                var liHtml = '<li title="'+product.name+'" style="margin-top: 5px">';
                liHtml += '<a href="javascript:void(0)" onclick="showViewPage(ctx + \'/ec/product/form?id=' + product.id + '&isView=true\',\'' + product.name + ' 商品详情\')">' + product.name + '</a>';
                liHtml += '&nbsp;&nbsp;<a href="javascript:void(0)" class="icon-trash" onclick="${id}DeleteProduct(this,\''+product.id+'\')"></a>';
                liHtml += '</li>';
                html += liHtml;

                selectedIds += "|" + product.id;
            }
        }
        ${id}SelectedIds = selectedIds;
        $('#${id}ProductUL').html(html);
    }
</script>
<input type="hidden" id="${id}" name="${name}" value="${value}"/>
<ul id="${id}ProductUL" style="list-style: none;"></ul>
<a id="${id}CallbackButton" href="javascript:void(0)" class="btn">选择商品</a>
