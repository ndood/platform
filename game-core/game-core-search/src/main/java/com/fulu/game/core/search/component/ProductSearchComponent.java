package com.fulu.game.core.search.component;

import com.fulu.game.common.exception.SearchException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.Criteria;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Component
@Slf4j
public class ProductSearchComponent extends AbsSearchComponent<ProductShowCaseDoc, Integer> {


    public final static String INDEX_TYPE = "product";

    @Autowired
    private JestClient jestClient;
    @Autowired
    private Config configProperties;

    /**
     * 保存商品索引
     *
     * @param productShowCaseDoc
     * @return
     */
    public boolean saveProductIndex(ProductShowCaseDoc productShowCaseDoc) {
        return createIndex(productShowCaseDoc);
    }

    /**
     * 通过用户查找商品索引
     *
     * @param userId
     * @return
     */
    public List<ProductShowCaseDoc> findByUser(Integer userId) {
        List<Criteria> criterias = Lists.newArrayList(new Criteria("userId", userId));
        return search(criterias, ProductShowCaseDoc.class);
    }


    /**
     * 昵称查询
     *
     * @param pageNum
     * @param pageSize
     * @param nickName
     * @return
     * @throws IOException
     */
    public Page<ProductShowCaseDoc> findByNickName(Integer pageNum,
                                                   Integer pageSize,
                                                   String nickName) throws IOException {
        //封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = matchQuery("nickName", nickName);
        searchSourceBuilder.query(matchQueryBuilder);


        //排序
        FieldSortBuilder scoreSort = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
        FieldSortBuilder onLineSort = SortBuilders.fieldSort("onLine").order(SortOrder.DESC);
        searchSourceBuilder.sort(scoreSort);
        searchSourceBuilder.sort(onLineSort);

        //分页
        int form = (pageNum - 1) * pageSize;
        searchSourceBuilder.from(form).size(pageSize);
        String sql = searchSourceBuilder.toString();
        log.info("昵称查询商品:{}", sql);
        //查询
        Search search = new Search.Builder(sql)
                .addIndex(getIndexDB())
                .addType(getIndexType())
                .build();
        SearchResult result = jestClient.execute(search);
        if (!result.isSucceeded()) {
            throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION, sql, result.getErrorMessage());
        }
        List<ProductShowCaseDoc> showCaseDocList = result.getSourceAsObjectList(ProductShowCaseDoc.class, false);
        Page<ProductShowCaseDoc> page = new Page(pageNum, pageSize);
        page.addAll(showCaseDocList);
        page.setTotal(result.getTotal());
        return page;
    }


    /**
     * 分类商品查询
     *
     * @param categoryId
     * @param gender
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     * @throws IOException
     */
    public Page<ProductShowCaseDoc> searchShowCaseDoc(int categoryId,
                                                      Integer gender,
                                                      Integer pageNum,
                                                      Integer pageSize,
                                                      String orderBy) throws IOException {
        //封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (gender != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("gender", gender));
        }
        boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId", categoryId));
        boolQueryBuilder.filter(QueryBuilders.termQuery("isIndexShow", true));
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        //在线状态排在前面
        FieldSortBuilder onLineSort = SortBuilders.fieldSort("onLine").order(SortOrder.DESC);
        searchSourceBuilder.sort(onLineSort);
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "sales";
        }
        if ("newman".equals(orderBy)) {
            //排序
            FieldSortBuilder sorter = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            searchSourceBuilder.sort(sorter);
        } else if ("sales".equals(orderBy)) {
            FieldSortBuilder sorter = SortBuilders.fieldSort("orderCount").order(SortOrder.DESC);
            searchSourceBuilder.sort(sorter);
        }

        //分页
        int form = (pageNum - 1) * pageSize;
        searchSourceBuilder.from(form).size(pageSize);
        String sql = searchSourceBuilder.toString();
        log.info("分页查询类别下商品:{}", sql);
        //查询
        Search search = new Search.Builder(sql)
                .addIndex(getIndexDB())
                .addType(getIndexType())
                .build();
        SearchResult result = jestClient.execute(search);
        if (!result.isSucceeded()) {
            throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION, sql, result.getErrorMessage());
        }
        List<ProductShowCaseDoc> showCaseDocList = result.getSourceAsObjectList(ProductShowCaseDoc.class, false);
        Page<ProductShowCaseDoc> page = new Page(pageNum, pageSize);
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
        return configProperties.getElasticsearch().getIndexDB();
    }

    @Override
    protected JestClient getJestClient() {
        return jestClient;
    }


}
