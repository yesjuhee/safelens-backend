package safelens.backend.history.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.history.domain.History.FilterType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EditRequest {

    @NotBlank(message = "imageUuid는 필수입니다")
    private String imageUuid;

    @NotEmpty(message = "regions는 비어있을 수 없습니다")
    @Valid
    private List<RegionInfo> regions;

    @NotNull(message = "filter는 필수입니다")
    private FilterType filter;
}
