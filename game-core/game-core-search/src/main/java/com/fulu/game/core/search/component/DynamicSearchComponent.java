package com.fulu.game.core.search.component;

import com.fulu.game.common.exception.SearchException;
import com.fulu.game.core.search.domain.DynamicDoc;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.github.pagehelper.Page;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/24 14:57.
 * @Description:
 */
@Component
@Slf4j
public class DynamicSearchComponent  extends AbsSearchComponent<DynamicDoc, Long>  {

    public final static String INDEX_TYPE = "dynamic";
    public final static String INDEX_DB = "dynamic";

    @Autowired
    private JestClient jestClient;

    enum OrderType{
        geohash,geohashShort,createTime,id
    }

    /**
     * 保存动态索引
     * @param dynamicDoc
     * @return
     */
    public boolean saveDynamicIndex(DynamicDoc dynamicDoc) {
        return createIndex(dynamicDoc);
    }

    /**
     * 修改动态中实时变化的值
     * @param id 动态id
     * @param rewards 是否自增打赏次数（true：自增；false：不自增）
     * @param likes 点赞次数 (取消点赞： -1；点赞：1)
     * @param comments 评论次数 (删除评论： -1；评论：1)
     * @param clicks 是否自增点击次数（true：自增；false：不自增）
     * @param userId 点赞的时候需要传userId
     * @return
     */
    public boolean updateIndexFilesById(Long id, boolean rewards, Integer likes,Integer comments,boolean clicks, Integer userId){
        DynamicDoc dynamicDoc = searchById(id, DynamicDoc.class);
        if(dynamicDoc == null ){
            log.info("修改动态索引异常，未找到id： {}", id);
            return false;
        }
        if(rewards ){
            if(dynamicDoc.getRewards() != null ){
                dynamicDoc.setRewards(dynamicDoc.getRewards() + 1);
            } else {
                dynamicDoc.setRewards( 1L);
            }
        }
        if(likes != null && likes.intValue() != 0){
            boolean flag = false;
            if(likes.intValue() > 0){
                flag = true;
            }
            if(dynamicDoc.getLikes() != null){
                dynamicDoc.setLikes(dynamicDoc.getLikes() + likes);
            } else {
                dynamicDoc.setLikes(1L);
                flag = true;
            }
            if(dynamicDoc.getLikeUserIds() == null){
                BitSet bitSet = new BitSet();
                bitSet.set(userId, flag);
                dynamicDoc.setLikeUserIds(bitSet);
            } else {
                BitSet bitSet = dynamicDoc.getLikeUserIds();
                bitSet.set(userId, flag);
                dynamicDoc.setLikeUserIds(bitSet);
            }
        }
        if(comments != null ){
            if(dynamicDoc.getComments() != null){
                dynamicDoc.setComments(dynamicDoc.getComments() + comments);
            } else {
                dynamicDoc.setComments(1L);
            }
        }
        if(clicks ){
            if(dynamicDoc.getClicks() != null){
                dynamicDoc.setClicks(dynamicDoc.getClicks() + 1);
            } else {
                dynamicDoc.setClicks(1L);
            }
        }
        return createIndex(dynamicDoc);
    }

    /**
     * 获取动态列表
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最大id；下滑：传客户端最小id
     * @param isTop 是否置顶（1：是；0：否）
     * @param isHot 是否热门（1：是；0：否）
     * @param pageSize 每页数量
     * @param orderBy 排序属性
     * @param userIdList 用户id集合
     * @param gender 性别【预留】
     * @return
     * @throws IOException
     */
    public Page<DynamicDoc> searchDynamicDocList( Integer slide,
                                              Integer id,
                                              Integer isTop,
                                              Integer isHot,
                                              Integer pageSize,
                                              String orderBy,
                                              List<String> userIdList,
                                              Integer gender ) throws IOException {
        //封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (isTop != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("isTop", isTop));
        }
        if (isTop != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("isHot", isHot));
        }
        if (gender != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userGender", gender));
        }
        if(slide != null && slide == 0 && id > 0){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("id").gt(id));
        }
        if(slide != null && slide == 1 && id > 0){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("id").lt(id));
        }
        //关注用户id包含自己id
        if(userIdList != null){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("userId", userIdList));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        //在线状态排在前面
//        FieldSortBuilder timeSort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
//        searchSourceBuilder.sort(timeSort);
        //置顶排序
//        FieldSortBuilder topSort = SortBuilders.fieldSort("topSort").order(SortOrder.DESC);
//        searchSourceBuilder.sort(topSort);
        //切换排序规则
        if (StringUtils.isBlank(orderBy)) {
            orderBy = OrderType.id.name();
        }
        FieldSortBuilder sorter = SortBuilders.fieldSort(orderBy).order(SortOrder.DESC);
        searchSourceBuilder.sort(sorter);

        //分页
        int pageNum = 1;
        int form = (pageNum - 1) * pageSize;
        searchSourceBuilder.from(form).size(pageSize);
        String sql = searchSourceBuilder.toString();
        log.info("分页查询关注用户动态列表:{}", sql);
        //查询
        Search search = new Search.Builder(sql)
                .addIndex(getIndexDB())
                .addType(getIndexType())
                .build();
        SearchResult result = jestClient.execute(search);
        if (!result.isSucceeded()) {
            throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION, sql, result.getErrorMessage());
        }
        List<DynamicDoc> showCaseDocList = result.getSourceAsObjectList(DynamicDoc.class, false);
        Page<DynamicDoc> page = new Page<>(pageNum, pageSize);
        page.addAll(showCaseDocList);
        page.setTotal(result.getTotal());
        page.setOrderBy(orderBy);
        return page;
    }



    @Override
    protected String getIndexType() {
        return INDEX_TYPE;
    }

    @Override
    protected String getIndexDB() {
        return INDEX_DB;
    }

    @Override
    protected JestClient getJestClient() {
        return jestClient;
    }
}
