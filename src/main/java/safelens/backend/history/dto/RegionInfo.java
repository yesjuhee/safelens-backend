package safelens.backend.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.history.domain.Detect.CategoryType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegionInfo {

    @NotNull(message = "category는 필수입니다")
    private CategoryType category;

    @NotBlank(message = "pii_type은 필수입니다")
    @JsonProperty("pii_type")
    private String piiType;

    @NotNull(message = "x 좌표는 필수입니다")
    private Integer x;

    @NotNull(message = "y 좌표는 필수입니다")
    private Integer y;

    @NotNull(message = "width는 필수입니다")
    private Integer width;

    @NotNull(message = "height는 필수입니다")
    private Integer height;
}
