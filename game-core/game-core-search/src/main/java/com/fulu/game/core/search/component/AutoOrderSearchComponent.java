package com.fulu.game.core.search.component;

import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.AutoAssignOrderDoc;
import io.searchbox.client.JestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutoOrderSearchComponent extends AbsSearchComponent<AutoAssignOrderDoc, Integer>{

    public final static String INDEX_TYPE = "auto_assign_order";

    @Autowired
    private JestClient jestClient;

    @Autowired
    private Config configProperties;


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
