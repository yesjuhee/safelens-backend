package safelens.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.domain.Detect.CategoryType;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DetectRequest {

    @NotBlank(message = "imageUuid는 필수입니다")
    private String imageUuid;

    @NotEmpty(message = "detectTargets는 비어있을 수 없습니다")
    private List<CategoryType> detectTargets;
}
