package org.example.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.DeleteResponse;
import org.example.productservice.dto.PageableResponse;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.exceptions.BadRequestException;
import org.example.productservice.exceptions.NotFoundException;
import org.example.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Раздел ПРОДУКТ", description = "Управление продуктами")
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Получение продукта",
            description = "Получение данных о продукте по его идентификационному номеру (id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт найден"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(
            @Parameter(description = "Идентификатор продукта (id)")
            @PathVariable Long id
    ) throws NotFoundException {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Operation(summary = "Добавление продукта",
            description = "Добавление нового продукта. Имя продукта должно быть уникально.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт добавлен успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос")
    })
    @PostMapping()
    public ResponseEntity<ProductDto> addProduct(
            @RequestBody ProductDto request
    ) throws BadRequestException {
        return ResponseEntity.ok(productService.addProduct(request));
    }

    @Operation(summary = "Обновление продукта",
            description = "Обновление данных о продукте. Идентификатор (id) обязателен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные продукта успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос"),
            @ApiResponse(responseCode = "404", description = "Продукт для обновления не найден")
    })
    @PutMapping()
    public ResponseEntity<ProductDto> updateProduct(
            @RequestBody ProductDto request
    ) throws BadRequestException, NotFoundException {
        return ResponseEntity.ok(productService.updateProduct(request));
    }

    @Operation(summary = "Удаление продукта",
            description = "Продукт получает статус REMOVED. Идентификатор (id) обязателен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Продукт для удаления не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteProduct(
            @Parameter(description = "Идентификатор продукта (id)")
            @PathVariable Long id
    ) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @Operation(summary = "Получение страницы всех продуктов",
            description = "Получение страницы списка всех продуктов. Для получения списка, включая удаленные продукты," +
                    "используйте параметр showRemoved= \"yes\", для показа только удаленных - showRemoved=\"only\"")
    @GetMapping()
    public ResponseEntity<PageableResponse<ProductDto>> getAllProduct(
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false, defaultValue = "no") String showRemoved
    ) {
        return ResponseEntity.ok(productService.getProductPage(page, size, showRemoved));
    }

}
