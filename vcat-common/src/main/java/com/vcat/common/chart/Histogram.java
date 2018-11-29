package com.vcat.common.chart;

import com.vcat.common.utils.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;

/**
 * Highcharts堆叠柱状图实体
 */
public class Histogram {
    private static final String X_KEY = "x";        // x轴的键
    private static final String Y_KEY = "y";        // y轴的键
    private static final String VALUE_KEY = "value";// 值的键
    private static final String NAME_KEY = "name";  // 数据的键
    private static final String DATA_KEY = "data";  // 数据的键
    private List<String> xAxis;                     // X轴数据
    private List<String> yAxis;                     // Y轴分组数据集合
    private List<Map<String, Object>> seriesData;    // 展示数据集合 KEY:y轴名称 VALUE:x轴数据集合

    private Histogram(){}

    /**
     * 通过传入包含x轴，y轴以及值的数据集合来实例化堆叠柱状图数据对象
     * @param dataList
     */
    public Histogram(List<Map<String,Object>> dataList){
        if(null == dataList || dataList.isEmpty()){
            return;
        }

        Set<String> xAxisSet = new HashSet<>();
        Set<String> yAxisSet = new HashSet<>();
        Map<String,Object> valueMap = new HashMap<>();
        this.seriesData = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            Map<String,Object> data = dataList.get(i);
            String x = data.get(X_KEY) + "";
            String y = data.get(Y_KEY) + "";
            Object value = data.get(VALUE_KEY);

            value = null == value || StringUtils.isBlank(value.toString()) ? "0" : value;

            if(StringUtils.isBlank(x) || StringUtils.isBlank(y)){
                continue;
            }

            xAxisSet.add(x);
            yAxisSet.add(y);

            valueMap.put(x + "_" + y, value);
        }

        List<String> xAxisList = new ArrayList<>(xAxisSet);
        List<String> yAxisList = new ArrayList<>(yAxisSet);
        Collections.sort(xAxisList);
        Collections.sort(yAxisList);

        yAxisList.forEach((y) -> {
            Map seriesMap = new HashMap();
            List valueList = new ArrayList<>(yAxisList.size());
            xAxisList.forEach((x) ->
                    valueList.add(null == valueMap.get(x + "_" + y) ? 0 : Double.parseDouble(valueMap.get(x + "_" + y).toString())))
            ;
            seriesMap.put(NAME_KEY, y);
            seriesMap.put(DATA_KEY, valueList);
            seriesData.add(seriesMap);
        });

        this.xAxis = xAxisList;
        this.yAxis = yAxisList;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public List<String> getyAxis() {
        return yAxis;
    }

    public List<Map<String, Object>> getSeriesData() {
        return seriesData;
    }

    public static void main(String[] args) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String,Object> data1 = new HashMap<>();
        Map<String,Object> data2 = new HashMap<>();
        Map<String,Object> data3 = new HashMap<>();
        data1.put(X_KEY, "1月");
        data1.put(Y_KEY, "普通");
        data1.put(VALUE_KEY, "15");
        data2.put(X_KEY, "2月");
        data2.put(Y_KEY, "抵扣");
        data2.put(VALUE_KEY, "22");
        data3.put(X_KEY, "3月");
        data3.put(Y_KEY, "活动");
        data3.put(VALUE_KEY, "37");
        dataList.add(data1);
        dataList.add(data2);
        dataList.add(data3);
        Histogram histogram = new Histogram(dataList);
        System.out.println(ReflectionToStringBuilder.toString(histogram));
    }
}
