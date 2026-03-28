package com.bs.tourism.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String orderBy;
    private String orderType = "desc";

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
