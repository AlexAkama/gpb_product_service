package org.example.productservice.services;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.DeleteResponse;
import org.example.productservice.dto.PageableResponse;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.exceptions.BadRequestException;
import org.example.productservice.exceptions.NotFoundException;
import org.example.productservice.model.Product;
import org.example.productservice.repositories.ProductRepo;
import org.example.productservice.utils.PageAndSizePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.productservice.model.EntityStatus.ACTIVE;
import static org.example.productservice.model.EntityStatus.REMOVED;
import static org.example.productservice.utils.AppUtils.getPageAndSizeToPageable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;

    @Value("${limit.page.size}")
    private long defaultPageSize;

    @Value("${limit.product.name.length}")
    private int nameLengthLimit;

     /**
      * В параметры есть смысл выносить значения, которые мы хотим менять без внесения изменений в код.
      * В данном случае этот принцип нарушен - аналогичные значения захардкожены в SQL скриптах.
      */
    @Value("${limit.product.description.length}")
    private int descriptionLengthLimit;

    /**
     *  Для маппинга DTO в Entity и обратно лучше использовать отдельный класс Mapper,
     *  либо еще лучше библиотеку Mapstruct - https://www.baeldung.com/mapstruct
     */
    public ProductDto getProduct(Long id) {
        Product product = getActiveById(id);
        return new ProductDto(product);
    }

    /** 
     * Валидацию лучше выносить в отдельный класс Validator, либо делать на уровне DTO. 
     * Например - https://code4fun.ru/programming/spring-boot-dto-validation.html
     */
    public ProductDto addProduct(ProductDto request) {
        if (request.getName() == null
                || request.getName().isBlank()) throw new BadRequestException("Не указано имя продукта");
        Product product = create(request);
        productRepo.saveAndFlush(product);
        return new ProductDto(product);
    }

    
    /**
     * 1. Слишком сложный алгоритм обновления. Что если в Product будет не 3 поля, а 300?
     *    Придется добавлять по методу на обновление каждого поля?
     *    Намного удобнее это можно решить с помощью Mapstruct - https://www.baeldung.com/spring-data-partial-update
     * 2. По философии HTTP, PUT подразумевает передачу и обновление всех полей в сущности.
     *    Если в каком-то поле DTO, переданном в PUT запросе передан null, значит нужно сбросить поле в null.
     *    Если мы хотим обновлять только not null поля, то корректнее использовать PATCH.
     */
    public ProductDto updateProduct(ProductDto request) {
        if (request.getId() == 0) throw new BadRequestException("Не указан id продукта");
        Product product = getById(request.getId());
        update(product, request);
        productRepo.saveAndFlush(product);
        return new ProductDto(product);
    }

    public DeleteResponse deleteProduct(Long productId) {
        Product product = getById(productId);
        if (product.getStatus() == REMOVED)
            throw new BadRequestException(String.format("Продукт id=%d уже удален", productId));
        product.setStatus(REMOVED);
        productRepo.save(product);
        return new DeleteResponse("Продукт id=" + productId);
    }

    public PageableResponse<ProductDto> getProductPage(Long page, Long size, String showRemoved) {
        long total;
        if (showRemoved.equals("yes")) total = productRepo.count();
        else if (showRemoved.equals("only")) total = productRepo.countProductsByStatus(REMOVED);
        else total = productRepo.countProductsByStatus(ACTIVE);

        PageAndSizePair pair = getPageAndSizeToPageable(page, size, total, defaultPageSize);
        Pageable pageable = PageRequest.of(pair.getPage(), pair.getSize(), Sort.by("id"));

        Page<Product> productPage;
        if (showRemoved.equals("yes")) productPage = productRepo.findAll(pageable);
        else if (showRemoved.equals("only")) productPage = productRepo.findAllByStatus(REMOVED, pageable);
        else productPage = productRepo.findAllByStatus(ACTIVE, pageable);

        List<ProductDto> list = productPage.get().map(ProductDto::new).collect(Collectors.toList());

        return PageableResponse.create(total, pair.getPage(), pair.getSize(), list);
    }

    public Product getById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт", id));
    }

    public Product getActiveById(Long id) {
        return productRepo.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(() -> new NotFoundException("Продукт", id));
    }

    private Product create(ProductDto request) {
        Product product = new Product();
        update(product, request);
        return product;
    }

    private void update(Product product, ProductDto request) {
        if (request.getName() != null) updateName(product, request.getName());
        if (request.getDescription() != null) updateDescription(product, request.getDescription());
        if (request.getKilocalories() != null) updateKilocalories(product, request.getKilocalories());
    }

    private void updateName(Product product, String name) {
        throwExceptionIfNameAlreadyExists(name);
        if (name.length() > nameLengthLimit)
            throw new BadRequestException(
                    String.format("Превышен лимит длинны имени (Ограничение=%d)", nameLengthLimit)
            );
        product.setName(name);
    }

    private void updateDescription(Product product, String description) {
        if (description.length() > descriptionLengthLimit)
            throw new BadRequestException(
                    String.format("Превышен лимит длинны описания (Ограничение=%d)", descriptionLengthLimit)
            );
        product.setDescription(description);
    }

    private void updateKilocalories(Product product, Integer kcal) {
        if (kcal < -1) throw new BadRequestException("Количество килокалорий должно быть положительным");
        product.setKilocalories(kcal);
    }

    private void throwExceptionIfNameAlreadyExists(String name) {
        if (productRepo.existsByNameAndStatus(name, ACTIVE))
            throw new BadRequestException(String.format("Продукт с именем:%s уже существует", name));
    }

}
