package com.fulu.game.core.search.component;

import com.fulu.game.common.exception.SearchException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.Criteria;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
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


    enum OrderType{
        sales,newman
    }

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
        Page<ProductShowCaseDoc> page = new Page<>(pageNum, pageSize);
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
    public <T> Page<T> searchShowCaseDoc(int categoryId,
                                         Integer gender,
                                         Integer pageNum,
                                         Integer pageSize,
                                         String orderBy,
                                         String dans,
                                         String prices,
                                         Class<T> type) throws IOException {
        //封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (gender != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("gender", gender));
        }

        boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId", categoryId));
        boolQueryBuilder.filter(QueryBuilders.termQuery("isIndexShow", true));
        if(dans != null && !"".equals(dans)){
            String[] dansArr = dans.split(",");
            boolQueryBuilder.filter(QueryBuilders.termsQuery("dan", dansArr));
        }
        if(prices != null && !"".equals(prices)){
            String[] priceArr = prices.split(",");
            boolQueryBuilder.filter(QueryBuilders.termsQuery("price", priceArr));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        //置顶排序
        FieldSortBuilder topSort = SortBuilders.fieldSort("topSort").order(SortOrder.DESC);
        searchSourceBuilder.sort(topSort);

        //排序
        //在线状态排在前面
        FieldSortBuilder onLineSort = SortBuilders.fieldSort("onLine").order(SortOrder.DESC);
        searchSourceBuilder.sort(onLineSort);

        //切换排序规则
        if (StringUtils.isBlank(orderBy)) {
            orderBy = OrderType.sales.name();
        }
        if (OrderType.newman.name().equals(orderBy)) {
            //排序
            FieldSortBuilder sorter = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            searchSourceBuilder.sort(sorter);
        } else if (OrderType.sales.name().equals(orderBy)) {
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
        List<T> showCaseDocList = result.getSourceAsObjectList(type, false);
        Page<T> page = new Page<>(pageNum, pageSize);
        page.addAll(showCaseDocList);
        page.setTotal(result.getTotal());
        page.setOrderBy(orderBy);
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
    public <T> Page<T> searchShowCaseDoc(int categoryId,
                                                      Integer gender,
                                                      Integer pageNum,
                                                      Integer pageSize,
                                                      String orderBy,
                                                      Class<T> type) throws IOException {
        return searchShowCaseDoc( categoryId, gender, pageNum, pageSize, orderBy, null, null,type);
    }

    /**
     * Put映射
     * @throws Exception
     */
    public void createIndexMapping() throws Exception {
        String source = "{\"" + getIndexType() + "\":{\"properties\":{"
                + "\"dan\":{\"type\":\"string\",\"index\":\"not_analyzed\"}"
                + "}}}";
        System.out.println(source);

        PutMapping putMapping = new PutMapping.Builder(getIndexDB(), getIndexType(), source).build();
        JestResult jr = jestClient.execute(putMapping);
        System.out.println(jr.isSucceeded());
    }

    /**
     * 创建索引
     * @throws Exception
     */
    public void createIndex() throws Exception {
        JestResult jr = jestClient.execute(new CreateIndex.Builder(getIndexDB()).build());
        System.out.println(jr.isSucceeded());
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
