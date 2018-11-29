package com.vcat.common.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.LRTreeEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 左右节点树级Dao
 * @param <T>
 */
@MyBatisDao
public interface LRTreeDao<T extends LRTreeEntity> extends CrudDao<T> {
    /**
     * 获取根节点
     * @return
     */
    T getRoot();

    /**
     * 获取父级节点
     * @param t
     * @return
     */
    T findParent(T t);

    /**
     * 验证分类是否有子分类
     * @param id
     * @return
     */
    boolean hasChild(String id);

    /**
     * 删除节点及所有下级节点
     * @param tableName
     * @param lft
     * @param rgt
     * @return
     */
    int delete(@Param(value = "tableName")String tableName, @Param(value="lft")Integer lft, @Param(value="rgt")Integer rgt);

    /**
     * 获取需要修改的坐标边界值
     * @param id
     * @return
     */
    Integer getLeft(@Param(value = "tableName")String tableName, @Param(value = "id")String id);

    /**
     * 更新lft坐标
     * @param lft
     * @param id
     */
    void updateL(@Param(value = "tableName")String tableName, @Param(value="lft")Integer lft, @Param(value="id")String id);

    /**
     * 更新rgt坐标
     * @param rgt
     * @param id
     */
    void updateR(@Param(value = "tableName")String tableName, @Param(value="rgt")Integer rgt,@Param(value="id")String id);

    /**
     * 更新左坐标
     * @param lft
     */
    void updateLFTBeforeInsert(@Param(value = "tableName")String tableName, @Param(value = "lft")Integer lft);

    /**
     * 更新右坐标
     * @param rgt
     */
    void updateRGTBeforeInsert(@Param(value = "tableName")String tableName, @Param(value = "rgt")Integer rgt);

    /**
     * 更新左坐标
     * @param tableName
     * @param width
     * @param lft
     */
    void updateLFTAfterDelete(@Param(value = "tableName")String tableName, @Param(value = "width") Integer width, @Param(value = "lft") Integer lft);

    /**
     * 更新右坐标
     * @param tableName
     * @param width
     * @param rgt
     */
    void updateRGTAfterDelete(@Param(value = "tableName")String tableName, @Param(value = "width") Integer width, @Param(value = "rgt") Integer rgt);

    /**
     * 获取最大的rgt
     * @param tableName
     * @param lft
     * @return
     */
    Integer getMaxRgt(@Param(value = "tableName")String tableName, @Param(value = "lft")Integer lft);

    /**
     * 将修改的对象节点更新至根节点[步骤一]
     * @param tableName
     * @param maxRgt
     * @param lft
     * @param rgt
     */
    void updateLFTAndRgtToRootAfterUpdateOne(@Param(value = "tableName")String tableName, @Param(value = "maxRgt")Integer maxRgt,
                                             @Param(value = "lft")Integer lft,@Param(value = "rgt") Integer rgt);

    /**
     * 将修改的对象节点更新至根节点[步骤二]
     * @param tableName
     * @param width
     * @param rgt
     */
    void updateLFTToRootAfterUpdateTwo(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width,
                                       @Param(value = "rgt")Integer rgt);

    /**
     * 将修改的对象节点更新至根节点[步骤三]
     * @param tableName
     * @param width
     * @param rgt
     */
    void updateRGTToRootAfterUpdateThree(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width,
                                         @Param(value = "rgt")Integer rgt);

    /**
     * 修改父节点[步骤一]
     * @param tableName
     * @param width
     * @param parentLft
     */
    void updateLFTAfterUpdateOne(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width, @Param(value = "parentLft")Integer parentLft);

    /**
     * 修改父节点[步骤二]
     * @param tableName
     * @param width
     * @param parentLft
     */
    void updateRGTAfterUpdateTwo(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width, @Param(value = "parentLft")Integer parentLft);

    /**
     * 修改父节点[步骤三]
     * @param tableName
     * @param parentLft
     * @param lft
     * @param rgt
     */
    void updateLFTAndRGTAfterUpdateThree(@Param(value = "tableName")String tableName, @Param(value = "parentLft")Integer parentLft, @Param(value = "lft")Integer lft, @Param(value = "rgt")Integer rgt);

    /**
     * 修改父节点[步骤四]
     * @param tableName
     * @param width
     * @param rgt
     */
    void updateLFTAfterUpdateFour(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width, @Param(value = "rgt")Integer rgt);

    /**
     * 修改父节点[步骤五]
     * @param tableName
     * @param width
     * @param rgt
     */
    void updateRGTAfterUpdateFive(@Param(value = "tableName")String tableName, @Param(value = "width")Integer width, @Param(value = "rgt")Integer rgt);
}