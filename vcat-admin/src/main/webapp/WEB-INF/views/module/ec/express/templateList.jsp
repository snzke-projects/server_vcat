<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>邮费模板列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function setDefault(id,name){
            confirmx("确认将"+name+"设置为默认邮费模板？",function(){
                $.ajax({
                    type : 'POST',
                    url : "${ctx}/ec/express/setExpressTemplateDefault",
                    data : {id : id},
                    success : function(customer){
                        try{
                            showTip("设置"+name+"为默认邮费模板成功！");
                            location.href = location.href;
                        }catch(e){
                            console.log("设置默认邮费模板失败："+e.message);
                            return doError(e);
                        }
                    },
                    error: function(XMLHttpRequest) {
                        return doError(XMLHttpRequest);
                    }
                });
            });
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/ec/express/expressTemplateList">邮费模板列表</a></li>
    <shiro:hasPermission name="ec:expressTemplate:edit"><li><a href="${ctx}/ec/express/expressTemplateForm">邮费模板添加</a></li></shiro:hasPermission>
</ul>
<sys:message content="${message}"/>
<table id="treeTable" class="table table-bordered table-condensed table-hover">
    <tr>
        <th width="200px">模板名称</th>
        <th width="100px">发货人名称</th>
        <th width="100px">发货人电话</th>
        <th>发货地址</th>
        <th width="60px">计价方式</th>
        <th width="30px">默认</th>
        <th width="100px">操作</th>
    </tr>
    <c:forEach items="${list}" var="entity">
        <tr>
            <td><a href="${ctx}/ec/express/expressTemplateForm?id=${entity.id}"><span title="${entity.name}">${fns:abbr(entity.name,32)}</span></a></td>
            <td><span title="${entity.addresserName}">${fns:abbr(entity.addresserName,16)}</span></td>
            <td><span title="${entity.addresserPhone}">${fns:abbr(entity.addresserPhone,16)}</span></td>
            <c:set var="address" value="${entity.provinceTitle}&nbsp;${entity.cityTitle}&nbsp;${entity.districtTitle}&nbsp;${entity.detailAddress}"></c:set>
            <td><span title="${address}">${fns:abbr(address,80)}</span></td>
            <td>${fns:getDictLabel(entity.valuationMethod,'ec_valuation_method','未知')}</td>
            <td><c:if test="${entity.isDefault}"><i class="icon-ok"></i></c:if></td>
            <shiro:hasPermission name="ec:expressTemplate:edit">
                <td>
                    <a href="${ctx}/ec/express/expressTemplateForm?id=${entity.id}">修改</a>
                    <c:if test="${!entity.isDefault}">
                        <a href="javascript:void(0)" onclick="setDefault('${entity.id}','${entity.name}')">设为默认</a>
                    </c:if>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
</table>
</body>
</html>