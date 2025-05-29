package com.sweetspot.server.post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sweetspot.server.comment.CommentRepository;
import com.sweetspot.server.comment.DTO.CommentDetailDTO;
import com.sweetspot.server.pin.PinRepository;
import com.sweetspot.server.pin.PinInfoDTO;
import com.sweetspot.server.post.DTO.MapPostListResponseDTO;
import com.sweetspot.server.post.DTO.MapPostPopularResponseDTO;
import com.sweetspot.server.post.DTO.MapPostRequestDTO;
import com.sweetspot.server.post.DTO.MapPostResponseDTO;
import com.sweetspot.server.post.image.PostImageDetailDTO;
import com.sweetspot.server.post.image.PostImageRepository;
import com.sweetspot.server.post.DTO.MapPostDetailResponseDTO;
import com.sweetspot.server.post.like.PostLikeEntity;
import com.sweetspot.server.post.like.PostLikeRepository;
import com.sweetspot.server.user.UserRepository;

@Service
public class MapPostService {
    private final MapPostRepository mapPostRepository;
    private final UserRepository userRepository;
    private final PinRepository pinRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PostImageRepository postImageRepository;

    public MapPostService(
        MapPostRepository mapPostRepository,
        UserRepository userRepository,
        PinRepository pinRepository,
        PostLikeRepository postLikeRepository,
        CommentRepository commentRepository,
        PostImageRepository postImageRepository
    ) {
        this.mapPostRepository = mapPostRepository;
        this.userRepository = userRepository;
        this.pinRepository = pinRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.postImageRepository = postImageRepository;
    }

    // 게시글 저장
    public MapPostResponseDTO createPost(MapPostRequestDTO dto) {
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
    private MapPostResponseDTO toDto(MapPostEntity post) {
        MapPostResponseDTO dto = new MapPostResponseDTO();
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

        // 게시글의 좋아요 수 반영
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
    @Transactional(readOnly = true)
    public List<MapPostPopularResponseDTO> getPopularPostsOfDay() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<MapPostEntity> popularPosts = mapPostRepository.findTop10PopularPostsOfDay(startOfDay, endOfDay);

        return popularPosts.stream().map(post -> {
            MapPostPopularResponseDTO dto = new MapPostPopularResponseDTO();
            dto.setPostId(post.getPostId());
            dto.setUserId(post.getUserId());
            dto.setTitle(post.getTitle());
            dto.setLikes(post.getLikes());
            dto.setUpdatedAt(post.getUpdatedAt());

            // 작성자 닉네임
            String nickname = userRepository.findById(post.getUserId())
                .map(user -> user.getNickname())
                .orElse("탈퇴한 사용자");
            dto.setNickname(nickname);

            // 핀 정보
            pinRepository.findById(post.getPinId()).ifPresent(pin -> {
                PinInfoDTO pinDto = new PinInfoDTO();
                pinDto.setPinId(pin.getPinId());
                pinDto.setLatitude(pin.getLatitude());
                pinDto.setLongitude(pin.getLongitude());
                dto.setPins(List.of(pinDto)); // 리스트로 감싸서 넣기
            });

            return dto;
        }).collect(Collectors.toList());
    }


    // 전체 게시글 리스트 조회
    @Transactional(readOnly = true)
    public List<MapPostListResponseDTO> getAllPosts() {
        List<MapPostEntity> posts = mapPostRepository.findAll();

        return posts.stream().map(post -> {
            // 작성자 정보 조회
            return userRepository.findById(post.getUserId())
                .map(user -> new MapPostListResponseDTO(
                    post.getPostId(),
                    post.getTitle(),
                    post.getUpdatedAt(),
                    post.getLikes(),
                    user.getUserId(),
                    user.getNickname()
                )).orElse(null); // user가 없을 경우 null 반환 (또는 예외 처리 가능)
        }).filter(dto -> dto != null) // null 제거
        .collect(Collectors.toList());
    }

    //게시글 조회
    @Transactional(readOnly = true)
    public MapPostDetailResponseDTO getPostById(Long postId) {
        MapPostEntity post = mapPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        String nickname = userRepository.findById(post.getUserId())
            .map(user -> user.getNickname())
            .orElse("탈퇴한 사용자");

        List<CommentDetailDTO> commentDtos = commentRepository.findByPostId(postId)
            .stream()
            .map(comment -> {
                String commentNickname = userRepository.findById(comment.getUserId())
                    .map(user -> user.getNickname())
                    .orElse("탈퇴한 사용자");

                CommentDetailDTO dto = new CommentDetailDTO();
                dto.setCommentId(comment.getCommentId());
                dto.setUserId(comment.getUserId());
                dto.setNickname(commentNickname);
                dto.setContent(comment.getContent());
                dto.setUpdatedAt(comment.getUpdatedAt());
                dto.setLikes(comment.getLikes());
                return dto;
            })
            .collect(Collectors.toList());

        // 이미지 정보 가져오기
        List<PostImageDetailDTO> imageDtos = postImageRepository.findByPostId(postId).stream()
            .map(img -> {
                PostImageDetailDTO dto = new PostImageDetailDTO();
                dto.setImageId(img.getImageId());
                dto.setImageUrl(img.getImageUrl());
                return dto;
            })
            .collect(Collectors.toList());

        MapPostDetailResponseDTO dto = new MapPostDetailResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setNickname(nickname);
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikes(post.getLikes());
        dto.setComments(commentDtos);
        dto.setImages(imageDtos); // 🔽 이미지 포함

        return dto;
    }
}
