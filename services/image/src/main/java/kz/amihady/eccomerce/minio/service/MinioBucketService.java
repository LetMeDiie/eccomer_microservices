package kz.amihady.eccomerce.minio.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import kz.amihady.eccomerce.exceiption.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioBucketService {
    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public void createBucketIfNotExists() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Бакет создан: " + bucketName);
            }
        } catch (Exception e) {
            log.error("Ошибка создания бакета");
            throw new MinioException("Ошибка создания бакета");
        }
    }
}
