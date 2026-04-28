package com.autocardealer.inventory.service;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.domain.SubscriptionType;
import com.autocardealer.inventory.domain.Vehicle;
import com.autocardealer.inventory.domain.VehicleStatus;
import com.autocardealer.inventory.dto.VehicleRequest;
import com.autocardealer.inventory.dto.VehicleResponse;
import com.autocardealer.inventory.exception.ResourceNotFoundException;
import com.autocardealer.inventory.mapper.VehicleMapper;
import com.autocardealer.inventory.repository.DealerRepository;
import com.autocardealer.inventory.repository.VehicleRepository;
import com.autocardealer.inventory.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VehicleService.
 *
 * Tests cover:
 * - CRUD operations
 * - Tenant isolation
 * - Dealer validation
 * - Complex filtering
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private static final String TENANT_ID = "tenant-123";
    private static final UUID DEALER_ID = UUID.randomUUID();
    private static final UUID VEHICLE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(TENANT_ID);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createVehicle_Success() {
        // Given
        VehicleRequest request = VehicleRequest.builder()
                .model("Toyota Camry")
                .price(BigDecimal.valueOf(25000))
                .status(VehicleStatus.AVAILABLE)
                .dealerId(DEALER_ID)
                .build();

        Dealer dealer = new Dealer();
        dealer.setId(DEALER_ID);
        dealer.setTenantId(TENANT_ID);

        Vehicle vehicle = new Vehicle();
        vehicle.setModel(request.getModel());
        vehicle.setPrice(request.getPrice());
        vehicle.setStatus(request.getStatus());

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(VEHICLE_ID);
        savedVehicle.setTenantId(TENANT_ID);
        savedVehicle.setModel(request.getModel());
        savedVehicle.setPrice(request.getPrice());
        savedVehicle.setStatus(request.getStatus());
        savedVehicle.setDealer(dealer);

        VehicleResponse response = VehicleResponse.builder()
                .id(VEHICLE_ID)
                .tenantId(TENANT_ID)
                .model(request.getModel())
                .price(request.getPrice())
                .status(request.getStatus())
                .dealerId(DEALER_ID)
                .build();

        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.of(dealer));
        when(vehicleMapper.toEntity(request)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(savedVehicle);
        when(vehicleMapper.toResponse(savedVehicle)).thenReturn(response);

        // When
        VehicleResponse result = vehicleService.createVehicle(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VEHICLE_ID);
        assertThat(result.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(result.getDealerId()).isEqualTo(DEALER_ID);

        verify(vehicleRepository).save(vehicle);
        assertThat(vehicle.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(vehicle.getDealer()).isEqualTo(dealer);
    }

    @Test
    void createVehicle_DealerNotFound() {
        // Given
        VehicleRequest request = VehicleRequest.builder()
                .model("Toyota Camry")
                .price(BigDecimal.valueOf(25000))
                .status(VehicleStatus.AVAILABLE)
                .dealerId(DEALER_ID)
                .build();

        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dealer");
    }

    @Test
    void createVehicle_DealerFromDifferentTenant() {
        // Given
        VehicleRequest request = VehicleRequest.builder()
                .model("Toyota Camry")
                .price(BigDecimal.valueOf(25000))
                .status(VehicleStatus.AVAILABLE)
                .dealerId(DEALER_ID)
                .build();

        // Dealer exists but belongs to different tenant
        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getVehicleById_Success() {
        // Given
        Dealer dealer = new Dealer();
        dealer.setId(DEALER_ID);
        dealer.setName("Test Dealer");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(VEHICLE_ID);
        vehicle.setTenantId(TENANT_ID);
        vehicle.setModel("Toyota Camry");
        vehicle.setDealer(dealer);

        VehicleResponse response = VehicleResponse.builder()
                .id(VEHICLE_ID)
                .tenantId(TENANT_ID)
                .model("Toyota Camry")
                .dealerId(DEALER_ID)
                .dealerName("Test Dealer")
                .build();

        when(vehicleRepository.findByIdAndTenantId(VEHICLE_ID, TENANT_ID))
                .thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(response);

        // When
        VehicleResponse result = vehicleService.getVehicleById(VEHICLE_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VEHICLE_ID);
        assertThat(result.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    void getVehicleById_NotFound() {
        // Given
        when(vehicleRepository.findByIdAndTenantId(VEHICLE_ID, TENANT_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.getVehicleById(VEHICLE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle");
    }

    @Test
    void getVehicles_WithFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        String model = "Toyota";
        VehicleStatus status = VehicleStatus.AVAILABLE;
        BigDecimal priceMin = BigDecimal.valueOf(20000);
        BigDecimal priceMax = BigDecimal.valueOf(30000);
        SubscriptionType subscription = SubscriptionType.PREMIUM;

        Vehicle vehicle = new Vehicle();
        vehicle.setId(VEHICLE_ID);
        vehicle.setTenantId(TENANT_ID);

        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        VehicleResponse response = VehicleResponse.builder().id(VEHICLE_ID).build();

        when(vehicleRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(vehiclePage);
        when(vehicleMapper.toResponse(vehicle)).thenReturn(response);

        // When
        Page<VehicleResponse> result = vehicleService.getVehicles(
                model, status, priceMin, priceMax, subscription, pageable
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(vehicleRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void deleteVehicle_Success() {
        // Given
        when(vehicleRepository.existsByIdAndTenantId(VEHICLE_ID, TENANT_ID))
                .thenReturn(true);

        // When
        vehicleService.deleteVehicle(VEHICLE_ID);

        // Then
        verify(vehicleRepository).deleteByIdAndTenantId(VEHICLE_ID, TENANT_ID);
    }

    @Test
    void deleteVehicle_NotFound() {
        // Given
        when(vehicleRepository.existsByIdAndTenantId(VEHICLE_ID, TENANT_ID))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> vehicleService.deleteVehicle(VEHICLE_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}