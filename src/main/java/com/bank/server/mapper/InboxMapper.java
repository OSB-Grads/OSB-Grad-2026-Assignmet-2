package com.bank.server.mapper;

import com.bank.server.dto.InboxDTO;
import com.bank.server.entity.Inbox;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface InboxMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "payload" , source = "payload" ,qualifiedByName = "jsonToMap")
    InboxDTO toDTO(Inbox inbox);

    @Mapping(target = "payload" , source = "payload" ,qualifiedByName = "mapToJson")
    Inbox toEntity(InboxDTO inboxDTO);

    @Named("jsonToMap")
    default Map<String,Object> jsonToMap(String json){
        try{
            return json==null
                    ? null
                    : objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to parse payload",e);
        }
    }

    @Named("mapToJson")
    default String mapToJson(Map<String,Object> map){
        try{
            return map==null
                    ? null
                    : objectMapper.writeValueAsString(map);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize payload",e);
        }
    }
}
