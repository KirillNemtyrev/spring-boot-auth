package com.community.server.mapper;

import com.community.server.dto.UserSettings;
import com.community.server.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SettingsUserMapper.class)
public interface SettingsUserMapper {

    UserEntity toDTO(UserSettings userSettings);
    UserSettings toModel(UserEntity userEntity);
}