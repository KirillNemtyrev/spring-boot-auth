package com.community.server.controller;

import com.community.server.dto.Comment;
import com.community.server.entity.CommentsEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.CommentVisible;
import com.community.server.repository.ChatRoomRepository;
import com.community.server.repository.CommentsRepository;
import com.community.server.repository.ExclusionCommentRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ExclusionCommentRepository exclusionCommentRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @GetMapping("/id{id}")
    public Object getMyComments(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Specified user is not found!"));

        if(((userEntity.getCommentVisible() == CommentVisible.FRIEND_VISION && !chatRoomRepository.existsBySenderIdOrRecipientId(userId, id)) ||
                (userEntity.getCommentVisible() == CommentVisible.EXCLUSION_VISION && !exclusionCommentRepository.existsByUserIdAndExclusionId(id, userId)) ||
                (userEntity.getCommentVisible() == CommentVisible.NO_VISION)) && !userId.equals(id))
            return new ResponseEntity("Comments close!", HttpStatus.BAD_REQUEST);

        List<CommentsEntity> commentsEntities = commentsRepository.findByUserId(id);
        List<Comment> commentList = new ArrayList<>(commentsEntities.size());

        for (CommentsEntity commentsEntity : commentsEntities) {

            UserEntity authorEntity = userRepository.findById(commentsEntity.getAuthorId()).orElseThrow(
                    () -> new UsernameNotFoundException("User is not found!"));

            commentList.add(new Comment(
                    commentsEntity.getId(), authorEntity.getId(), authorEntity.getName(),
                    authorEntity.getUsername(), authorEntity.getFileNameAvatar(),
                    commentsEntity.getComment(), commentsEntity.getCreateDate())
            );
        }

        return commentList;
    }

}
