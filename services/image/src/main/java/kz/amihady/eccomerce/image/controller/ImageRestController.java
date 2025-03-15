package kz.amihady.eccomerce.image.controller;


import kz.amihady.eccomerce.image.response.ProductImagesResponse;
import kz.amihady.eccomerce.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-images")
public class ImageRestController {

    private final ImageService imageService;


    @PostMapping("/{productId}")
    public ResponseEntity<UUID> uploadImage(@RequestParam("file") MultipartFile file,
                                            @PathVariable("productId") String productId) {
        UUID imageId = imageService.saveImage(file, productId);
        return ResponseEntity.ok(imageId);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable("imageId") String imageId){
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductImagesResponse> getImagesForProduct(
            @PathVariable("productId") String productId){
        return ResponseEntity.ok(imageService.getImagesForProduct(productId));
    }
}
