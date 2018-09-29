package com.fulu.game.core.search.component;

import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.SearchException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.Criteria;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.fulu.game.core.search.domain.UserDoc;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
public class UserSearchComponent extends AbsSearchComponent<UserDoc, Integer> {


    public final static String INDEX_TYPE = "user";
    public final static String INDEX_DB = "user";

    @Autowired
    private JestClient jestClient;
    @Autowired
    private Config configProperties;



    /**
     * 保存索引
     *
     * @param userDoc
     * @return
     */
    public boolean saveIndex(UserDoc userDoc) {
        return createIndex(userDoc);
    }

    /**
     * 获取陪玩师列表
     * @param pageNum
     * @param pageSize
     * @param nickname
     * @return
     * @throws IOException
     */
    public Page<UserDoc> findPlayerByNickName(Integer pageNum,
                                        Integer pageSize,
                                        String nickname) throws IOException {
        return findByNickName( pageNum, pageSize, nickname, UserTypeEnum.ACCOMPANY_PLAYER.getType());
    }

    /**
     * 通过昵称查询用户列表(暂未用到，查询所有用户)
     * @param pageNum
     * @param pageSize
     * @param nickname
     * @return
     * @throws IOException
     */
    public Page<UserDoc> findByNickName(Integer pageNum,
                                        Integer pageSize,
                                        String nickname) throws IOException {
        return findByNickName( pageNum, pageSize, nickname, null);
    }


    /**
     * 通过昵称查询用户列表
     * @param pageNum
     * @param pageSize
     * @param nickname
     * @return type用户类型查询（目前首页只会查询type为2的即陪玩师）
     * @throws IOException
     */
    public Page<UserDoc> findByNickName(Integer pageNum,
                                            Integer pageSize,
                                            String nickname,
                                        Integer type) throws IOException {
        //封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = matchQuery("nickname", nickname);
        QueryBuilder queryBuilder = null;
        // 如果类型不为空
        if (type != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.filter(QueryBuilders.termQuery("type", type));
            queryBuilder = QueryBuilders.boolQuery().must(matchQueryBuilder).must(boolQueryBuilder);
        } else {
            queryBuilder = QueryBuilders.boolQuery().must(matchQueryBuilder);
        }

        searchSourceBuilder.query(queryBuilder);

        //排序
//        FieldSortBuilder scoreSort = SortBuilders.fieldSort("_score").order(SortOrder.DESC);
//        searchSourceBuilder.sort(scoreSort);

        //分页
        int form = (pageNum - 1) * pageSize;
        searchSourceBuilder.from(form).size(pageSize);
        String sql = searchSourceBuilder.toString();
        log.info("昵称查询用户:{}", sql);
        //查询
        Search search = new Search.Builder(sql)
                .addIndex(getIndexDB())
                .addType(getIndexType())
                .build();
        SearchResult result = jestClient.execute(search);
        if (!result.isSucceeded()) {
            throw new SearchException(SearchException.ExceptionCode.FIND_EXCEPTION, sql, result.getErrorMessage());
        }
        List<UserDoc> userDocList = result.getSourceAsObjectList(UserDoc.class, false);
        Page<UserDoc> page = new Page<>(pageNum, pageSize);
        page.addAll(userDocList);
        page.setTotal(result.getTotal());
        return page;
    }


    @Override
    protected String getIndexType() {
        return INDEX_TYPE;
    }

    @Override
    protected String getIndexDB() {
        return configProperties.getEvn().getPrefix().toLowerCase() + "-" + INDEX_DB;
    }

    @Override
    protected JestClient getJestClient() {
        return jestClient;
    }


}
