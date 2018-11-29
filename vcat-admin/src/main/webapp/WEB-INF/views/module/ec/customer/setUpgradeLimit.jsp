<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设置升级限制</title>
	<meta name="decorator" content="default"/>
    <style type="text/css">
        .imgDiv{
            border:1px solid WHITE;
            transition:border 0.3s;
            -moz-transition:border 0.3s; /* Firefox 4 */
            -webkit-transition:border 0.3s; /* Safari and Chrome */
            -o-transition:border 0.3s; /* Opera */
        }

        .imgDiv:hover{
            border:1px solid ORANGE;
        }

        .imgSelected{
            border:1px solid ORANGE;
        }
    </style>
	<script type="text/javascript">
        $(function (){
            var currentUpgradeLimit = "${currentUpgradeLimit}";
            currentUpgradeLimit = currentUpgradeLimit && parseInt(currentUpgradeLimit) > 0 ? parseInt(currentUpgradeLimit) : 0;
            var levelList = ${fns:toJson(levelList)};
            var limitImgUrlPath = "";
            if(levelList && levelList.length > 0){
                for(var i = 0; i < levelList.length; i++){
                    var level = levelList[i];
                    if(level.maxExp > currentUpgradeLimit && currentUpgradeLimit >= level.minExp){
                        limitImgUrlPath = level.urlPath;
                        $('#' + level.id).addClass("imgSelected");
                        break;
                    }
                }
            }
            if(limitImgUrlPath != ""){
                $('#limitImg').attr("src",limitImgUrlPath);
            }
            $('.imgDiv').each(function (){
                $(this).bind("click",function (){
                    $('.imgDiv').removeClass("imgSelected");
                    $(this).addClass("imgSelected");
                    $("#levelId").val($(this).attr("id"));
                });
            });
        });
	</script>
</head>
<body>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">设置升级限制</div>
        </div>
        <div class="panel-body">
            <form id="inputForm" action="${ctx}/ec/customer/setUpgradeLimit" method="post" class="form-horizontal">
                <div class="control-group">
                    <label class="control-label">当前钻石小店等级限制：</label>
                    <div class="controls">
                        <img id="limitImg" src="" alt="等级" title="当前钻石小店等级限制">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">新钻石小店等级限制：</label>
                    <div class="controls">
                        <c:forEach items="${levelList}" var="level">
                            <img class="imgDiv" id="${level.id}" src="${level.urlPath}" alt="${level.name}" title="${level.name}">
                        </c:forEach>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="hidden" id="levelId" name="id"/>
                    <shiro:hasPermission name="ec:customer:setUpgradeLimit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/></shiro:hasPermission>
                </div>
            </form>
        </div>
    </div>
</body>
</html>