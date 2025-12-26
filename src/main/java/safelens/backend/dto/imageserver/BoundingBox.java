package safelens.backend.dto.imageserver;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Image Server의 BoundingBox 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoundingBox {

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
}
