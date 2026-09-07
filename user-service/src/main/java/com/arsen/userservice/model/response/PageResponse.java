package com.arsen.userservice.model.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PageResponse<T> {
    private Integer page;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private List<T> content;
}
