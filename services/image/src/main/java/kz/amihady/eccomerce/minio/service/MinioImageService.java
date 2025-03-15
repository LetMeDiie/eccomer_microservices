package kz.amihady.eccomerce.minio.service;


import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import kz.amihady.eccomerce.exceiption.MinioException;
import kz.amihady.eccomerce.minio.config.MinioProperties;
import kz.amihady.eccomerce.redis.FailedDeletesCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE,makeFinal = true)
public class MinioImageService {
     MinioClient minioClient;
     MinioProperties minioProperties;

     FailedDeletesCacheService failedDeletesCacheService;

    public void uploadImage(MultipartFile file, UUID imageId) {
        try {
            log.info("Попытка добавить файл в облако id:"+imageId);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(imageId.toString())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Файл загружен в облако: " + imageId);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла в облако Id:"+imageId, e);
            throw new MinioException("Ошибка загрузки файла в облако Id:"+imageId);
        }
    }


    @Async
    public void deleteImage(UUID id) {
        try {
            log.info("Попытка удалить фото из облака id:" +id);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(id.toString())
                            .build()
            );
            log.info("Файл удален из облака: " + id);
        } catch (Exception e) {
            log.error("Ошибка удаления файла из облака", e);
            log.info("Попытка добавить UUID файла в Redis");

            failedDeletesCacheService.addUUID(id);
        }
    }

    public String getImageUrl(UUID id) {
        try {
            log.info("Попытка получить временную ссылку на фото с id:"+id);
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(id.toString())
                            .method(Method.GET)
                            .expiry(minioProperties.getUrlExpiration())
                            .build());
            log.info("Временная ссылка для файла:"+id+" было успешно создано. " + url);
            return url;
        } catch (Exception e) {
            log.error("Ошибка при генерации ссылки для файла из облака: " + id, e);
            throw new MinioException("Ошибка при получении ссылки на файл из облака");
        }
    }
}
