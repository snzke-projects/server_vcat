<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<div class="panel panel-info">
	<div class="panel-heading">
		<div class="panel-title" style="font-size: 14px;">
			商品规格列表
		</div>
	</div>
	<div class="panel-body">
		<table id="itemTable" class="table table-bordered table-hover">
			<tbody>
			<tr>
				<th class="specNameTitleTH" width="100px">货号</th>
				<c:forEach items="${product.specNameArray}" var="specName">
					<th class="specTableHead">${specName}</th>
				</c:forEach>
                <th width="50px" title="=(结算价 + 佣金 + 分红 + 一级团队分红 + 二级团队分红 + 扣点 + V猫币)">销售价</th>
                <shiro:hasPermission name="ec:showEarning">
                    <th width="50px" title="进价">结算价</th>
                </shiro:hasPermission>
                <th width="50px">销售佣金</th>
                <shiro:hasPermission name="ec:showEarning">
                    <th width="50px">分红</th>
                    <th width="50px">一级团队分红</th>
                    <th width="50px">二级团队分红</th>
                    <th width="80px">扣点金额</th>
                    <%--<th width="50px">可用V猫币</th>--%>
                </shiro:hasPermission>
				<th width="30px">库存</th>
                <%--<th width="50px"><span title="购物券全额抵扣库存">全额库存</span></th>--%>
                <%--<th width="60px"><span title="购物券非全额抵扣库存">非全额库存</span></th>--%>
                <th width="50px">重量</th>
				<th width="50px" class="<c:if test="${isView}">hide</c:if>">操作</th>
			</tr>
			<c:forEach items="${product.itemList}" var="item" varStatus="itemIndex">
				<tr id="templateItemRow_${itemIndex.index}" class="templateItemRow">
					<td>
						<input type="hidden" name="itemList[${itemIndex.index}].id" value="${item.id}">
						<input type="text" title="规格货号" value="${item.itemSn}" name="itemList[${itemIndex.index}].itemSn" maxlength="60" class="itemSn input-medium"/>
					</td>
					<input type="hidden" name="itemList[${itemIndex.index}].name" value="${item.name}">
					<c:forEach items="${product.specNameArray}" var="specName" varStatus="specIndex">
					<td class="${specName}TD" specValue="${item.spec[specName]}">
						<label>${item.spec[specName]}</label>
						<input type="hidden" name="itemList[${itemIndex.index}].specList[${specIndex.index}].name" value="${specName}"/>
						<input type="hidden" name="itemList[${itemIndex.index}].specList[${specIndex.index}].value" value="${item.spec[specName]}"/>
					</td>
					</c:forEach>
                    <td><input type="text" title="商品规格 ${item.name} 销售价" value="${item.retailPrice}" name="itemList[${itemIndex.index}].retailPrice" maxlength="13" class="input-mini retailPrice number"/></td>
                    <shiro:hasPermission name="ec:showEarning">
                        <td><input type="text" title="商品规格 ${item.name} 结算价（进价）" value="${item.purchasePrice}" name="itemList[${itemIndex.index}].purchasePrice" maxlength="13" class="input-mini purchasePrice number"/></td>
                    </shiro:hasPermission>
                    <td><input type="text" title="商品规格 ${item.name} 销售佣金" value="${item.saleEarning}" name="itemList[${itemIndex.index}].saleEarning" maxlength="13" class="input-mini saleEarning number"/></td>
                    <shiro:hasPermission name="ec:showEarning">
                        <td><input type="text" title="商品规格 ${item.name} 分红" value="${item.bonusEarning}" name="itemList[${itemIndex.index}].bonusEarning" maxlength="13" class="input-mini bonusEarning number"/></td>
                        <td><input type="text" title="商品规格 ${item.name} 一级团队分红" value="${item.firstBonusEarning}" name="itemList[${itemIndex.index}].firstBonusEarning" maxlength="13" class="input-mini firstBonusEarning number"/></td>
                        <td><input type="text" title="商品规格 ${item.name} 二级团队分红" value="${item.secondBonusEarning}" name="itemList[${itemIndex.index}].secondBonusEarning" maxlength="13" class="input-mini secondBonusEarning number"/></td>
                        <td><input type="text" title="商品规格 ${item.name} 扣点金额" value="${item.point}" name="itemList[${itemIndex.index}].point" maxlength="13" class="input-mini point number"/></td>
                        <%--<td><input type="text" title="商品规格 ${item.name} 可用V猫币" value="${item.couponValue}" name="itemList[${itemIndex.index}].couponValue" maxlength="13" class="input-mini couponValue number"/></td>--%>
                    </shiro:hasPermission>
					<td><input type="text" title="商品规格 ${item.name} 库存" value="${item.inventory}" name="itemList[${itemIndex.index}].inventory" maxlength="10" class="input-min inventory digits"/></td>
                    <%--<td><input type="text" title="商品规格 ${item.name} 全额抵扣库存" value="${item.couponAllInventory}" name="itemList[${itemIndex.index}].couponAllInventory" maxlength="10" class="input-min couponAllInventory digits"/></td>--%>
                    <%--<td><input type="text" title="商品规格 ${item.name} 非全额抵扣库存" value="${item.couponPartInventory}" name="itemList[${itemIndex.index}].couponPartInventory" maxlength="10" class="input-min couponPartInventory digits"/></td>--%>
                    <td><input type="text" title="商品规格 ${item.name} 重量" value="${item.weight}" name="itemList[${itemIndex.index}].weight" maxlength="13" class="input-mini weight digits"/></td>
					<td class="<c:if test="${isView}">hide</c:if>">
                        <a href="javascript:void(0)" onclick="editSpec(this,${itemIndex.index})" class="icon-edit"></a>
                        <a href="javascript:void(0)" onclick="refreshItemChangeLogList('${item.id}','${item.name}')" class="icon-time"></a>
                        <a href="${ctx}/ec/product/deleteProductItem?id=${item.id}&productId=${product.id}" onclick="return confirmx('确认删除规格： ${item.name} ？',this.href)" class="icon-trash"></a>
                    </td>
				</tr>
			</c:forEach>
			<tr class="lastSpecTr hide"></tr>
			</tbody>
		</table>
        <div class="<c:if test="${isView}">hide</c:if>">
            <button class="btn" onclick="showSpecDialog()" type="button">添加规格</button>
            <%--<label>库存类型：</label>--%>
            <%--<select id="inventoryType" class="input-small" style="margin-right: 5px;">--%>
                <%--<option value="inventory" selected>基本库存</option>--%>
                <%--<option value="couponAllInventory">全额抵扣库存</option>--%>
                <%--<option value="couponPartInventory">非全额抵扣库存</option>--%>
            <%--</select>--%>
            <label>库存：</label>
            <input type="text" id="allInventory" class="input-min" title="库存" onkeyup="this.value = this.value.replace(/\D/g, '')"/>
            <button class="btn" onclick="if(!isNull($('#allInventory').val())){$('.inventory').each(function () {$(this).val(parseInt(getDefault($(this).val(), 0)) + parseInt(getDefault($('#allInventory').val(), 0)))})}else{$('#allInventory').focus()}" type="button">增加库存</button>
            <button class="btn" onclick="$('.inventory').val('0')" type="button">清空库存</button>
        </div>
	</div>
</div>

<div id="specDialog" class="vcat-dialog" style="width: 670px;">
	<div class="panel panel-info"><!-- 设置商品的规格 -->
		<div class="panel-heading">
			<div class="panel-title" style="font-size: 14px;">
				设置商品的规格
				<a class="icon-remove pull-right" href="javascript:void(0);" onclick="showDialog('specDialog')"></a>
			</div>
		</div>
		<div class="panel-body">
			<div class="specItem">
				<div class="specItemLeft">
					<p class="specItemTitle">请选择规格属性</p>
					<ul class="specItemList">
                        <li class="addSpecLi" style="vertical-align: middle"><i class="icon-plus"></i><span> 添加新规格</span></li>
                    </ul>
				</div>
				<div class="specItemRight">
					<p class="specItemTitle">请勾选规格属性值（可多选）</p>
					<div class="specItemValue">
						<p class="specCheckAllBox">
							<label class="specCheckAll" style="margin-top: 10px;"><input id="specCheckAll" type="checkbox" onclick="checkAllItemValue(this)"><span>全选</span></label>
                            <label class="addSpecValueLabel hide">
                                <input type="hidden" id="specValueId">
                                <input type="text" class="input" id="newSpecValueLabelText" placeholder="新属性名称(多个用&quot; , &quot;分割)">
                                <input type="button" class="btn btn-primary" id="newSpecValueBtn" value="确定">
                            </label>
						</p>
                        <ul class="addSpecUl hide">
                            <li class="specAddInputLi">
                                <input type="text" class="input" id="newSpecTitleText" width="290px" placeholder="请输入新规格名称"/>
                                <input type="text" class="input" id="newSpecValueText" width="290px" placeholder='请输入新规格备选值(多个用","分割)'/>
                                <input type="button" class="btn btn-primary" id="newSpecBtn" value="确定"/>
                            </li>
                        </ul>
					</div>
				</div>
			</div>
			<div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
				<input onclick="saveSpec()" class="btn btn-success" type="button" value="生成所有规格"/>
				<input onclick="showDialog('specDialog')" class="btn btn-default" type="button" value="返回"/>
			</div>
		</div>
	</div>
</div>