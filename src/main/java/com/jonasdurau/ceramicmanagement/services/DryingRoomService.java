package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.DryingRoomRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.DryingRoomResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.MachineDTO;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.entities.DryingRoom;
import com.jonasdurau.ceramicmanagement.entities.DryingSession;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.repositories.DryingRoomRepository;
import com.jonasdurau.ceramicmanagement.repositories.DryingSessionRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;

@Service
public class DryingRoomService {
    
    @Autowired
    private DryingRoomRepository dryingRoomRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private DryingSessionRepository dryingSessionRepository;

    @Transactional(readOnly = true)
    public List<DryingRoomResponseDTO> findAll() {
        List<DryingRoom> list = dryingRoomRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public DryingRoomResponseDTO findById(Long id) {
        DryingRoom entity = dryingRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public DryingRoomResponseDTO create(DryingRoomRequestDTO dto) {
        DryingRoom entity = new DryingRoom();
        if(dryingRoomRepository.existsByName(dto.getName())) {
            throw new BusinessException("Esse nome já existe.");
        }
        entity.setName(dto.getName());
        entity.setGasConsumptionPerHour(dto.getGasConsumptionPerHour());
        for(Long id : dto.getMachines()) {
            Machine machine = machineRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + id));
            entity.getMachines().add(machine);
        }
        entity = dryingRoomRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public DryingRoomResponseDTO update(Long id, DryingRoomRequestDTO dto) {
        DryingRoom entity = dryingRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + id));
        String oldName = entity.getName();
        String newName = dto.getName();
        if(!oldName.equals(newName) && dryingRoomRepository.existsByName(newName)) {
            throw new BusinessException("Esse nome já existe.");
        }
        entity.setName(newName);
        entity.setGasConsumptionPerHour(dto.getGasConsumptionPerHour());
        List<Machine> oldList = new ArrayList<>(entity.getMachines());
        List<Machine> newList = dto.getMachines().stream().map(machineId -> {
            Machine machine = machineRepository.findById(machineId)
                    .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + machineId));
            return machine;
        }).collect(Collectors.toList());
        Set<Long> oldIds = oldList.stream().map(Machine::getId).collect(Collectors.toSet());
        Set<Long> newIds = newList.stream().map(Machine::getId).collect(Collectors.toSet());
        List<Machine> toRemove = oldList.stream().filter(machine -> !newIds.contains(machine.getId())).collect(Collectors.toList());
        List<Machine> toAdd = newList.stream().filter(machine -> !oldIds.contains(machine.getId())).collect(Collectors.toList());
        entity.getMachines().removeAll(toRemove);
        entity.getMachines().addAll(toAdd);
        entity = dryingRoomRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        DryingRoom entity = dryingRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + id));
        boolean hasDryingSessions = dryingSessionRepository.existsByDryingRoomId(id);
        if(hasDryingSessions) {
            throw new ResourceDeletionException("A estufa não pode ser deletada pois ela possui usos registrados.");
        }
        dryingRoomRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<YearReportDTO> yearlyReport(Long dryingRoomId) {
        DryingRoom dryingRoom = dryingRoomRepository.findById(dryingRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("DryingRoom not found: " + dryingRoomId));
        List<DryingSession> sessions = dryingRoom.getSessions();
        ZoneId zone = ZoneId.systemDefault();
        Map<Integer, Map<Month, List<DryingSession>>> mapYearMonth = sessions.stream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, s.getCreatedAt().atZone(zone)))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getValue().getMonth(),
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<DryingSession>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<DryingSession>> monthMap = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<DryingSession> monthSessions = monthMap.getOrDefault(m, Collections.emptyList());
                double incomingQty = monthSessions.size();
                BigDecimal incomingCost = monthSessions.stream()
                        .map(session -> session.getCostAtTime() != null ? session.getCostAtTime() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalIncomingQtyYear += incomingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
                MonthReportDTO monthDto = new MonthReportDTO();
                monthDto.setMonthName(m.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                monthDto.setIncomingQty(incomingQty);
                monthDto.setIncomingCost(incomingCost);
                monthDto.setOutgoingQty(0.0);
                monthDto.setOutgoingProfit(BigDecimal.ZERO);
                yearReport.getMonths().add(monthDto);
            }
            yearReport.setTotalIncomingQty(totalIncomingQtyYear);
            yearReport.setTotalIncomingCost(totalIncomingCostYear);
            yearReport.setTotalOutgoingQty(0.0);
            yearReport.setTotalOutgoingProfit(BigDecimal.ZERO);
            yearReports.add(yearReport);
        }
        yearReports.sort((a, b) -> b.getYear() - a.getYear());
        return yearReports;
    }

    private DryingRoomResponseDTO entityToDTO(DryingRoom entity) {
        DryingRoomResponseDTO dto = new DryingRoomResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setGasConsumptionPerHour(entity.getGasConsumptionPerHour());
        for(Machine machine : entity.getMachines()) {
            MachineDTO machineDTO = new MachineDTO();
            machineDTO.setId(machine.getId());
            machineDTO.setCreatedAt(machine.getCreatedAt());
            machineDTO.setUpdatedAt(machine.getUpdatedAt());
            machineDTO.setName(machine.getName());
            machineDTO.setPower(machine.getPower());
            dto.getMachines().add(machineDTO);
        }
        return dto;
    }
}
