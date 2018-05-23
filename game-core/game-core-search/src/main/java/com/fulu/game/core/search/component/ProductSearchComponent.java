package com.fulu.game.core.search.component;

import com.fulu.game.common.properties.Config;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import io.searchbox.client.JestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductSearchComponent extends AbsSearchComponent<ProductShowCaseDoc,Integer>{


    public final static String INDEX_TYPE = "product";

    @Autowired
    private JestClient jestClient;
    @Autowired
    private Config configProperties;


    public boolean saveProductIndex(ProductShowCaseDoc productShowCaseDoc){
        ProductShowCaseDoc sourceProductDoc = searchById(productShowCaseDoc.getId(),ProductShowCaseDoc.class);
        if(sourceProductDoc==null){
            return createIndex(productShowCaseDoc);
        }else{
            return updateIndex(productShowCaseDoc);
        }
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
