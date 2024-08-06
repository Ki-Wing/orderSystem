package com.beyond.ordersystem.product.Service;

import com.beyond.ordersystem.common.serivce.StockInventoryService;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Dto.ProductResDto;
import com.beyond.ordersystem.product.Dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

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
    private final StockInventoryService stockInventoryService;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public ProductService(ProductRepository productRepository, StockInventoryService stockInventoryService, S3Client s3Client) {
        this.productRepository = productRepository;
        this.stockInventoryService = stockInventoryService;
        this.s3Client = s3Client;
    }

    @Transactional
    public Product createProduct(ProductSaveReqDto dto){
        MultipartFile image = dto.getProductimage();
        Product product;
        try{
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:\\Users\\Playdata\\Desktop\\tmp"
                    , product.getId() + "_" + image.getOriginalFilename());
            Files.write(path,bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            product.updateImagePath(path.toString());

//            상품 등록 시, redis에 등록 여부 체그
            if(dto.getName().contains("sale")){
                stockInventoryService.increaseStock(product.getId(),dto.getStockQuantity());
            }
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("이미지 저장 실패");
        }
        return product;
    }

    @Transactional
    public Product awscreateProduct(ProductSaveReqDto dto) {
        MultipartFile image = dto.getProductimage();
        Product product = null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            String fileName = product.getId() + "_" + image.getOriginalFilename();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/", fileName);

//            local pc에 임시 저장
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

//            aws에 pc에 저장된 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromFile(path));
            String s3path =s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName))
                    .toExternalForm();

            product.updateImagePath(s3path); //더티췝~
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




