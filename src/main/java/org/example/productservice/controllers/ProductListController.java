package org.example.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.AppResponse;
import org.example.productservice.dto.DeleteResponse;
import org.example.productservice.dto.ProductListDto;
import org.example.productservice.dto.PageableResponse;
import org.example.productservice.exceptions.BadRequestException;
import org.example.productservice.exceptions.NotFoundException;
import org.example.productservice.services.ProductListService;
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

@Tag(name = "Раздел СПИСКИ ПРОДУКТОВ")
@Controller
@RequiredArgsConstructor
@RequestMapping("/lists")
public class ProductListController {

    private final ProductListService listService;

    @Operation(summary = "Получение списка продукта",
            description = "Получение данных списка продукта по его идентификатору (id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список найден"),
            @ApiResponse(responseCode = "404", description = "Список не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductListDto> getList(
            @Parameter(description = "Идентификатор списка (id)")
            @PathVariable Long id
    ) throws NotFoundException {
        return ResponseEntity.ok(listService.getList(id));
    }

    @Operation(summary = "Добавление списка",
            description = "Добавление нового списка. Имя списка должно быть уникально.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список добавлен успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос")
    })
    @PostMapping()
    public ResponseEntity<ProductListDto> addList(
            @RequestBody ProductListDto request
    ) throws BadRequestException {
        return ResponseEntity.ok(listService.addList(request));
    }

    @Operation(summary = "Обновление списка",
            description = "Обновление данных списка продукта. Идентификатор (id) обязателен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные списка успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос"),
            @ApiResponse(responseCode = "404", description = "Список для обновления не найден")
    })
    @PutMapping()
    public ResponseEntity<ProductListDto> updateList(
            @RequestBody ProductListDto request
    ) throws BadRequestException, NotFoundException {
        return ResponseEntity.ok(listService.updateList(request));
    }

    @Operation(summary = "Удаление списка",
            description = "Список продуктов получает статус REMOVED. Идентификатор (id) обязателен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список успешно удален"),
            @ApiResponse(responseCode = "404", description = "Список для удаления не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteList(
            @PathVariable Long id
    ) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(listService.deleteList(id));
    }

    @Operation(summary = "Получение страницы всех списков",
            description = "Получение страницы перечня всех списков продуктов. Для получения страницы, включая удаленные продукты," +
                    "используйте параметр showRemoved= \"yes\", для показа только удаленных - showRemoved=\"only\"")
    @GetMapping()
    public ResponseEntity<PageableResponse<ProductListDto>> getAllLists(
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false, defaultValue = "no") String showRemoved
    ) {
        return ResponseEntity.ok(listService.getListPage(page, size, showRemoved));
    }

    @Operation(summary = "Добавление продукта в список",
            description = "Добавление существующего продукта в существующий список продуктов."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Продукт или список не найдены")
    })
    @PostMapping("/{listId}/product/{productId}")
    public ResponseEntity<AppResponse> addToList(
            @Parameter(description = "Идентификатор списка (id)")
            @PathVariable Long listId,
            @Parameter(description = "Идентификатор продукта (id)")
            @PathVariable Long productId) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(listService.addProduct(listId, productId));
    }

    @Operation(summary = "Удаление продукта из списка",
            description = "Удаление существующего продукта из существующий список продуктов."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Продукт или список не найдены")
    })
    @DeleteMapping("/{listId}/product/{productId}")
    public ResponseEntity<AppResponse> removeFromList(
            @Parameter(description = "Идентификатор списка (id)")
            @PathVariable Long listId,
            @Parameter(description = "Идентификатор продукта (id)")
            @PathVariable Long productId) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(listService.removeProduct(listId, productId));
    }

}
