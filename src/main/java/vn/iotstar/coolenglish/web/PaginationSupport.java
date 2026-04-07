package vn.iotstar.coolenglish.web;

import java.util.Collections;
import java.util.List;

public final class PaginationSupport {

    public static final int DEFAULT_PAGE_SIZE = 30;

    private PaginationSupport() {
    }

    public static <T> Page<T> paginate(List<T> source, String pageParam, int pageSize) {
        List<T> items = source == null ? Collections.emptyList() : source;
        int safePageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        int totalItems = items.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / safePageSize));
        int currentPage = normalizePage(pageParam, totalPages);

        int fromIndex = (currentPage - 1) * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, totalItems);
        List<T> pageItems = fromIndex >= totalItems ? Collections.emptyList() : items.subList(fromIndex, toIndex);

        return new Page<>(pageItems, currentPage, totalPages, totalItems, safePageSize);
    }

    private static int normalizePage(String pageParam, int totalPages) {
        int page = 1;
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        if (page < 1) {
            return 1;
        }

        return Math.min(page, totalPages);
    }

    public static final class Page<T> {
        private final List<T> items;
        private final int currentPage;
        private final int totalPages;
        private final int totalItems;
        private final int pageSize;

        private Page(List<T> items, int currentPage, int totalPages, int totalItems, int pageSize) {
            this.items = items;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
            this.pageSize = pageSize;
        }

        public List<T> getItems() {
            return items;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getPageSize() {
            return pageSize;
        }
    }
}

