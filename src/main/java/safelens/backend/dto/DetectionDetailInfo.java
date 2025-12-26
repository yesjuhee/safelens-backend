package safelens.backend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.domain.Detect.CategoryType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DetectionDetailInfo {

    private Long detectId;
    private CategoryType category;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
}
