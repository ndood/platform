package com.fulu.game.core.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by burgl on 2018/4/14.
 */
@ConfigurationProperties(prefix = "datasource")
@Component
@Data
public class MultipleDatabaseProperties {

    DataSourceProperties playAdminDb = new DataSourceProperties();
}
