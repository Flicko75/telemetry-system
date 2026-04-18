package com.telemetry.monitoring;

import com.telemetry.common.DTOs.DeviceResponse;
import com.telemetry.common.DTOs.DeviceUpdateDTO;
import com.telemetry.common.enums.DeviceHealth;
import com.telemetry.common.exceptions.ResourceNotFoundException;
import com.telemetry.monitoring.entity.DeviceEntity;
import com.telemetry.monitoring.repos.DeviceRepository;
import com.telemetry.monitoring.services.DeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void deviceRegisterSuccess_usingDTOValues(){
        String deviceId = "SAT-001";
        DeviceUpdateDTO dto = buildDTO("Device 002", LocalDateTime.now());

        DeviceEntity device = buildDevice(
                deviceId,
                "Device 001",
                DeviceHealth.DEGRADED
        );

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(deviceRepository.save(device)).thenReturn(device);

        deviceService.officiallyRegisterDevice(deviceId, dto);

        ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);

        verify(deviceRepository).save(captor.capture());
        verify(deviceRepository).findByDeviceId(deviceId);

        DeviceEntity saved = captor.getValue();
        assertEquals(dto.deviceDesc(), saved.getDeviceDesc());
        assertEquals(dto.officiallyRegisteredAt(), saved.getOfficiallyRegisteredAt());
    }

    @Test
    void deviceRegisterSuccess_withoutDTOValues(){
        String deviceId = "SAT-001";
        DeviceUpdateDTO dto = buildDTO(null, null);

        DeviceEntity device = buildDevice(
                deviceId,
                "Device 001",
                DeviceHealth.DEGRADED
        );

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(deviceRepository.save(device)).thenReturn(device);

        deviceService.officiallyRegisterDevice(deviceId, dto);

        ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);

        verify(deviceRepository).save(captor.capture());
        verify(deviceRepository).findByDeviceId(deviceId);

        DeviceEntity saved = captor.getValue();
        assertEquals(device.getDeviceDesc(), saved.getDeviceDesc());
        assertNotNull(saved.getOfficiallyRegisteredAt());
    }

    @Test
    void deviceRegister_deviceNotFound(){
        String deviceId = "SAT-001";
        DeviceUpdateDTO dto = buildDTO(null, null);

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> deviceService.officiallyRegisterDevice(deviceId, dto));

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(deviceRepository, never()).save(any());
    }

    private DeviceUpdateDTO buildDTO(String description, LocalDateTime registerTime){
        return new DeviceUpdateDTO(
                description,
                registerTime
        );
    }

    private DeviceEntity buildDevice(String deviceId, String deviceDesc, DeviceHealth health){
        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(deviceId);
        device.setDeviceDesc(deviceDesc);
        device.setDeviceHealth(health);
        device.setRegisteredAt(LocalDateTime.now());
        return device;
    }

}
