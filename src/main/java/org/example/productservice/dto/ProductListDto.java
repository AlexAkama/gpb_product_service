package org.example.productservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.productservice.model.ProductList;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Объект данных списка продуктов")
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductListDto implements Serializable {

    @Schema(description = "Идентификатор", example = "1")
    private final long id;
    @Schema(description = "Имя", example = "Шоколадные батончики")
    private final String name;
    @JsonProperty("products")
    @Schema(description = "Список продуктов, входящих в состав листа")
    private List<ProductDto> productDtoList;
    @Schema(description = "Суммарная калорийная ценность", example = "1100")
    @JsonProperty("calories")
    private Long caloriesTotal;

    public static ProductListDto getTestList() {
        return new ProductListDto(1, "TestList", null, null);
    }

    public ProductListDto(ProductList list) {
        id = list.getId();
        name = list.getName();
    }


}
