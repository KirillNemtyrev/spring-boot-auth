package com.community.server.mapper;

import com.community.server.entity.UserEntity;
import com.community.server.view.UserView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserMapper {

    UserEntity toDTO(UserView userView);

    UserView toModel(UserEntity userEntity);
}
