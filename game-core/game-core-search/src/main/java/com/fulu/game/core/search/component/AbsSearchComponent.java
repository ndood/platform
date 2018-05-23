package com.fulu.game.core.search.component;

import com.fulu.game.core.search.domain.Criteria;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

@Slf4j
public abstract class AbsSearchComponent<T,K> {


    /**
     * 创建索引
     * @param t
     * @return
     */
    public boolean createIndex(T t){
        try {
            Index index = new Index.Builder(t)
                    .index(getIndexDB())
                    .type(getIndexType())
                    .build();
            DocumentResult result = getJestClient().execute(index);
            return result.isSucceeded();
        }catch (Exception  e){
            log.error("创建索引错误:",e);
            return false;
        }
    }

    /**
     * 更新索引
     * @param t
     * @return
     */
    public boolean updateIndex(T t){
        try {
            Update update = new Update.Builder(t)
                    .index(getIndexDB())
                    .type(getIndexType())
                    .refresh(true)
                    .build();
            DocumentResult result = getJestClient().execute(update);
            return result.isSucceeded();
        }catch (Exception  e){
            log.error("创建索引错误:",e);
            return false;
        }
    }

    /**
     * 删除
     *
     * @param id 文档id
     * @return 是否执行成功
     */
    public boolean deleteIndex(K id) {
        try {
            Delete delete = new Delete.Builder(String.valueOf(id))
                    .index(getIndexDB())
                    .type(getIndexType())
                    .build();
            DocumentResult result = getJestClient().execute(delete);
            return result.isSucceeded();
        } catch (Exception e) {
            throw new RuntimeException("delete exception", e);
        }
    }

    /**
     * 通过ID查询
     * @param id
     * @param clazz
     * @return
     */
    public T searchById(K id,Class<T> clazz){
        try {
            Get get= new Get.Builder(getIndexDB(), String.valueOf(id)).type(getIndexType()).build();
            DocumentResult result = getJestClient().execute(get);
            return result.getSourceAsObject(clazz);
        } catch (Exception e) {
            throw new RuntimeException("searchById exception", e);
        }
    }

    /**
     * 通用查询
     * @param criterias
     * @param clazz
     * @return
     */
    public List<T> search(List<Criteria> criterias, Class<T> clazz) {
        try {
            SearchResult result = getJestClient().execute(new Search.Builder(buildSearch(criterias).toString())
                    // multiple index or types can be added.
                    .addIndex(getIndexDB())
                    .addType(getIndexType())
                    .build());
            return result.getSourceAsObjectList(clazz, false);

        } catch (Exception e) {
            throw new RuntimeException("search exception", e);
        }
    }

    /**
     * 生成查询语句
     * @param criterias
     * @return
     */
    private SearchSourceBuilder buildSearch(List<Criteria> criterias) {
        //指定查询的库表
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (criterias != null && !criterias.isEmpty()) {
            //构建查询条件必须嵌入filter中！
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Criteria c : criterias) {
                boolQueryBuilder.filter(QueryBuilders.termQuery(c.getFieldName(), c.getFieldValue()));
            }
            searchSourceBuilder.query(boolQueryBuilder);
        }
        return searchSourceBuilder;
    }



    protected abstract String getIndexType();

    protected abstract String getIndexDB();

    protected abstract JestClient getJestClient();

}
