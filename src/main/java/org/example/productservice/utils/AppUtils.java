package org.example.productservice.utils;

public final class AppUtils {

    private AppUtils() {
    }

    public static PageAndSizePair getPageAndSizeToPageable(Long page, Long size, Long total, Long defaultPageSize) {
        if (page == null) {
            page = 1L;
            size = total;
        } else if (size == null) {
            size = defaultPageSize;
        }
        if (page < 1) page = 1L;
        if (size < 1) size = defaultPageSize;
        long pages = Math.round(Math.ceil(1.0 * total / size));
        if (page > pages) page = pages;
        return new PageAndSizePair(page.intValue() - 1, size.intValue());
    }

}
