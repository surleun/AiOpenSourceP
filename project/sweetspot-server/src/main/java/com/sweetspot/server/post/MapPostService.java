package com.sweetspot.server.post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sweetspot.server.pin.PinRepository;
import com.sweetspot.server.post.like.PostLikeEntity;
import com.sweetspot.server.post.like.PostLikeRepository;
import com.sweetspot.server.user.UserRepository;

@Service
public class MapPostService {
    private final MapPostRepository mapPostRepository;
    private final UserRepository userRepository;
    private final PinRepository pinRepository;
    private final PostLikeRepository postLikeRepository;

    public MapPostService(
        MapPostRepository mapPostRepository,
        UserRepository userRepository,
        PinRepository pinRepository,
        PostLikeRepository postLikeRepository
    ) {
        this.mapPostRepository = mapPostRepository;
        this.userRepository = userRepository;
        this.pinRepository = pinRepository;
        this.postLikeRepository = postLikeRepository;
    }

    // 게시글 저장
    public MapPostResponseDto createPost(MapPostRequestDto dto) {
        // 유저 존재 여부 확인
        if (!userRepository.existsById(dto.getUserId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 핀 존재 여부 확인
        if (!pinRepository.existsById(dto.getPinId())) {
            throw new IllegalArgumentException("존재하지 않는 핀입니다.");
        }

        MapPostEntity post = new MapPostEntity();
        post.setUserId(dto.getUserId());
        post.setPinId(dto.getPinId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        MapPostEntity saved = mapPostRepository.save(post);

        return toDto(saved);
    }

    // 게시글 삭제
    public boolean deletePost(Long postId, Long userId) {
        Optional<MapPostEntity> postOpt = mapPostRepository.findById(postId);

        if (postOpt.isPresent() && postOpt.get().getUserId().equals(userId)) {
            mapPostRepository.deleteById(postId);
            return true;
        }
        return false;
    }

    // Entity → Response DTO 변환
    private MapPostResponseDto toDto(MapPostEntity post) {
        MapPostResponseDto dto = new MapPostResponseDto();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setPinId(post.getPinId()); // 추가된 필드 반영
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikes(post.getLikes());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }

    // 게시글 좋아요
    public boolean likePost(Long userId, Long postId) {
        // 이미 좋아요 누른 경우
        if (postLikeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return false;
        }

        PostLikeEntity like = new PostLikeEntity();
        like.setUserId(userId);
        like.setPostId(postId);
        postLikeRepository.save(like);

        // 게시글의 좋아요 수 반영 (선택 사항)
        mapPostRepository.findById(postId).ifPresent(post -> {
            post.setLikes(post.getLikes() + 1);
            mapPostRepository.save(post);
        });

        return true;
    }

    //좋아요 삭제
    @Transactional
    public boolean unlikePost(Long userId, Long postId) {
        Optional<PostLikeEntity> likeOpt = postLikeRepository.findByUserIdAndPostId(userId, postId);
        
        if (likeOpt.isEmpty()) {
            return false; // 좋아요 기록 없음
        }

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);

        // 게시글 좋아요 수 감소
        mapPostRepository.findById(postId).ifPresent(post -> {
            post.setLikes(Math.max(0, post.getLikes() - 1)); // 0 이하로 떨어지지 않도록
            mapPostRepository.save(post);
        });

        return true;
    }

    //인기 게시글 조회
    public List<MapPostResponseDto> getTop10PopularPostsToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 오늘 00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // 오늘 23:59:59.999999999

        List<MapPostEntity> topPosts = mapPostRepository.findTop10PopularPostsOfDay(startOfDay, endOfDay);

        return topPosts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
