package org.example.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.productservice.model.Product;

import java.io.Serializable;

@Schema(description = "Объект данных продукта")
@Getter
@AllArgsConstructor
public class ProductDto implements Serializable {

    @Schema(description = "Идентификатор", example = "1")
    private final long id;
    @Schema(description = "Имя", example = "Шоколад Snickers с лесным орехом")
    private final String name;
    @Schema(description = "Описание", example = "Карамель: арахис, глюкозный сироп (кукурузный, пшеничный), " +
            "фундук дробленый, сахар, масло пальмовое рафинированное дезодорированное, сухое обезжиренное молоко, " +
            "соль, ароматизатор (ванилин).")
    private final String description;
    @Schema(description = "Калорийная ценность", example = "514")
    @JsonProperty("kcal")
    private final Integer kilocalories;

    public ProductDto(Product product) {
        id = product.getId();
        name = product.getName();
        description = (product.getDescription() != null) ? product.getDescription() : "";
        kilocalories = (product.getKilocalories() != null) ? product.getKilocalories() : 0;
    }

}
