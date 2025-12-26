package safelens.backend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HistoryResponse {

    private Long memberMeId;
    private String nickname;
    private Integer totalHistories;
    private List<HistoryDetailInfo> histories;
}
