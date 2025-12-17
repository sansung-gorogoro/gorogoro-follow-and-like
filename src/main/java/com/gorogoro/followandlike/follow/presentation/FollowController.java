package com.gorogoro.followandlike.follow.presentation;

import com.gorogoro.followandlike.common.application.dto.CursorPageResult;
import com.gorogoro.followandlike.follow.application.FollowCommandService;
import com.gorogoro.followandlike.follow.application.FollowQueryService;
import com.gorogoro.followandlike.follow.application.dto.FollowQueryResult;
import com.gorogoro.followandlike.follow.application.dto.FollowersCursorPageQuery;
import com.gorogoro.followandlike.follow.application.dto.FollowingsCursorPageQuery;
import com.gorogoro.followandlike.follow.application.dto.RequiredNonNegativeId;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.presentation.request.FindByFollowerIdAndFolloweeIdRequest;
import com.gorogoro.followandlike.follow.presentation.request.FollowersCursorPageRequest;
import com.gorogoro.followandlike.follow.presentation.request.FollowingsCursorPageRequest;
import com.gorogoro.followandlike.follow.presentation.response.CountResponse;
import com.gorogoro.followandlike.follow.presentation.response.FollowResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gorogoro.followandlike.common.presentation.CommonHttpConstants.ERR_MSG_USER_ID_IS_NULL;
import static com.gorogoro.followandlike.common.presentation.CommonHttpConstants.ERR_MSG_USER_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.common.presentation.CommonHttpConstants.PARAM_NAME_X_USER_ID;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWEE_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOW_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOW_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.PARAM_NAME_FOLLOWEE_ID;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.PARAM_NAME_FOLLOWER_ID;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.PARAM_NAME_FOLLOW_ID;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

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
            @RequestHeader(name = PARAM_NAME_X_USER_ID)
            @NotNull(message = ERR_MSG_USER_ID_IS_NULL)
            @Positive(message = ERR_MSG_USER_ID_OUT_OF_RANGE)
            Long userId,

            @RequestParam(name = PARAM_NAME_FOLLOWEE_ID)
            @NotNull(message = ERR_MSG_FOLLOWEE_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE)
            Long followeeId
    ) {
        followCommandService.follow(new WhoFollowsWhom(userId, followeeId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unfollow(
            @RequestHeader(name = PARAM_NAME_X_USER_ID)
            @NotNull(message = ERR_MSG_USER_ID_IS_NULL)
            @Positive(message = ERR_MSG_USER_ID_OUT_OF_RANGE)
            Long userId,

            @RequestParam(name = PARAM_NAME_FOLLOWEE_ID)
            @NotNull(message = ERR_MSG_FOLLOWEE_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE)
            Long followeeId
    ) {
        followCommandService.unfollow(new WhoFollowsWhom(userId, followeeId));
        return ResponseEntity.ok().build();
    }

    // Query Handlers --------------------

    @GetMapping("/{id}")
    public ResponseEntity<FollowResponse> findById(
            @PathVariable(name = PARAM_NAME_FOLLOW_ID)
            @NotNull(message = ERR_MSG_FOLLOW_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOW_ID_OUT_OF_RANGE)
            Long id
    ) {
        FollowQueryResult queryResult = followQueryService.findById(new RequiredNonNegativeId(id));
        FollowResponse body = FollowResponse.from(queryResult);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    public ResponseEntity<FollowResponse> findByFollowerIdAndFolloweeId(
            @Valid
            @ModelAttribute
            FindByFollowerIdAndFolloweeIdRequest request
    ) {
        FollowQueryResult queryResult = followQueryService.findByFollowerIdAndFolloweeId(
                new WhoFollowsWhom(request.followerId(), request.followeeId())
        );

        if (queryResult == null) {
            return ResponseEntity.ok().build();
        }

        FollowResponse body = FollowResponse.from(queryResult);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/following-count")
    public ResponseEntity<CountResponse> countFollowings(
            @RequestParam(name = PARAM_NAME_FOLLOWER_ID)
            @NotNull(message = ERR_MSG_FOLLOWER_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE)
            Long followerId
    ) {
        CountResponse body = CountResponse.of(
                followQueryService.countByFollowerId(
                        new RequiredNonNegativeId(followerId)
                )
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/follower-count")
    public ResponseEntity<CountResponse> countFollowers(
            @RequestParam(name = PARAM_NAME_FOLLOWEE_ID)
            @NotNull(message = ERR_MSG_FOLLOWER_ID_IS_NULL)
            @Positive(message = ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE)
            Long followeeId
    ) {
        CountResponse body = CountResponse.of(
                followQueryService.countByFolloweeId(
                        new RequiredNonNegativeId(followeeId)
                )
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/followings")
    public ResponseEntity<CursorPageResult<FollowQueryResult>> findFollowings(
            @Valid
            @ModelAttribute
            FollowingsCursorPageRequest request
    ) {
        CursorPageResult<FollowQueryResult> body = followQueryService.findFollowings(
                new FollowingsCursorPageQuery(
                        request.followerId(),
                        request.pageCursor(),
                        request.pageSize()
                )
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/followers")
    public ResponseEntity<CursorPageResult<FollowQueryResult>> findFollowers(
            @Valid
            @ModelAttribute
            FollowersCursorPageRequest request
    ) {
        CursorPageResult<FollowQueryResult> body = followQueryService.findFollowers(
                new FollowersCursorPageQuery(
                        request.followeeId(),
                        request.pageCursor(),
                        request.pageSize()
                )
        );

        return ResponseEntity.ok(body);
    }
}
