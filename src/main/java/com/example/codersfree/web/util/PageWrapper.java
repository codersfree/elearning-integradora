package com.example.codersfree.web.util;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageWrapper<T> {

    public static final int MAX_PAGINAS_VISIBLES = 5;

    private Page<T> page;
    private List<Integer> pageNumbers;

    public PageWrapper(Page<T> page) {
        this.page = page;

        int totalPages = page.getTotalPages();
        int currentPage = page.getNumber();

        int start = Math.max(0, currentPage - (MAX_PAGINAS_VISIBLES / 2));
        int end = Math.min(totalPages - 1, start + MAX_PAGINAS_VISIBLES - 1);

        if (end - start + 1 < MAX_PAGINAS_VISIBLES && totalPages > MAX_PAGINAS_VISIBLES) {
            start = Math.max(0, end - MAX_PAGINAS_VISIBLES + 1);
        }

        if (totalPages > 0) {
            this.pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
        }
    }

    public List<T> getContent() {
        return page.getContent();
    }

    public int getNumber() {
        return page.getNumber();
    }

    public int getSize() {
        return page.getSize();
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean hasNext() {
        return page.hasNext();
    }

    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    public List<Integer> getPageNumbers() {
        return this.pageNumbers;
    }
}