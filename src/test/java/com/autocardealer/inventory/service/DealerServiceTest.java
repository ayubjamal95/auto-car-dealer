package com.autocardealer.inventory.service;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.domain.SubscriptionType;
import com.autocardealer.inventory.dto.DealerRequest;
import com.autocardealer.inventory.dto.DealerResponse;
import com.autocardealer.inventory.dto.DealerUpdateRequest;
import com.autocardealer.inventory.exception.ResourceNotFoundException;
import com.autocardealer.inventory.mapper.DealerMapper;
import com.autocardealer.inventory.repository.DealerRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DealerService.
 *
 * Tests cover:
 * - CRUD operations
 * - Tenant isolation
 * - Error handling
 */
@ExtendWith(MockitoExtension.class)
class DealerServiceTest {

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private DealerMapper dealerMapper;

    @InjectMocks
    private DealerService dealerService;

    private static final String TENANT_ID = "tenant-123";
    private static final UUID DEALER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(TENANT_ID);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createDealer_Success() {
        // Given
        DealerRequest request = DealerRequest.builder()
                .name("Test Dealer")
                .email("test@example.com")
                .subscriptionType(SubscriptionType.PREMIUM)
                .build();

        Dealer dealer = new Dealer();
        dealer.setName(request.getName());
        dealer.setEmail(request.getEmail());
        dealer.setSubscriptionType(request.getSubscriptionType());

        Dealer savedDealer = new Dealer();
        savedDealer.setId(DEALER_ID);
        savedDealer.setTenantId(TENANT_ID);
        savedDealer.setName(request.getName());
        savedDealer.setEmail(request.getEmail());
        savedDealer.setSubscriptionType(request.getSubscriptionType());

        DealerResponse response = DealerResponse.builder()
                .id(DEALER_ID)
                .tenantId(TENANT_ID)
                .name(request.getName())
                .email(request.getEmail())
                .subscriptionType(request.getSubscriptionType())
                .build();

        when(dealerMapper.toEntity(request)).thenReturn(dealer);
        when(dealerRepository.save(dealer)).thenReturn(savedDealer);
        when(dealerMapper.toResponse(savedDealer)).thenReturn(response);

        // When
        DealerResponse result = dealerService.createDealer(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEALER_ID);
        assertThat(result.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(result.getName()).isEqualTo(request.getName());

        verify(dealerRepository).save(dealer);
        assertThat(dealer.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    void getDealerById_Success() {
        // Given
        Dealer dealer = new Dealer();
        dealer.setId(DEALER_ID);
        dealer.setTenantId(TENANT_ID);
        dealer.setName("Test Dealer");

        DealerResponse response = DealerResponse.builder()
                .id(DEALER_ID)
                .tenantId(TENANT_ID)
                .name("Test Dealer")
                .build();

        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.of(dealer));
        when(dealerMapper.toResponse(dealer)).thenReturn(response);

        // When
        DealerResponse result = dealerService.getDealerById(DEALER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEALER_ID);
        assertThat(result.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    void getDealerById_NotFound() {
        // Given
        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dealerService.getDealerById(DEALER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dealer");
    }

    @Test
    void getDealerById_CrossTenantAccess() {
        // Given
        UUID otherDealerId = UUID.randomUUID();
        when(dealerRepository.findByIdAndTenantId(otherDealerId, TENANT_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dealerService.getDealerById(otherDealerId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllDealers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);

        Dealer dealer1 = new Dealer();
        dealer1.setId(UUID.randomUUID());
        dealer1.setTenantId(TENANT_ID);

        Dealer dealer2 = new Dealer();
        dealer2.setId(UUID.randomUUID());
        dealer2.setTenantId(TENANT_ID);

        Page<Dealer> dealerPage = new PageImpl<>(List.of(dealer1, dealer2));

        DealerResponse response1 = DealerResponse.builder().id(dealer1.getId()).build();
        DealerResponse response2 = DealerResponse.builder().id(dealer2.getId()).build();

        when(dealerRepository.findByTenantId(TENANT_ID, pageable)).thenReturn(dealerPage);
        when(dealerMapper.toResponse(dealer1)).thenReturn(response1);
        when(dealerMapper.toResponse(dealer2)).thenReturn(response2);

        // When
        Page<DealerResponse> result = dealerService.getAllDealers(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void updateDealer_Success() {
        // Given
        DealerUpdateRequest request = DealerUpdateRequest.builder()
                .name("Updated Name")
                .build();

        Dealer dealer = new Dealer();
        dealer.setId(DEALER_ID);
        dealer.setTenantId(TENANT_ID);
        dealer.setName("Old Name");

        DealerResponse response = DealerResponse.builder()
                .id(DEALER_ID)
                .tenantId(TENANT_ID)
                .name("Updated Name")
                .build();

        when(dealerRepository.findByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(Optional.of(dealer));
        when(dealerRepository.save(dealer)).thenReturn(dealer);
        when(dealerMapper.toResponse(dealer)).thenReturn(response);

        // When
        DealerResponse result = dealerService.updateDealer(DEALER_ID, request);

        // Then
        assertThat(result).isNotNull();
        verify(dealerMapper).updateEntityFromRequest(request, dealer);
        verify(dealerRepository).save(dealer);
    }

    @Test
    void deleteDealer_Success() {
        // Given
        when(dealerRepository.existsByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(true);

        // When
        dealerService.deleteDealer(DEALER_ID);

        // Then
        verify(dealerRepository).deleteByIdAndTenantId(DEALER_ID, TENANT_ID);
    }

    @Test
    void deleteDealer_NotFound() {
        // Given
        when(dealerRepository.existsByIdAndTenantId(DEALER_ID, TENANT_ID))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealerService.deleteDealer(DEALER_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}