package com.community.server.controller;

import com.community.server.body.CommentBody;
import com.community.server.dto.Comment;
import com.community.server.entity.CommentsEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.CommentVisible;
import com.community.server.exception.BadRequestException;
import com.community.server.repository.*;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @Autowired
    private BlackListRepository blackListRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @GetMapping("/id{id}")
    public Object getComments(HttpServletRequest request, @PathVariable Long id) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Specified user is not found!"));

        if(blackListRepository.existsByUserIdAndBanId(id, userId))
            return new ResponseEntity("You are blacklisted!", HttpStatus.BAD_REQUEST);

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
                    commentsEntity.getComment(), (authorEntity.getId().equals(userId) || userId.equals(id)), commentsEntity.getCreateDate())
            );
        }

        return commentList;
    }

    @PostMapping("/id{id}")
    public Object sendComment(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody CommentBody commentBody){

        if(commentBody.getComment().isEmpty())
            return new ResponseEntity("Your comment is empty", HttpStatus.BAD_REQUEST);

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

        CommentsEntity commentsEntity = new CommentsEntity();
        commentsEntity.setComment(commentBody.getComment());
        commentsEntity.setAuthorId(userId);
        commentsEntity.setUserId(id);

        commentsRepository.save(commentsEntity);
        return new ResponseEntity("Your comment is send", HttpStatus.OK);
    }

    @DeleteMapping("/commentId{commentId}")
    public Object deleteComment(HttpServletRequest request, @PathVariable Long commentId){
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        CommentsEntity commentsEntity = commentsRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException("Comment not Found"));

        if(!commentsEntity.getAuthorId().equals(userId) && !commentsEntity.getUserId().equals(userId))
            return new BadRequestException("You do not have access to interact with this comment!");

        commentsRepository.delete(commentsEntity);
        return new ResponseEntity("Comment deleted", HttpStatus.OK);
    }
}
