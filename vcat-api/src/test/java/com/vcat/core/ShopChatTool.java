package com.vcat.core;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vcat.ApiApplication;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.InviteCodeDao;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.InviteCode;
import com.vcat.module.ec.entity.Shop;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class ShopChatTool extends AbstractJUnit4SpringContextTests {
    @Autowired
    private CustomerDao   customerDao;
    @Autowired
    private ShopDao       shopDao;
    @Autowired
    private InviteCodeDao inviteCodeDao;

    private static final String FAIL_TOVIP = "failListOfUpgradeToVIP";
    private static final String FAIL_CHANGE = "failListOfChangeRelationShipList";
    private static final String BASE_PATH = "E:\\data\\";

    private static final JsonNodeFactory factory = new JsonNodeFactory(false);
    private static final Logger         LOGGER         = LoggerFactory.getLogger(ShopChatTool.class);

    // 根据电话号码获取关系
    Map<String,Object> relation = new HashMap<>();
    @Test
    public void RefactorRelationShip() throws BiffException, InterruptedException {
        // 获取 excel 中的所有 vip 电话
        List<String> superUserPhones = readExcel("aaa.xls");
        // 第一步:根据提供的电话列表更新店铺为VIP (设置邀请码ID, advanced_shop设置为1, parent_id设置为 null)
        System.out.println("开始第一步");
        upToVIPByPhone(superUserPhones);
        System.out.println("第一步结束");

        // 第二步:根据单个电话获取所有下家,并为下家添加邀请码
        System.out.println("开始第二步");
        addInvitation(superUserPhones);
        System.out.println("第二步结束");

        // 第三步:修改数据库中其他店铺的上下级关系,修改环信关系
        String bossPhoneNumber = "13540333000";
        Customer boos = new Customer();
        boos.setPhoneNumber(bossPhoneNumber);
        boos = customerDao.get(boos);
        // 获得所有不是 vip 和 不是 B级 用户 的店铺号
        List<Shop> otherShops = shopDao.getOtherShop();
        // 删除原来的环信关系,修改此店铺的上级店铺为 小黑, 添加使用的邀请码,新建环信关系
        changeRelationShip(otherShops,boos.getShop());
    }

    /**
     * 根据提供的电话列表更新店铺为VIP
     * @return
     * @throws IOException
     * @throws BiffException
     */
    private void upToVIPByPhone(List<String> superUserPhones) throws BiffException, InterruptedException {
        List<String> failList = new ArrayList<>();
        for(String phone : superUserPhones){
            //创建邀请码
            InviteCode inviteCode = new InviteCode();
            inviteCode.preInsert();
            inviteCode.setCode(generateInviteCode(6));
            inviteCode.setStatus(1);
            inviteCodeDao.insert(inviteCode);
            //修改VIP 用户资料 (设置邀请码ID, advanced_shop设置为1, parent_id设置为 null, 删除与上级的环信关系)
            Customer VIPCustomer = new Customer();
            VIPCustomer.setPhoneNumber(phone);
            VIPCustomer = customerDao.get(VIPCustomer);
            Shop VIPShop = new Shop();
            VIPShop.setCustomer(VIPCustomer);
            VIPShop.setMyInviteCode(inviteCode);

//            解除朋友关系 因为是全新的环信库，所以不用解除
//            ObjectNode response = ResponseUtils.convertResopnse(EasemobIMUsers.deleteFriendSingle(VIPCustomer.getId(), VIPCustomer.getShop().getParentId()));
//            if(ResponseUtils.isStatusSuccess(response)){
//                shopDao.updateToVIP(VIPShop);
//            } else{
//                System.out.println("解除VIP上级关系失败");
//                failList.add("解除VIP上级关系失败"+VIPCustomer.getId());
//            }
//            Thread.sleep(1000);
             shopDao.updateToVIP(VIPShop);
//            ObjectNode datanode = JsonNodeFactory.instance.objectNode();
//            datanode.put("username", VIPCustomer.getId());
//            datanode.put("password", Constants.DEFAULT_PASSWORD);
//            ObjectNode createNewIMUserSingleNode = ResponseUtils.convertResopnse(EasemobIMUsers.createNewIMUserSingle(datanode));
//            if (ResponseUtils.isStatusSuccess(createNewIMUserSingleNode)) {
//                System.out.println("创建环信账号成功");
//            } else{
//                System.out.println("创建环信账号失败");
//                failList.add("创建环信账号失败" + VIPCustomer.getId());
//            }
//            Thread.sleep(1000);

        }
        saveToFile("解除VIP上级关系.txt",failList);
    }

    /**
     * 更新 vip 下家资料
     * 已经是好友关系 环信关系不用改
     * 根据单个电话获取所有下家,并为下家添加 使用的邀请码ID
     * @param superUserPhones
     */
    private void addInvitation(List<String> superUserPhones) throws InterruptedException {
        List<String> failList = new ArrayList<>();
        for(String phone : superUserPhones){
            Customer superCustomer = new Customer();
            superCustomer.setPhoneNumber(phone);
            superCustomer = customerDao.get(superCustomer);
            // 获取所有下家ID
            List<String> friends = shopDao.getFriendsById(superCustomer.getId());
            if (friends != null) {
                for (String friendId : friends) {
                    System.out.println(friendId);
//                    ObjectNode datanode = JsonNodeFactory.instance.objectNode();
//                    datanode.put("username", friendId);
//                    datanode.put("password", Constants.DEFAULT_PASSWORD);
//                    ObjectNode createNewIMUserSingleNode = ResponseUtils.convertResopnse(EasemobIMUsers.createNewIMUserSingle(datanode));
//                    if (ResponseUtils.isStatusSuccess(createNewIMUserSingleNode)) {
//                        System.out.println("创建环信账号成功");
//                    } else{
//                        System.out.println("创建环信账号失败");
//                        failList.add("创建环信账号失败" + friendId);
//                    }
                    // 添加好友关系
//                    ObjectNode add = ResponseUtils.convertResopnse(EasemobIMUsers.addFriendSingle(friendId,superCustomer.getId()));
//                    if(ResponseUtils.isStatusSuccess(add)){
                        shopDao.addInvitation(superCustomer.getId(), friendId);
//                        System.out.println("添加好友成功");
//                    }else {
//                        System.out.println("添加好友失败");
//                        failList.add("添加好友失败" +friendId);
//                    }
//                    Thread.sleep(1000);
                }
            }
        }
        saveToFile("更新 vip 下家资料.txt",failList);
    }

    /**
     * 删除原来的好友关系 然后和小黑哥发生关系
     * @param otherShops
     * @param bossShop
     * @return
     */
    private void  changeRelationShip(List<Shop> otherShops,Shop bossShop) throws InterruptedException {
        List<String> failList = new ArrayList<>();
        System.out.println("开始第三步");
        for(Shop otherShop : otherShops){
            // 查询此用户是否注册过环信
//            ObjectNode getIMUsersByUserNameNode = ResponseUtils.convertResopnse(EasemobIMUsers.getIMUsersByUserName(otherShop.getId()));
//            if (null != getIMUsersByUserNameNode) {
//                System.out.println("获取IM用户[主键查询]: " + getIMUsersByUserNameNode.toString());
//            }
//
//            if(ResponseUtils.isStatusSuccess(getIMUsersByUserNameNode)){ // 如果此用户有环信账号
//                System.out.println(otherShop.getId()+"有环信账号");
//                ObjectNode del = ResponseUtils.convertResopnse(EasemobIMUsers.deleteFriendSingle(otherShop.getId(), otherShop.getParentId()));
//                 if(ResponseUtils.isStatusSuccess(del)){
//                    System.out.println("解除关系成功");
//                } else{
//                    System.out.println("删除关系失败");
//                    failList.add("删除关系失败" + otherShop.getId());
//                }
//            }
//            else{
                //System.out.println(otherShop.getId()+"没有环信账号");
                //如果没有环信账号 创建环信账号
//                ObjectNode datanode = JsonNodeFactory.instance.objectNode();
//                datanode.put("username", otherShop.getId());
//                datanode.put("password", Constants.DEFAULT_PASSWORD);
//                ObjectNode createNewIMUserSingleNode = ResponseUtils.convertResopnse(EasemobIMUsers.createNewIMUserSingle(datanode));
//                if (ResponseUtils.isStatusSuccess(createNewIMUserSingleNode)) {
//                    System.out.println("创建环信账号成功");
//                } else{
//                    System.out.println("创建环信账号失败");
//                    failList.add("创建环信账号失败" + otherShop.getId());
//                }
//            }
            // 添加好友关系
//            ObjectNode add = ResponseUtils.convertResopnse(EasemobIMUsers.addFriendSingle(otherShop.getId(),bossShop.getId()));
//            if(ResponseUtils.isStatusSuccess(add)){
                // 修改 otherShop的上家店铺ID 和 使用的 邀请码
                otherShop.setUsedInviteCodeId(bossShop.getMyInviteCodeId());
                otherShop.setParentId(bossShop.getId());
                shopDao.updateParentId(otherShop);
//                System.out.println("添加好友成功");
//            }else {
//                System.out.println("添加好友失败");
//                failList.add("添加好友失败" + otherShop.getId());
//            }
//            Thread.sleep(1000);
        }
        System.out.println("第三步结束");
        saveToFile("第三步.txt",failList);
    }

    // 将所有失败的结果保存到文件中,以便再次执行
    private List<String> saveToFile(String type,List<String> list) {
        Iterator       iterator = list.iterator();
        File           file     = new File(BASE_PATH + type);
        FileWriter     fw       = null;
        BufferedWriter writer   = null;
        try {
            fw = new FileWriter(file,true);
            writer = new BufferedWriter(fw);
            while(iterator.hasNext()){
                writer.write(iterator.next().toString());
                writer.newLine();
            }
            writer.flush();
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> readExcel(String fileName) throws BiffException{
        //创建一个list 用来存储读取的内容
        List<String> list = new ArrayList<>();
        Workbook     rwb  = null;
        Cell         cell = null;
        //创建输入流
        InputStream stream = null;
        try {
            stream = new FileInputStream(BASE_PATH+fileName);
            //获取Excel文件对象
            rwb = Workbook.getWorkbook(stream);
            //获取文件的指定工作表 默认的第一个
            Sheet sheet = rwb.getSheet(0);
            //行数(表头的目录不需要，从1开始)
            String str = "";
            for(int i=2; i<sheet.getRows(); i++){
                cell = sheet.getCell(3,i);
                str = cell.getContents();
                list.add(str);
            }
            list.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * java生成随机数字和字母组合
     * @param length [生成随机数的长度]
     * @return
     */
    private String generateInviteCode(int length){
        String invaiteCode = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                invaiteCode += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                invaiteCode += String.valueOf(random.nextInt(10));
            }
        }
        System.out.println(invaiteCode);
        return invaiteCode;
    }
}
