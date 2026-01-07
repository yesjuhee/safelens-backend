package safelens.backend.history.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.history.dto.HistoryDetailResponse;
import safelens.backend.history.dto.HistoryResponse;
import safelens.backend.history.service.HistoryService;

@Slf4j
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/{memberId}")
    public ResponseEntity<HistoryResponse> getHistories(@PathVariable Long memberId) {
        log.info("GET /history/{} 요청", memberId);

        try {
            HistoryResponse response = historyService.getHistoriesByMemberId(memberId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("히스토리 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/detail/{historyId}")
    public ResponseEntity<HistoryDetailResponse> getHistoryDetail(@PathVariable Long historyId) {
        log.info("GET /history/detail/{} 요청", historyId);

        try {
            HistoryDetailResponse response = historyService.getHistoryDetail(historyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("히스토리 상세 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
