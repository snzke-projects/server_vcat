/**
 * 展示物流信息
 * @param com 物流公司编码
 * @param number 物流单号
 * @param cached 是否读取缓存 默认读取
 */
function showExpressInfo(com,number,cached){
    cached = null != cached ? cached : true;
    if(isNull(com) || isNull(number)){
        return;
    }
    if($(".expressRow").length > 0 && cached){
        showDialog('expressDialog');
        return;
    }
    $(".expressRow").remove();
    $.ajax({
        type : 'POST',
        url : ctx+"/ec/order/queryExpress",
        data : {
            com : com,
            number : number
        },
        success : function(noticeRequest){
            try{
                var lastResult = noticeRequest.lastResult;
                if(!isNull(lastResult)){
                    var status = lastResult.status;
                    if("200" == status){
                        var datas = lastResult.data;
                        for(var i in datas){
                            var info = datas[i];
                            var infoTR = "<tr class='expressRow'>";
                            infoTR += "<td>" + info.time + "</td>";
                            infoTR += "<td>" + info.context + "</td>";
                            infoTR += "</tr>";
                            if($(".expressRow :eq(0)").length > 0){
                                $(".expressRow :eq(0)").before(infoTR);
                            }else{
                                $(".expressTH").after(infoTR);
                            }
                        }
                    }else{
                        $("#expressTable").append("<tr class='expressRow'><td colspan='2'>"+lastResult.message+"</td></tr>");
                    }
                }else{
                    $("#expressTable").append("<tr class='expressRow'><td colspan='2'>无最新物流信息</td></tr>");
                }
                showDialog('expressDialog');
            }catch(e){
                console.log("获取物流信息失败："+e.message);
                return doError(e);
            }
        },
        error: function(XMLHttpRequest) {
            return doError(XMLHttpRequest);
        }
    });
}