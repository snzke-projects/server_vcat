package com.vcat.module.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.entity.Role;
import com.vcat.module.core.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service基类
 */
@Transactional(readOnly = true)
public abstract class BaseService {
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

    private static String PRODUCT_DATA_SCOPE_KEY = "productScope";

	/**
	 * 数据范围过滤
	 * @param user 当前用户对象
	 * @param officeAlias 机构表别名，多个用“,”逗号隔开。
	 * @param userAlias 用户表别名，多个用“,”逗号隔开，传递空，忽略此参数
	 * @return 标准连接条件对象
	 */
	public static String dataScopeFilter(User user, String officeAlias, String userAlias) {

		StringBuilder sqlString = new StringBuilder();
		
		// 进行权限过滤，多个角色权限范围之间为或者关系。
		List<String> dataScope = Lists.newArrayList();
		
		// 超级管理员，跳过权限过滤
		if (!user.isAdmin()){
			boolean isDataScopeAll = false;
			for (Role r : user.getRoleList()){
				for (String oa : StringUtils.split(officeAlias, ",")){
					if (!dataScope.contains(r.getDataScope()) && StringUtils.isNotBlank(oa)){
						if (Role.DATA_SCOPE_ALL.equals(r.getDataScope())){
							isDataScopeAll = true;
						}
						else if (Role.DATA_SCOPE_COMPANY_AND_CHILD.equals(r.getDataScope())){
							sqlString.append(" OR " + oa + ".id = '" + user.getCompany().getId() + "'");
							sqlString.append(" OR " + oa + ".parent_ids LIKE '" + user.getCompany().getParentIds() + user.getCompany().getId() + ",%'");
						}
						else if (Role.DATA_SCOPE_COMPANY.equals(r.getDataScope())){
							sqlString.append(" OR " + oa + ".id = '" + user.getCompany().getId() + "'");
							// 包括本公司下的部门 （type=1:公司；type=2：部门）
							sqlString.append(" OR (" + oa + ".parent_id = '" + user.getCompany().getId() + "' AND " + oa + ".type = '2')");
						}
						else if (Role.DATA_SCOPE_OFFICE_AND_CHILD.equals(r.getDataScope())){
							sqlString.append(" OR " + oa + ".id = '" + user.getOffice().getId() + "'");
							sqlString.append(" OR " + oa + ".parent_ids LIKE '" + user.getOffice().getParentIds() + user.getOffice().getId() + ",%'");
						}
						else if (Role.DATA_SCOPE_OFFICE.equals(r.getDataScope())){
							sqlString.append(" OR " + oa + ".id = '" + user.getOffice().getId() + "'");
						}
						else if (Role.DATA_SCOPE_CUSTOM.equals(r.getDataScope())){
							String officeIds =  StringUtils.join(r.getOfficeIdList(), "','");
							if (StringUtils.isNotEmpty(officeIds)){
								sqlString.append(" OR " + oa + ".id IN ('" + officeIds + "')");
							}
						}
						//else if (Role.DATA_SCOPE_SELF.equals(r.getDataScope())){
						dataScope.add(r.getDataScope());
					}
				}
			}
			// 如果没有全部数据权限，并设置了用户别名，则当前权限为本人；如果未设置别名，当前无权限为已植入权限
			if (!isDataScopeAll){
				if (StringUtils.isNotBlank(userAlias)){
					for (String ua : StringUtils.split(userAlias, ",")){
						sqlString.append(" OR " + ua + ".id = '" + user.getId() + "'");
					}
				}else {
					for (String oa : StringUtils.split(officeAlias, ",")){
						//sqlString.append(" OR " + oa + ".id  = " + user.getOffice().getId());
						sqlString.append(" OR " + oa + ".id IS NULL");
					}
				}
			}else{
				// 如果包含全部权限，则去掉之前添加的所有条件，并跳出循环。
				sqlString = new StringBuilder();
			}
		}
		if (StringUtils.isNotBlank(sqlString.toString())){
			return " AND (" + sqlString.substring(4) + ")";
		}
		return "";
	}

	/**
	 * 过滤商品过滤SQL
     * @param data 需要添加的对象
	 * @param distributionTableAlias 供应商表别名
	 * @param orderTableAlias 订单表别名
	 * @return SQL
	 */
	public static void productDataScopeFilter(DataEntity<?> data, String distributionTableAlias, String orderTableAlias){
		StringBuffer sql = new StringBuffer();
        if(null == data){
            return;
        }
        User user = data.getCurrentUser();
		// 如果用户或用户所属机构则不做过滤供应商
		if(null == user || null == user.getOffice()){
			return;
		}
		// 超级管理员，跳过权限过滤
		if(user.isAdmin()){
			return;
		}

        Set<String> officeIdSet = Sets.newHashSet();

        // 将用户所属机构加入权限集合中
        officeIdSet.add(user.getOffice().getId());

        // 获取所有角色所属机构加入权限集合中
        List<Role> roleList = user.getRoleList();
		for (Role r : roleList){
            // 包含全部权限，跳过权限过滤
			if (Role.DATA_SCOPE_ALL.equals(r.getDataScope())){
				return;
			}
            officeIdSet.add(r.getOffice().getId());
		}

        // 拼装权限SQL
        StringBuffer officeIds = new StringBuffer();

        for (String officeId : officeIdSet){
            officeIds.append(",'" + officeId + "'");
        }

		if(StringUtils.isNotEmpty(distributionTableAlias)){
			distributionTableAlias = distributionTableAlias.trim();

			// 添加供应商权限过滤
			sql.append(distributionTableAlias +".id in (select distribution_id from ec_office_distribution where office_id in (''"+officeIds.toString()+"))");
		}else if(StringUtils.isNotEmpty(orderTableAlias)){
			orderTableAlias = orderTableAlias.trim();

			// 添加供应商权限过滤
			sql.append(orderTableAlias+".id in (SELECT order_id FROM ec_order_item orderItem");
            sql.append(" LEFT JOIN ec_product product ON product.id = orderItem.product_id");
            sql.append(" LEFT JOIN ec_office_distribution office ON office.distribution_id = product.distribution_id");
            sql.append(" WHERE office.office_id in (''"+officeIds.toString()+")");
            sql.append(" AND orderItem.order_item_type in ('1','2','3','4','7','8','9')");
            sql.append(")");
		}

        sql.insert(0," AND (");
        sql.append(")");

        data.getSqlMap().put(PRODUCT_DATA_SCOPE_KEY,sql.toString());
	}
}
