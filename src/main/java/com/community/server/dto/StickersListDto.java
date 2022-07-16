package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class StickersListDto {

    private Long id;
    private String name;
    private List<StickerDto> stickers;

}
