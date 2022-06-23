package com.community.server.mapper;

import com.community.server.dto.UserSearch;
import com.community.server.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SearchUserMapper.class)
public interface SearchUserMapper {
    UserEntity toDTO(UserSearch userSearch);
    UserSearch toModel(UserEntity userEntity);
}
