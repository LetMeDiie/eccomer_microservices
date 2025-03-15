package kz.amihady.eccomerce.image.service;

import kz.amihady.eccomerce.exceiption.BusinessException;
import kz.amihady.eccomerce.exceiption.EntityNotFoundException;
import kz.amihady.eccomerce.image.entity.Image;
import kz.amihady.eccomerce.image.repo.ImageRepository;
import kz.amihady.eccomerce.image.response.ProductImagesResponse;
import kz.amihady.eccomerce.image.validation.ImageFileValidator;
import kz.amihady.eccomerce.minio.service.MinioImageService;
import kz.amihady.eccomerce.product.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageService {
    MinioImageService minioImageService;
    ImageRepository imageRepository;
    ProductRepository productRepository;
    ImageFileValidator validator;

    @Transactional
    public UUID saveImage(MultipartFile file, String productId) {
        log.info("Запрос на добавление нового фото для продукта:" + productId);
        UUID productUUID = getUUIDFromString(productId);

        var product = productRepository.findById(productUUID)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден id:" + productId));

        validator.validate(file);

        Image image = new Image();
        image.setProduct(product);
        imageRepository.save(image);
        log.info("Новое фото было добавлено в БД , ID:" + image.getId());

        minioImageService.uploadImage(file, image.getId());
        evictProductImagesCache(productId);

        return image.getId();
    }

    public void deleteImage(String imageId) {
        log.info("Запрос на удаление изображения. ID: {}", imageId);

        UUID imageUUID = getUUIDFromString(imageId);
        var image = imageRepository.findById(imageUUID)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить несуществующее изображение. ID: {}", imageId);
                    return new EntityNotFoundException("Изображение не найдено.");
                });

        imageRepository.delete(image);
        log.info("Изображение с ID {} удалено из БД.", imageUUID);

        minioImageService.deleteImage(imageUUID);
        evictProductImagesCache(image.getProduct().getId().toString());
    }


    @Cacheable(value = "productImages", key = "#productId")
    public ProductImagesResponse getImagesForProduct(String productId) {
        log.info("Запрос на получение изображении для продукта: " + productId);
        UUID productUUID = getUUIDFromString(productId);

        var product = productRepository.findById(productUUID)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не существует: " + productId));

        List<String> imageUrls = new ArrayList<>();

        for (Image image : product.getImageList()) {
            imageUrls.add(minioImageService.getImageUrl(image.getId()));
        }

        return new ProductImagesResponse(productUUID, imageUrls);
    }

    @CacheEvict(value = "productImages", key = "#productId")
    public void evictProductImagesCache(String productId) {
        log.info("Очистка кеша productImages для продукта: {}", productId);
    }


    private UUID getUUIDFromString(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Некорректный формат UUID: " + id);
        }
    }
}