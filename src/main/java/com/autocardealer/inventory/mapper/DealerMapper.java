package com.autocardealer.inventory.mapper;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.dto.DealerRequest;
import com.autocardealer.inventory.dto.DealerResponse;
import com.autocardealer.inventory.dto.DealerUpdateRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Dealer entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DealerMapper {

    /**
     * Converts a Dealer entity to a DealerResponse DTO.
     */
    DealerResponse toResponse(Dealer dealer);

    /**
     * Converts a DealerRequest DTO to a Dealer entity.
     */
    Dealer toEntity(DealerRequest request);

    /**
     * Updates an existing Dealer entity with values from DealerUpdateRequest.
     * Only non-null values in the request will update the entity.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(DealerUpdateRequest request, @MappingTarget Dealer dealer);
}