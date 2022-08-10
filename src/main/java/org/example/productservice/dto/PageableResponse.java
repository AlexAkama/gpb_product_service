package org.example.productservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Объект для передачи страницы ответа
 *
 * @param <T> тип(класс) объектов в списке
 */
@Schema(description = "Объект для передачи страницы ответа")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class PageableResponse<T extends Serializable> implements Serializable {

    /**
     * Общее количество записей в ответе
     */
    @Schema(description = "Общее количество записей в ответе")
    private Long total;
    /**
     * Порядковый номер страницы (нумерация с 1)
     */
    @Schema(description = "Порядковый номер страницы (нумерация с 1)")
    private Integer page;
    /**
     * Порядковый номер первой записи на странице
     */
    @Schema(description = "Порядковый номер первой записи на странице")
    private Long start;
    /**
     * Порядковый номер последней записи на странице
     */
    @Schema(description = "Порядковый номер последней записи на странице")
    private Long end;
    /**
     * Максимально количество записей на странице (размер страницы)
     */
    @Schema(description = "Максимально количество записей на странице (размер страницы)")
    private Integer size;
    /**
     * Общее количество страниц в ответе
     */
    @Schema(description = "Общее количество страниц в ответе")
    private Long pages;
    /**
     * Список данных на странице
     */
    @Schema(description = "Список данных на странице")
    private List<T> list;

    /**
     * Создание страницы ответа по параметрам
     *
     * @param total {@link PageableResponse#total Общее количество записей}
     * @param page  {@link PageableResponse#page Номер страницы}
     * @param size  {@link PageableResponse#size Размер страницы}
     * @param list  {@link PageableResponse#list Содержимое страницы}
     * @param <T>   Тип элементов списка страницы
     * @return Объект {@link PageableResponse}
     */
    public static <T extends Serializable> PageableResponse<T> create(long total, int page, int size, List<T> list) {
        PageableResponse<T> response = new PageableResponse<>();
        if (total < 0 || page < 0 || size < 1) return response;
        if (total > 0 && (list == null || list.isEmpty())) return response;
        long pages = Math.round(Math.ceil(1.0 * total / size));
        if (page > pages) return response;
        response.setTotal(total);
        response.setPage(page + 1);
        response.setSize(size);
        if (total > 0) {
            long start = (long) page  * size + 1;
            response.setStart(start);
            long end = start + size - 1;
            if (end > total) end = total;
            response.setEnd(end);
            response.setPages(pages);
            response.setList(list);
        } else {
            response.setStart(0L);
            response.setEnd(0L);
            response.setPages(1L);
            response.setList(emptyList());
        }
        return response;
    }

}
