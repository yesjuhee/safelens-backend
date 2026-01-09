package safelens.backend.member.dto;

import safelens.backend.member.domain.Member;

public record UserResponse(
        long id,
        String nickname
) {

    public UserResponse(final Member member) {
        this(member.getId(), member.getNickname());
    }
}
