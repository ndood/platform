package com.fulu.game.core.service;

import com.fulu.game.core.entity.ProductTop;
import com.github.pagehelper.PageInfo;


/**
 * 商品置顶表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-07 15:28:32
 */
public interface ProductTopService extends ICommonService<ProductTop,Integer>{

	/**
	 * 置顶上下架
	 * @param id
	 * @param status
	 */
	 void productTopPutAway(Integer id, Boolean status);

	 ProductTop findByUserAndCategory(Integer userId, Integer categoryId);

	/**
	 * 查找用户所有商品类型排序
	 * @param userId
	 * @param categoryId
	 * @return
	 */
	 int findTopSortByUserCategory(Integer userId,Integer categoryId);


	 PageInfo<ProductTop> productList(int pageNum,int pageSize,String nickName,String mobile,Integer categoryId);

}
