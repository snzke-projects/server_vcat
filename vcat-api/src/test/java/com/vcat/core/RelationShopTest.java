package com.vcat.core;

import com.vcat.ApiApplication;
import com.vcat.module.ec.dao.ShopDao;
import com.vcat.module.ec.entity.Shop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class RelationShopTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    public ShopDao shopDao;
    /**
     * 根据给定的名单重新获取环信关系,重构数据库用户关系
     *
     */
    @Test
    public void changeRelationShip() throws InterruptedException{
        Shop shop1 = new Shop();
        shop1.setId("0002964cf39942ddb9d7f3a352ec50ab");
        shop1 = shopDao.get(shop1);
        Shop shop2 = new Shop();
        shop2.setId("00078aa36c7e4890bbf601b1df14f618");
        shop2 = shopDao.get(shop2);
        //从文件中将给出的店铺信息转换为 List<Shop>
        List<Shop> shopList = new ArrayList<>();
        shopList.add(shop1);
        shopList.add(shop2);
        for(Shop rootShop : shopList){
            //Iterator<Shop> childIterator = shopDao.getAllShopByParentId(rootShop.getParentId()).iterator();
            ////List<Shop> childList = shopDao.getAllShopByParentId(rootShop.getParentId());
            //while(childIterator.hasNext()){
            //    Shop childShop = childIterator.next();
            //    if(childShop.getId().equals(rootShop.getId())){ //如果给出的列表中的某个店铺的下家正好在给出的店铺名单中时
            //        childIterator.remove();
            //    }
            //}
            RelationShopNode root = new RelationShopNode();
            root.setShop(rootShop);
            root.creatRelationTree(root);
            root.traverse();
            System.out.println("A级店铺:" + rootShop);
        }
    }
    class RelationShopNode {
        // 节点数据对象
        private Shop                   shop;
        // 子节点列表
        private List<RelationShopNode> childList;
        // 父节点
        private RelationShopNode       parentNode;

        public RelationShopNode() {
            initChildList();
        }

        public RelationShopNode(RelationShopNode parentNode) {
            this.getParentNode();
            initChildList();
        }

        public boolean isLeaf() {
            if (childList == null) {
                return true;
            } else {
                if (childList.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        /**
         * 根据父节点中的Shop中的 parentId 动态生成一个关系树
         * @param root 传入父节点
         * @return 返回这棵树的父节点
         */
        public RelationShopNode creatRelationTree(RelationShopNode root){
            //父节点ID
            String rootShopId = root.getShop().getId();
            //根据父节点ID 查找所有子节点对象
            List<Shop> childShopList = shopDao.getAllShopByParentId(rootShopId);
            for(Shop childNode : childShopList){
                RelationShopNode newNode = new RelationShopNode();
                newNode.setShop(childNode);
                //添加到子节点中
                root.addChildNode(newNode);
            }
            return root;
        }

        /**
         * 递归插入一个子节点到当前节点中
         * @param newNode
         */
        public void addChildNode(RelationShopNode newNode) {
            //获取新节点的子节点列表
            List<Shop> childShopList = shopDao.getAllShopByParentId(newNode.getShop().getParentId());
            if(childShopList == null){
                // 如果子节点没有孩子节点了
                this.childList.add(newNode);
            }else{
                for(Shop childNode : childShopList) {
                    RelationShopNode newChildNode = new RelationShopNode();
                    newChildNode.setShop(childNode);
                    //添加到子节点中
                    newNode.addChildNode(newChildNode);
                }
            }
            //this.childList.add(newNode);
        }

        public void initChildList() {
            if (childList == null)
                childList = new ArrayList<RelationShopNode>();
        }

        /* 返回当前节点的父辈节点集合 */
        public List<RelationShopNode> getElders() {
            List<RelationShopNode> elderList  = new ArrayList<RelationShopNode>();
            RelationShopNode       parentNode = this.getParentNode();
            if (parentNode == null) {
                return elderList;
            } else {
                elderList.add(parentNode);
                elderList.addAll(parentNode.getElders());
                return elderList;
            }
        }

        /* 返回当前节点的晚辈集合 */
        public List<RelationShopNode> getJuniors() {
            List<RelationShopNode> juniorList = new ArrayList<RelationShopNode>();
            List<RelationShopNode> childList  = this.getChildList();
            if (childList == null) {
                return juniorList;
            } else {
                for (RelationShopNode junior : childList) {
                    juniorList.add(junior);
                    juniorList.addAll(junior.getJuniors());
                }
                return juniorList;
            }
        }

        /* 返回当前节点的孩子集合 */
        public List<RelationShopNode> getChildList() {
            return childList;
        }

        /* 删除节点和它下面的晚辈 */
        public void deleteNode() {
            RelationShopNode parentNode = this.getParentNode();
            if (parentNode != null) {
                parentNode.deleteChildNode(this);
            }
        }

        /* 删除当前节点的某个子节点 */
        public void deleteChildNode(RelationShopNode childNode) {
            List<RelationShopNode> childList   = this.getChildList();
            int                    childNumber = childList.size();
            RelationShopNode       child       = null;
            for (int i = 0; i < childNumber; i++) {
                child = childList.get(i);
                if (child.getShop().getId().equals(childNode.getShop().getId())) {
                    childList.remove(i);
                    return;
                }
            }
        }



        //private void AddNodeChildren(RelationShopNode node, List<RelationShopNode> childrenNodeList) {
        //    if (node != null && childrenNodeList != null) {
        //        RelationShopNode rootNode    = node;
        //        boolean         isHaveChild = false;
        //        for (int i = 0; i < childrenNodeList.size(); ) {
        //            if (childrenNodeList.get(i).getParentNode().getShop().getId() == rootNode.getShop().getId()) {
        //                RelationShopNode childNode = new RelationShopNode();
        //                childNode = childrenNodeList.get(i);
        //                // 只有数据不为空的项目节点才有右键菜单
        //
        //                node.addChildNode(childNode);
        //                isHaveChild = true;
        //                childrenNodeList.remove(i);
        //                if (childrenNodeList.size() > 0) {
        //                    this.AddNodeChildren(childNode, childrenNodeList);
        //                    i = 0;
        //                }
        //            } else {
        //                i++;
        //            }
        //        }
        //        if (!isHaveChild) {
        //            return;
        //        }
        //    }
        //}


        /* 找到一颗树中某个节点 */
        public RelationShopNode findTreeNodeById(Shop shop) {
            if (this.getShop().getId().equals(shop.getId()))
                return this;
            if (this.childList.isEmpty() || this.childList == null) return null;
            else {
                for (RelationShopNode child : this.childList) {
                    RelationShopNode resultNode = child.findTreeNodeById(shop);
                    if (resultNode != null) {
                        return resultNode;
                    }
                }
                return null;
            }
        }


        /* 动态的插入一个新的节点到当前树中 */
        public void insertJuniorNode(RelationShopNode newNode,List<RelationShopNode> childNodeList) {
            // 如果新节点的上级Id和根节点的店铺ID 一样,说明是这个根节点的子节点
            if (this.shop.getId().equals(newNode.getShop().getParentId())) {
                this.addChildNode(newNode);
                // 如果根节点是新节点的子节点
            }else if (this.shop.getParentId().equals(newNode.getShop().getId())){
                newNode.addChildNode(this);
            }
        }

        /* 遍历一棵树，层次遍历 */
        public void traverse() {
            if (this.childList == null || this.childList.isEmpty()){
                System.out.println("B级店铺:"+this.shop);
                //修改店铺之间的关系
                return;
            }
            this.childList.forEach(RelationShopNode::traverse);
        }

        public void print(String content) {
            System.out.println(content);
        }

        public void print(int content) {
            System.out.println(String.valueOf(content));
        }

        public void setChildList(List<RelationShopNode> childList) {
            this.childList = childList;
        }

        public RelationShopNode getParentNode() {
            return parentNode;
        }

        public void setParentNode(RelationShopNode parentNode) {
            this.parentNode = parentNode;
        }

        public Shop getShop() {
            return shop;
        }

        public void setShop(Shop shop) {
            this.shop = shop;
        }
    }
}

// 节点类

