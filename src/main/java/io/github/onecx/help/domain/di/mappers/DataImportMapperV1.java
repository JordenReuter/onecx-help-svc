package io.github.onecx.help.domain.di.mappers;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.help.di.v1.model.DataImportHelpDTOV1;
import io.github.onecx.help.domain.models.Help;

@Mapper(uses = OffsetDateTimeMapper.class)
public abstract class DataImportMapperV1 {

    @Inject
    ObjectMapper mapper;

    @Named("import")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    public abstract Help importHelp(DataImportHelpDTOV1 dto);

    @Named("properties")
    String properties(Object properties) {
        if (properties == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
