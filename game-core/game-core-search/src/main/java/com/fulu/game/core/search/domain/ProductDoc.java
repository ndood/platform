package com.fulu.game.core.search.domain;

import io.searchbox.annotations.JestId;
import lombok.Data;

@Data
public class ProductDoc {

    @JestId
    private Integer id;

    private String name;



}
