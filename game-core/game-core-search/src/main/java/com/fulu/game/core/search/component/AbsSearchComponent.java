package com.fulu.game.core.search.component;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbsSearchComponent<T> {



    public Boolean createIndex(T t){
        try {
            Index index = new Index.Builder(t)
                    .index(getIndexDB())
                    .type(getIndexType())
                    .build();
            getJestClient().execute(index);
        }catch (Exception  e){
            log.error("创建索引错误:",e);
            return false;
        }
        return true;
    }


    public abstract String getIndexType();

    public abstract String getIndexDB();

    public abstract JestClient getJestClient();

}
