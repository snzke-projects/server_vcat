package com.vcat.module.core.service;

import com.vcat.common.dao.LRTreeDao;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.LRTreeEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 左右节点树级Service基类
 * 继承此类时，Mapping文件必须提供findParent以及getRoot 查询
 */
@Transactional(readOnly = true)
public abstract class LRTreeService<D extends LRTreeDao<T>, T extends LRTreeEntity<T>>
		extends CrudService<D, T> {

	@Transactional(readOnly = false)
    public void save(T t) {
        if (t.getIsNewRecord()){
            t.preInsert();
            beforeInsert(t);
            dao.insert(t);  // 插入数据
        }else {
            T old = get(t);
            t.preUpdate();
            dao.update(t);
            afterUpdate(t,old.getParentId());
        }
    }

    /**
     * 在插入实体前的准备工作
     * @param t
     */
    private void beforeInsert(T t) {
        Integer left = dao.getLeft(t.getTableName(), t.getParentId());  // 获取需要更新的边界值
        left = null == left ? 0 : left;
        t.setLft(left + 1);   // 设置左坐标值
        t.setRgt(left + 2);   // 设置右坐标值
        // 历史数据分别加 2，为新数据腾出坐标值
        dao.updateLFTBeforeInsert(t.getTableName(), left);    // 更新左坐标值
        dao.updateRGTBeforeInsert(t.getTableName(), left);    // 更新右坐标值
    }

    /**
     * 更新实体后的维护工作
     * @param t
     */
    private void afterUpdate(T t, String oldParentId) {
        if(t.getParentId().equals(oldParentId)){
            return;
        }
        t = get(t);
        Integer width = t.getRgt() - t.getLft() + 1;
        if("0".equals(t.getParentId())){
            Integer maxRgt = dao.getMaxRgt(t.getTableName(), t.getLft());

            dao.updateLFTAndRgtToRootAfterUpdateOne(t.getTableName(), maxRgt, t.getLft(), t.getRgt());
            dao.updateLFTToRootAfterUpdateTwo(t.getTableName(), width, t.getRgt());
            dao.updateRGTToRootAfterUpdateThree(t.getTableName(), width, t.getRgt());
        }else if(StringUtils.isNotBlank(oldParentId) && !"0".equals(t.getParentId())){
            T parent = dao.findParent(t);
            dao.updateLFTAfterUpdateOne(t.getTableName(), width, parent.getLft());
            dao.updateRGTAfterUpdateTwo(t.getTableName(), width, parent.getLft());

            t = get(t);
            dao.updateLFTAndRGTAfterUpdateThree(t.getTableName(), parent.getLft(), t.getLft(), t.getRgt());

            width = t.getRgt() - t.getLft() + 1;
            dao.updateLFTAfterUpdateFour(t.getTableName(), width, t.getRgt());
            dao.updateRGTAfterUpdateFive(t.getTableName(), width, t.getRgt());
        }
    }

    @Transactional(readOnly = false)
    public void delete(T t) {
        t = get(t);
        Integer width = t.getRgt() - t.getLft() + 1;    // 获取需要删除的坐标范围
        dao.delete(t.getTableName(), t.getLft(), t.getRgt());
        dao.updateRGTAfterDelete(t.getTableName(), width, t.getRgt());    // 更新关联右坐标
        dao.updateLFTAfterDelete(t.getTableName(), width, t.getLft());    // 更新关联左坐标
    }

    /**
     * 重建库表的所有左右坐标值
     */
    @Transactional(readOnly = false)
    public void rebuild() throws InstantiationException, IllegalAccessException {
        T root = dao.getRoot();
        if(null != root) updateLR(0, root);
    }

    /**
     * 重建该对象左右坐标值，包含下级分类
     * @param lft
     * @param t
     * @return
     */
    private Integer updateLR(Integer lft,T t) throws IllegalAccessException, InstantiationException {
        Integer rgt;
        lft++;
        dao.updateL(t.getTableName(), lft, t.getId());

        if(dao.hasChild(t.getId())){
            T temp = (T) t.getClass().newInstance();
            temp.setParent(t);
            List<T> list = dao.findList(temp);
            rgt = lft;
            for (T c : list){
                rgt = updateLR(rgt,c);
            }
            rgt++;
        }else{
            rgt = lft + 1;
        }

        if(rgt > 0){
            dao.updateR(t.getTableName(), rgt, t.getId());
        }

        return rgt;
    }
}
