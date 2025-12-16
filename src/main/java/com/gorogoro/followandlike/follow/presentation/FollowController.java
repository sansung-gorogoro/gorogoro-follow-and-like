package com.gorogoro.followandlike.follow.presentation;

import com.gorogoro.followandlike.follow.application.FollowCommandService;
import com.gorogoro.followandlike.follow.application.FollowQueryService;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.type.NullType;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    // Magic Strings --------------------

    private final String ID = "id";
    private final String FOLLOWER_ID = "followerId";
    private final String FOLLOWEE_ID = "followeeId";
    private final String CREATED_AT = "createdAt";
    private final String PAGE_CURSOR = "pageCursor";
    private final String PAGE_SIZE = "pageSize";
    private final String X_USER_ID = "X-User-Id";

    private final String ERR_MSG_USER_ID_IS_NULL = "user id 누락됨";
    private final String ERR_MSG_USER_ID_OUT_OF_RANGE = "user id 값 범위 오류: 0 또는 음수";
    private final String ERR_MSG_FOLLOWEE_ID_IS_NULL = "followeeId 누락됨";
    private final String ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE = "followeeId 값 범위 오류: 0 또는 음수";
    private final String ERR_MSG_FOLLOW_ID_IS_NULL = "followId 누락됨";
    private final String ERR_MSG_FOLLOW_ID_OUT_OF_RANGE = "followId 값 범위 오류: 0 또는 음수";

    // Dependencies --------------------

    private final FollowCommandService followCommandService;
    private final FollowQueryService followQueryService;

    public FollowController(FollowCommandService followCommandService, FollowQueryService followQueryService) {
        this.followCommandService = followCommandService;
        this.followQueryService = followQueryService;
    }

    // Command Handlers --------------------

    @PostMapping
    public ResponseEntity<Void> follow(
            @RequestHeader(name = X_USER_ID)
            @NotNull(message = ERR_MSG_USER_ID_IS_NULL)
            @Positive(message = ERR_MSG_USER_ID_OUT_OF_RANGE)
            Long userId,

            @RequestParam(name = FOLLOWEE_ID)
            @NotNull(message = ERR_MSG_FOLLOWEE_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE)
            Long followeeId
    ) {
        followCommandService.follow(new WhoFollowsWhom(userId, followeeId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unfollow(
            @RequestHeader(name = X_USER_ID)
            @NotNull(message = ERR_MSG_USER_ID_IS_NULL)
            @Positive(message = ERR_MSG_USER_ID_OUT_OF_RANGE)
            Long userId,

            @RequestParam(name = FOLLOWEE_ID)
            @NotNull(message = ERR_MSG_FOLLOWEE_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE)
            Long followeeId
    ) {
        followCommandService.unfollow(new WhoFollowsWhom(userId, followeeId));
        return ResponseEntity.ok().build();
    }

    // Query Handlers --------------------


}
