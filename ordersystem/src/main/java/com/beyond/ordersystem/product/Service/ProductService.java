package com.beyond.ordersystem.product.Service;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Dto.ProductResDto;
import com.beyond.ordersystem.product.Dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(ProductSaveReqDto dto) {
        MultipartFile image = dto.getProductimage();
        Product product=null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",
                    product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString()); //더티췝~
        }catch (IOException e){
            throw new RuntimeException("[Failed] Image Save");
        }
        return product;

    }

    @Transactional
    public Product awscreateProduct(ProductSaveReqDto dto) {
        MultipartFile image = dto.getProductimage();
        Product product=null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",
                    product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString()); //더티췝~
        }catch (IOException e){
            throw new RuntimeException("[Failed] Image Save");
        }
        return product;

    }







    public Page<ProductResDto> listProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
//        Page<MemberResDto> memberResDtos = members.map(a->a.fromEntity());
        return products.map(a->a.fromEntity());
    }
}




