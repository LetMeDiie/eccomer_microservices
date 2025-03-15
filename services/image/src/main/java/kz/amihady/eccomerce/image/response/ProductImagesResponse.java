package kz.amihady.eccomerce.image.response;

import java.util.List;
import java.util.UUID;

public record ProductImagesResponse(
        UUID productId,
        List<String> imagesUrl
) {
}
