package safelens.backend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.domain.History.FilterType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EditResponse {

    private Long historyId;
    private String newUrl;
    private String oldUuid;
    private String newUuid;
    private FilterType filter;
    private List<EditedRegionInfo> editedRegions;
    private LocalDateTime createdAt;
}
