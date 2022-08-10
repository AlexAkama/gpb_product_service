package org.example.productservice.services;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.AppResponse;
import org.example.productservice.dto.DeleteResponse;
import org.example.productservice.dto.PageableResponse;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.ProductListDto;
import org.example.productservice.exceptions.BadRequestException;
import org.example.productservice.exceptions.NotFoundException;
import org.example.productservice.model.Product;
import org.example.productservice.model.ProductList;
import org.example.productservice.repositories.ProductListRepo;
import org.example.productservice.utils.PageAndSizePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.productservice.model.EntityStatus.ACTIVE;
import static org.example.productservice.model.EntityStatus.REMOVED;
import static org.example.productservice.utils.AppUtils.getPageAndSizeToPageable;

@Service
@RequiredArgsConstructor
public class ProductListService {

    private final ProductListRepo productListRepo;
    private final ProductService productService;

    @Value("${limit.list.size}")
    private int listSizeLimit;

    @Value("${limit.page.size}")
    private long defaultPageSize;

    @Value("${limit.list.name.length}")
    private int nameLengthLimit;

    public ProductListDto getList(Long listId) {
        ProductList productList = getActiveById(listId);
        Set<Product> productSet = productList.getProducts();
        ProductListDto result = new ProductListDto(productList);
        if (productSet.isEmpty()) return result;
        List<ProductDto> productDtoList = productSet.stream()
                .filter(product -> product.getStatus() == ACTIVE)
                .sorted(Comparator.comparingLong(Product::getId))
                .map(ProductDto::new)
                .collect(Collectors.toList());
        long sum = productSet.stream().mapToInt(Product::getKilocalories).sum();
        result.setProductDtoList(productDtoList);
        result.setCaloriesTotal(sum);
        return result;
    }

    public ProductListDto addList(ProductListDto request) {
        if (request.getName() == null || request.getName().isBlank())
            throw new BadRequestException("Не указано имя списка");
        ProductList productList = create(request);
        productListRepo.saveAndFlush(productList);
        return new ProductListDto(productList);
    }

    public ProductListDto updateList(ProductListDto request) {
        if (request.getId() == 0) throw new BadRequestException("Не указан id списка");
        ProductList productList = getActiveById(request.getId());
        update(productList, request);
        productListRepo.save(productList);
        return new ProductListDto(productList);
    }

    public DeleteResponse deleteList(Long listId) {
        ProductList productList = getById(listId);
        if (productList.getStatus() == REMOVED)
            throw new BadRequestException(String.format("Список продуктов id=%d уже удален", listId));
        productList.setStatus(REMOVED);
        productListRepo.save(productList);
        return new DeleteResponse("Список продуктов id=" + listId);
    }

    public PageableResponse<ProductListDto> getListPage(Long page, Long size, String showRemoved) {
        long total;
        if (showRemoved.equals("yes")) total = productListRepo.count();
        else if (showRemoved.equals("only")) total = productListRepo.countProductListByStatus(REMOVED);
        else total = productListRepo.countProductListByStatus(ACTIVE);

        PageAndSizePair pair = getPageAndSizeToPageable(page, size, total, defaultPageSize);
        Pageable pageable = PageRequest.of(pair.getPage(), pair.getSize());

        Page<ProductList> productListPage;
        if (showRemoved.equals("yes")) productListPage = productListRepo.findAll(pageable);
        else if (showRemoved.equals("only")) productListPage = productListRepo.findAllByStatus(REMOVED, pageable);
        else productListPage = productListRepo.findAllByStatus(ACTIVE, pageable);

        List<ProductListDto> list = productListPage.get()
                .map(ProductListDto::new)
                .collect(Collectors.toList());

        return PageableResponse.create(total, pair.getPage(), pair.getSize(), list);
    }

    public AppResponse addProduct(Long listId, Long productId) {
        ProductList list = getActiveById(listId);
        Product product = productService.getActiveById(productId);
        Set<Product> productSet = list.getProducts();
        long size = productSet.stream().filter(p -> p.getStatus() == ACTIVE).count();
        if (listSizeLimit > 0 && size > listSizeLimit)
            throw new BadRequestException(
                    String.format("В список id=%d больше нельзя добавить продукт (Ограничение=%d)"
                            , listId, listSizeLimit));
        if (productSet.contains(product))
            throw new BadRequestException(String.format("Продукт id=%d уже есть в списке id=%d", productId, listId));
        productSet.add(product);
        productListRepo.save(list);
        return new AppResponse(String.format("Продукт id=%d добавлен в список id=%d", productId, listId));
    }

    public AppResponse removeProduct(Long listId, Long productId) {
        ProductList productList = getById(listId);
        Product product = productService.getById(productId);
        Set<Product> products = productList.getProducts();
        if (!products.contains(product))
            throw new BadRequestException(String.format("Продукт id=%d отсутствует в списке id=%d", productId, listId));
        products.remove(product);
        productListRepo.save(productList);
        return new AppResponse(String.format("Продукт id=%d удален из списка id=%d", productId, listId));

    }

    private ProductList getById(Long id) {
        return productListRepo.findById(id).orElseThrow(() -> new NotFoundException("Список продуктов", id));
    }

    private ProductList getActiveById(Long id) {
        return productListRepo.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(() -> new NotFoundException("Список продуктов", id));
    }

    private ProductList create(ProductListDto request) {
        ProductList productList = new ProductList();
        update(productList, request);
        return productList;
    }

    private void update(ProductList productList, ProductListDto request) {
        if (request.getName() != null) updateName(productList, request.getName());
    }

    private void updateName(ProductList productList, String name) {
        throwExceptionIfNameAlreadyExists(name);
        if (name.length() > nameLengthLimit)
            throw new BadRequestException(
                    String.format("Превышен лимит длинны имени (Ограничение=%d)", nameLengthLimit)
            );
        productList.setName(name);
    }

    private void throwExceptionIfNameAlreadyExists(String name) {
        if (productListRepo.existsByNameAndStatus(name, ACTIVE))
            throw new BadRequestException(String.format("Список с именем:'%s' уже существует", name));
    }


}
