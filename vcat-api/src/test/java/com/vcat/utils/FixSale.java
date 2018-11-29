package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Code.Ai on 16/4/29.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class FixSale {
    @Autowired
    private SqlMapper sqlMapper;
    @Test
    public void test() throws BiffException {
        List<String> orderNumbers = getOrderNumbers();
        BigDecimal mon = new BigDecimal(0);
        for(String number : orderNumbers){
            BigDecimal next = sqlMapper.getOrderItemDto(number);
            if(next != null){
                mon = mon.add(next);
            }
        }
        System.out.println(mon.toBigInteger());
    }


    private List<String> getOrderNumbers() throws BiffException {
        //创建一个list 用来存储读取的内容
        List<String> list = new ArrayList<>();
        Workbook     rwb  = null;
        Cell         cell = null;
        //创建输入流
        InputStream stream = null;
        try {
            stream = new FileInputStream("/Users/codeai/Downloads/销售分红.xls");
            //获取Excel文件对象
            rwb = Workbook.getWorkbook(stream);
            //获取文件的指定工作表 默认的第一个
            Sheet sheet = rwb.getSheet(0);
            //行数(表头的目录不需要，从1开始)
            String str = "";
            for(int i=0; i<sheet.getRows(); i++){
                cell = sheet.getCell(0,i);
                str = cell.getContents();
                list.add(str);
            }
            list.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}


@MyBatisDao
interface SqlMapper {
    @Select("SELECT  sum(eoi.quantity*eoi.bonus_earning)\n" +
            "FROM ec_order_item AS eoi\n" +
            "left join ec_order as eo on eoi.order_id = eo.id\n" +
            "where eo.order_number = #{number}")
    BigDecimal getOrderItemDto(@Param("number")String number);
}