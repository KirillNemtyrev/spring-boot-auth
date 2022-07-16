package com.community.server.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="stickers")
public class StickerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stickerPack;

    private String directoryNameSticker;
    private String fileNameSticker;
}
