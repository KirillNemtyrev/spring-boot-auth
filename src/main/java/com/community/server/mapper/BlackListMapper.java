package com.community.server.mapper;

import com.community.server.dto.BlackList;
import com.community.server.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BlackListMapper.class)
public interface BlackListMapper {
    UserEntity toDTO(BlackList blackList);
    BlackList toModel(UserEntity userEntity);
}
