package com.community.server.controller;

import com.community.server.dto.StickerDto;
import com.community.server.dto.StickersListDto;
import com.community.server.entity.StickerEntity;
import com.community.server.entity.StickerSubscribeEntity;
import com.community.server.entity.StickersListEntity;
import com.community.server.repository.*;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sticker")
public class StickerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private StickerListRepository stickerListRepository;

    @Autowired
    private StickerSubscribeRepository stickerSubscribeRepository;

    @GetMapping("/subscribes")
    public Object getSubscribeStickers(HttpServletRequest request){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        List<StickerSubscribeEntity> stickerSubscribeEntities = stickerSubscribeRepository.findByUserId(userId);
        if(stickerSubscribeEntities == null || stickerSubscribeEntities.size() == 0) {
            return new ResponseEntity("You no have subscribes stickers", HttpStatus.BAD_REQUEST);
        }

        List<StickersListDto> stickersListDtos = new ArrayList<StickersListDto>();
        for(StickerSubscribeEntity stickerSubscribeEntity : stickerSubscribeEntities){

            StickersListEntity stickersListEntity = stickerListRepository.findById(stickerSubscribeEntity.getSubscribePack()).orElse(null);
            if(stickersListEntity == null) {
                continue;
            }
            List<StickerEntity> stickerEntityList = stickerRepository.findByStickerPack(stickerSubscribeEntity.getSubscribePack());
            if(stickerEntityList == null || stickerEntityList.size() == 0){
                continue;
            }
            
            List<StickerDto> stickerDtos = new ArrayList<StickerDto>();
            for(StickerEntity stickerEntity : stickerEntityList){
                
                StickerDto stickerDto = new StickerDto( stickerEntity.getId(), stickerEntity.getDirectoryNameSticker() + "/" + stickerEntity.getFileNameSticker() );
                stickerDtos.add(stickerDto);
                
            }
            
            StickersListDto stickersListDto = new StickersListDto(stickersListEntity.getId(), stickersListEntity.getName(), stickerDtos);
            stickersListDtos.add(stickersListDto);
        }
        return stickersListDtos;
    }

}
