package com.fulu.game.core.search.component;

import com.fulu.game.common.exception.SearchException;
import com.fulu.game.core.search.domain.Criteria;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
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
            if(!result.isSucceeded()){
                throw new SearchException(SearchException.ExceptionCode.SAVE_EXCEPTION,index.getPathToResult(),result.getErrorMessage());
            }
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
    public boolean updateIndex(T t,K id){
        try {
            Update update = new Update.Builder(t)
                    .index(getIndexDB())
                    .type(getIndexType())
                    .id(id.toString())
                    .refresh(true)
                    .build();
            DocumentResult result = getJestClient().execute(update);
            if(!result.isSucceeded()){
                throw new SearchException(SearchException.ExceptionCode.SAVE_EXCEPTION,update.getPathToResult(),result.getErrorMessage());
            }
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
            if(!result.isSucceeded()){
                throw new SearchException(SearchException.ExceptionCode.DEL_EXCEPTION,"deleteId:"+id,result.getErrorMessage());
            }
            return result.isSucceeded();
        } catch (Exception e) {
            log.error("删除索引错误:",e);
            return false;
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
            if(!result.isSucceeded()){
                throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION,get.getPathToResult(),result.getErrorMessage());
            }
            return result.getSourceAsObject(clazz);
        } catch (Exception e) {
            log.error("查询索引错误:",e);
            return null;
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
            String sql =  buildSearch(criterias).toString();
            SearchResult result = getJestClient().execute(new Search.Builder(sql)
                    // multiple index or types can be added.
                    .addIndex(getIndexDB())
                    .addType(getIndexType())
                    .build());
            if(!result.isSucceeded()){
                throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION,sql,result.getErrorMessage());
            }
            return result.getSourceAsObjectList(clazz, false);
        } catch (Exception e) {
            log.error("查询索引错误:",e);
            return new ArrayList<>();
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
