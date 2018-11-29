<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>KPI</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">include('highcharts','${ctxStatic}/highcharts/',['highcharts.js']);</script>
    <script type="text/javascript">include('highcharts_export','${ctxStatic}/highcharts/',['exporting.js']);</script>
    <script type="text/javascript">include('highcharts_3d','${ctxStatic}/highcharts/',['highcharts-3d.js']);</script>
    <script type="text/javascript">
        var columnCharts;
        var columnChartsOpt;
        $(function (){
            columnChartsOpt = {
                chart: {
                    renderTo: 'columnChartDiv',
                    type: 'area'
                },
                credits: {enabled: false},
                exporting: {
                    buttons: {
                        contextButton: {
                            menuItems: [{
                                text: '打印',
                                onclick: function () {
                                    this.print();
                                }
                            }]
                        }
                    }
                },
                xAxis: {
                    categories : null,
                    tickmarkPlacement: 'on',
                    title: {
                        enabled: false
                    }
                },
                yAxis: {
                    stackLabels: {
                        enabled: true,
                        style: {
                            fontWeight: 'bold',
                            color: 'gray'
                        }
                    }
                },
                tooltip: {
                    shared: true,
                    valueSuffix: ' 元'
                },
                plotOptions: {
                    area: {
                        stacking: 'normal',
                        lineColor: '#666666',
                        lineWidth: 1,
                        marker: {
                            lineWidth: 1,
                            lineColor: '#666666'
                        }
                    }
                },
                series:null
            };
        })
        function reQuery(){
            if(isNull($('#content').val())){
                alertx("请选择统计内容",function (){
                    $('#content').select2('open');
                });
                return;
            }
            if(isNull($('#dimensions').val())){
                alertx("请选择统计维度",function (){
                    $('#dimensions').select2('open');
                });
                return;
            }
            if(isNull($('#st').val()) && isNull($('#et').val())){
                alertx("请至少输入一个统计时间",function (){
                    if(isNull($('#st').val())){
                        $('#st').focus();
                    }else if(isNull($('#et').val())){
                        $('#et').focus();
                    }
                });
                return;
            }
            $.get(ctx + "/ec/statistics/kpiData?" + $('#searchForm').serialize(),function(histogram){
                var title = {
                    text : $('#content :selected').html() + $('#dimensions :selected').html() + '度统计图'
                };
                var yTitle = {
                    text : '订单类型' + $('#content :selected').html()
                };

                columnChartsOpt.title = title;
                columnChartsOpt.xAxis.categories = histogram.xAxis;
                columnChartsOpt.yAxis.title = yTitle;
                columnChartsOpt.series = histogram.seriesData;
                columnCharts = new Highcharts.Chart(columnChartsOpt);
            });
        }


        function changeTimeSelect(dimensions){
            $("#st").val("");
            $("#et").val("");
            if("year" === dimensions){
                $("#st").unbind("click");
                $("#et").unbind("click");
                $("#st").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-01-01",maxDate:"%y"});
                });
                $("#et").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-01-01",maxDate:"%y"});
                });
            }else if("month" === dimensions){
                $("#st").unbind("click");
                $("#et").unbind("click");
                $("#st").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-MM-01",maxDate:"%y-%M"});
                });
                $("#et").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-MM-01",maxDate:"%y-%M"});
                });
            }else if("day" === dimensions){
                $("#st").unbind("click");
                $("#et").unbind("click");
                $("#st").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-MM-dd",maxDate:"%y-%M-%d"});
                });
                $("#et").bind("click",function (){
                    WdatePicker({dateFmt:"yyyy-MM-dd",maxDate:"%y-%M-%d"});
                });
            }
        }
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="supplier" action="${ctx}/ec/statistics/kpiData" method="get" class="breadcrumb form-search">
    <label>统计内容：</label>
    <form:select id="content" path="sqlMap['content']" cssClass="input-small">
        <form:option value="">请选择</form:option>
        <form:option value="sales">订单销售额</form:option>
        <form:option value="refund">订单退款金额</form:option>
        <form:option value="earning">产生利润</form:option>
        <form:option value="point">公司扣点</form:option>
    </form:select>
    <label>统计维度：</label>
    <form:select id="dimensions" path="sqlMap['dimensions']" cssClass="input-small" onchange="changeTimeSelect(this.value)">
        <form:option value="">请选择</form:option>
        <form:option value="year">年</form:option>
        <form:option value="month">月</form:option>
        <form:option value="day">日</form:option>
    </form:select>
    <label>开始时间：</label>
    <form:input id="st" path="sqlMap['st']" cssClass="Wdate" cssStyle="width: 90px" readonly="true"></form:input>
    <label>结束时间：</label>
    <form:input id="et" path="sqlMap['et']" cssClass="Wdate" cssStyle="width: 90px" readonly="true"></form:input>
    <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="reQuery()"/>
</form:form>
<sys:message content="${message}"/>
<div id="columnChartDiv" style="border: #ccc solid 1px"></div>
</body>
</html>