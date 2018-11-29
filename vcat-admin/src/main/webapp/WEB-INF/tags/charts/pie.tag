<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="唯一标识"%>
<%@ attribute name="title" type="java.lang.String" description="标题"%>
<%@ attribute name="unit" type="java.lang.String" description="显示单位"%>
<%@ attribute name="dataCallBack" type="java.lang.String" description="图表数据回调函数"%>

<script type="text/javascript">include('highcharts','${ctxStatic}/highcharts/',['highcharts.js']);</script>
<script type="text/javascript">include('highcharts_export','${ctxStatic}/highcharts/',['exporting.js']);</script>
<script type="text/javascript">include('highcharts_3d','${ctxStatic}/highcharts/',['highcharts-3d.js']);</script>

<script type="text/javascript">
    var ${id};
    var dataCallBack = eval('${dataCallBack}');
    var ${id}Unit = "${unit}";
    $(function () {
        ${id} = new Highcharts.Chart({
            chart: {
                renderTo: '${id}',
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 60,
                    beta: 0
                }
            },
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
            },  // 禁用导出工具栏
            credits: {enabled: false},  // 禁用官网超链接
            title: {text: '${title}'},
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 1) +'% ('+
                            Highcharts.numberFormat(this.y, 0, ',')+' '+${id}Unit+')';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    depth: 20,
                    dataLabels: {
                        enabled: true,
                        format: '{point.name}',
                        formatter: function() {
                            if (this.percentage > 4) return this.point.name;
                        }
                    },
                    showInLegend: true
                }
            }
        });

        flushPie('${title}',getPieData());
    });
    function getPieData(){
        if(isFunction(dataCallBack)){
            var data = dataCallBack.call(this);
            if(isArray(data)){
                return data;
            }
        }
        return [{name:"暂无数据",y:1}];
    }
    function flushPie(title,data){
        title = {text:title};
        ${id}.setTitle(title);
        ${id}Series = {type:'pie'};
        ${id}Series.name = title;
        ${id}Series.data = data;
        removeSeries(${id});
        ${id}.addSeries(${id}Series);
    }
    function removeSeries(chart){
        var seriesList = chart.series; //获得图表的所有序列
        if(isNull(seriesList)){
            return;
        }
        var seriesListLength = seriesList.length;
        if(isNull(seriesListLength)){
            return;
        }

        //通过for循环删除序列数据
        for(var i = 0;i<seriesListLength;i++){
            chart.series[0].remove();
        }
    }
</script>

<div id="${id}"></div>
