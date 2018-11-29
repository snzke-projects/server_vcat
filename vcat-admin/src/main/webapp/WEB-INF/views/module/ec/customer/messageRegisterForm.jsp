<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推送应用消息</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(function (){
            var interval = '${messageRegister.interval}';
            var id = '${messageRegister.article.id}';
            var title = '${messageRegister.article.title}';
            if(!isNull(interval)){
                formatInterval($('#interval')[0]);
            }
            if(!isNull(id) && !isNull(title)){
                var articleArray = [];
                articleArray.push(id);
                articleArray.push(title);
                articleSelect.push(articleArray);
                articleSelectRefresh();
            }
        });
		function toSubmit(){
			var msg = $('#articleId').val();
			if(isNull(msg)){
				alertx("请选择一篇注册消息文章！",function (){
					$('#relationButton').focus();
				});
				return;
			}
            if(parseInt($('#interval').val()) == 0){
                alertx("发送时间必须大于0！",function (){
                    $('#interval').focus();
                });
                return;
            }
            $('#inputForm').submit();
		}
        function formatInterval(input){
            var interval = input.value;
            interval = parseInt(getDefault(interval.replace(/[^\d]*/g,''),0));

            input.value = interval;

            var yearShow = "";
            var year = 525600;
            if(interval >= year){  // 年
                yearShow += parseInt(interval / year) + "年";
                interval = interval % year;
            }

            var dayShow = "";
            var day = 1440;
            if(interval >= day){ // 天
                dayShow += parseInt(interval / day) + "天";
                interval = interval % day;
            }

            var hourShow = "";
            var hour = 60;
            if(interval >= hour){      // 小时
                hourShow += parseInt(interval / hour) + "小时";
                interval = interval % hour;
            }

            var minShow = "";
            if(interval > 0){
                minShow += interval + "分钟";
            }

            $('#intervalSpan').html("准确发送时间：注册后" + yearShow + dayShow + hourShow + minShow);
        }
	</script>
</head>
<body>
	<sys:message content="${message}"/>
    <div class="panel panel-info">
        <div class="panel-heading">
            <div class="panel-title" style="font-size: 14px;">
                设置注册消息
            </div>
        </div>
        <div class="panel-body">
            <form:form id="inputForm" action="${ctx}/ec/customer/saveMessageRegister" method="post" class="form-horizontal">
            <div class="control-group">
                <label class="control-label">系统消息文章：</label>
                <div class="controls">
                    <input type="hidden" id="articleId" name="article.id"/>
                    <ol id="articleSelectList" style="margin: 0 auto;"></ol>
                    <a id="relationButton" href="javascript:" class="btn">选择系统消息</a>
                    <script type="text/javascript">
                        var articleSelect = [];
                        function articleSelectAddOrDel(id,title){
                            var isExtents = false, index = 0;
                            for (var i=0; i<articleSelect.length; i++){
                                if (articleSelect[i][0]==id){
                                    isExtents = true;
                                    index = i;
                                }
                            }
                            if(isExtents){
                                articleSelect.splice(index,1);
                            }else if(articleSelect.length > 0){
                                return function (){
                                    this.checked = false;
                                    top.$.jBox.tip('只能选择一篇文章','warning');
                                };
                            }else{
                                articleSelect.push([id,title]);
                            }
                            articleSelectRefresh();
                        }
                        function articleSelectRefresh(){
                            $("#articleSelectList").children().remove();
                            for (var i=0; i<articleSelect.length; i++){
                                $("#articleSelectList").append("<li>"+articleSelect[i][1]+"&nbsp;&nbsp;<a href=\"javascript:\" onclick=\"articleSelectAddOrDel('"+articleSelect[i][0]+"','"+articleSelect[i][1]+"');\">×</a></li>");
                            }
                            if(null != articleSelect && articleSelect.length > 0){
                                $('#articleId').val(articleSelect[0][0]);
                            }
                        }
                        $("#relationButton").click(function(){
                            var systemCategoryId = '${fns:getDictSingleValue('ec_system_article_category_id', '1')}';
                            if(!isNull($('#messageRegisterId').val())){
                                articleSelect = [];
                                $("#articleSelectList").children().remove();
                            }
                            top.$.jBox.open("iframe:${ctx}/cms/article/selectList?pageSize=10&category.id="+systemCategoryId+"&lockCategory=1", "选择文章",$(top.document).width()-220,$(top.document).height()-180,{
                                buttons:{"确定":true}, loaded:function(h){
                                    $(".jbox-content", top.document).css("overflow-y","hidden");
                                }
                            });
                        });
                    </script>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">发送时间（分钟）：</label>
                <div class="controls">
                    <input type="text" id="interval" name="interval" class="required digits input-small" onkeyup="formatInterval(this)" value="${messageRegister.interval}"/>
                    <span id="intervalSpan" class="help-inline"></span>
                </div>
            </div>
            <div class="form-actions" style="margin-top: 0px;margin-bottom: 0px;">
                <input type="hidden" id="messageRegisterId" name="id" value="${messageRegister.id}"/>
                <input onclick="toSubmit()" class="btn btn-success" type="button" value="保 存"/>
            </div>
            </form:form>
        </div>
    </div>
</body>
</html>