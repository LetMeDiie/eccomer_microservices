package kz.amihady.eccomerce.minio;

import jakarta.annotation.PostConstruct;
import kz.amihady.eccomerce.minio.service.MinioBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private final MinioBucketService minioBucketService;

    @PostConstruct
    public void init() {
        minioBucketService.createBucketIfNotExists();
    }
}
