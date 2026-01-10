package safelens.backend.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageUrlUtil.class);
    private static String imageServerUrl;

    @Value("${image-server.url}")
    public void setImageServerUrl(String url) {
        ImageUrlUtil.imageServerUrl = url;
    }

    /**
     * UUID를 이미지 URL로 변환
     *
     * @param uuid 이미지 UUID
     * @return 이미지 전체 URL
     */
    public static String toImageUrl(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            return null;
        }
        String baseUrl = imageServerUrl.endsWith("/") ? imageServerUrl : imageServerUrl + "/";
        log.debug("ImageUrlUtil - baseUrl: {}, uuid: {}", baseUrl, uuid);
        
        return baseUrl + "download/" + uuid;
    }
}
