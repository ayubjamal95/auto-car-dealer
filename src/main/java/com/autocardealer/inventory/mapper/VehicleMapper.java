package com.autocardealer.inventory.mapper;

import com.autocardealer.inventory.domain.Vehicle;
import com.autocardealer.inventory.dto.VehicleRequest;
import com.autocardealer.inventory.dto.VehicleResponse;
import com.autocardealer.inventory.dto.VehicleUpdateRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Vehicle entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehicleMapper {

    /**
     * Converts a Vehicle entity to a VehicleResponse DTO.
     * Maps dealer relationship to dealerId and dealerName.
     */
    @Mapping(source = "dealer.id", target = "dealerId")
    @Mapping(source = "dealer.name", target = "dealerName")
    VehicleResponse toResponse(Vehicle vehicle);

    /**
     * Converts a VehicleRequest DTO to a Vehicle entity.
     * Note: Dealer relationship must be set separately in the service layer.
     */
    @Mapping(target = "dealer", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    /**
     * Updates an existing Vehicle entity with values from VehicleUpdateRequest.
     * Only non-null values in the request will update the entity.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dealer", ignore = true)
    void updateEntityFromRequest(VehicleUpdateRequest request, @MappingTarget Vehicle vehicle);
}