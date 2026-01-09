package safelens.backend.history.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import safelens.backend.global.util.ImageUrlUtil;
import safelens.backend.history.domain.Detect;
import safelens.backend.history.domain.History;
import safelens.backend.history.dto.DetectionDetailInfo;
import safelens.backend.history.dto.HistoryDetailInfo;
import safelens.backend.history.dto.HistoryDetailResponse;
import safelens.backend.history.dto.HistoryResponse;
import safelens.backend.history.repository.HistoryRepository;
import safelens.backend.member.domain.Member;
import safelens.backend.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;

    /**
     * 특정 사용자의 전체 편집 히스토리 조회
     */
    @Transactional(readOnly = true)
    public HistoryResponse getHistoriesByMemberId(Long memberId, Member authMember) {
        log.info("히스토리 조회 시작 - memberId: {}", memberId);

        if (!authMember.getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 히스토리만 조회할 수 있습니다");
        }

        // 1. Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId)
                );

        // 2. Member의 모든 History 조회 (최신순)
        List<History> histories = historyRepository.findByMemberOrderByCreatedAtDesc(member);
        log.info("조회된 히스토리 개수: {}", histories.size());

        // 3. DTO 변환
        List<HistoryDetailInfo> historyDetails = histories.stream()
                .map(this::convertToHistoryDetailInfo)
                .collect(Collectors.toList());

        return new HistoryResponse(
                member.getId(),
                member.getNickname(),
                histories.size(),
                historyDetails
        );
    }

    /**
     * 단일 히스토리 상세 조회
     */
    @Transactional(readOnly = true)
    public HistoryDetailResponse getHistoryDetail(Long historyId, Member authMember) {
        log.info("히스토리 상세 조회 시작 - historyId: {}", historyId);

        // 1. History 조회
        History history = historyRepository.findById(historyId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "History not found: " + historyId)
                );

        if (!history.getMember().getId().equals(authMember.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 히스토리만 조회할 수 있습니다");
        }

        // 2. Detection 정보 변환
        List<DetectionDetailInfo> detections = history.getDetects().stream()
                .map(this::convertToDetectionDetailInfo)
                .collect(Collectors.toList());

        // 3. 편집된 이미지 URL 생성 (quality=low 파라미터 추가)
        String editedImageUrl = ImageUrlUtil.toImageUrl(history.getNewUuid()) + "?quality=low";

        log.info("히스토리 상세 조회 완료 - historyId: {}, memberId: {}", historyId, history.getMember().getId());

        return new HistoryDetailResponse(
                history.getId(),
                history.getMember().getId(),
                history.getOldUuid(),
                editedImageUrl,
                history.getFilter(),
                history.getCreatedAt(),
                detections
        );
    }

    /**
     * History 엔티티를 HistoryDetailInfo DTO로 변환
     */
    private HistoryDetailInfo convertToHistoryDetailInfo(History history) {
        // Detection 정보 변환
        List<DetectionDetailInfo> detections = history.getDetects().stream()
                .map(this::convertToDetectionDetailInfo)
                .collect(Collectors.toList());

        // URL 생성 (quality=low 파라미터 추가)
        String oldUrl = ImageUrlUtil.toImageUrl(history.getOldUuid()) + "?quality=low";
        String newUrl = ImageUrlUtil.toImageUrl(history.getNewUuid()) + "?quality=low";

        return new HistoryDetailInfo(
                history.getId(),
                oldUrl,
                newUrl,
                history.getOldUuid(),
                history.getNewUuid(),
                history.getFilter(),
                history.getCreatedAt(),
                detections
        );
    }

    /**
     * Detect 엔티티를 DetectionDetailInfo DTO로 변환
     */
    private DetectionDetailInfo convertToDetectionDetailInfo(Detect detect) {
        return new DetectionDetailInfo(
                detect.getId(),
                detect.getCategory(),
                detect.getX(),
                detect.getY(),
                detect.getWidth(),
                detect.getHeight()
        );
    }
}
