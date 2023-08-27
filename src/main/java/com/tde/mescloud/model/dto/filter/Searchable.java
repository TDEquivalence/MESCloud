package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Searchable {

    FilterSearch getSearch();
}
