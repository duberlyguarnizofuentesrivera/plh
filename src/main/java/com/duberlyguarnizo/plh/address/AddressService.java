package com.duberlyguarnizo.plh.address;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AddressService {
    private final AddressRepository repository;
    private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public List<AddressBasicDto> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByRegion(String region, int page, int size) {
        return repository.findByRegion(region, PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByRegionAndProvince(String region, String province, int page, int size) {
        return repository.findByRegionAndProvince(region, province, PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByRegionAndProvinceAndDistrict(String region, String province, String district, int page, int size) {
        return repository.findByRegionAndProvinceAndDistrict(region, province, district, PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByDate(LocalDate date, int page, int size) {
        return repository.findByLastModifiedDateBetween(date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByDateInterval(LocalDate date, LocalDate date2, int page, int size) {
        return repository.findByLastModifiedDateBetween(date.atStartOfDay(),
                date2.plusDays(1).atStartOfDay(),
                PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    public List<AddressBasicDto> getAllByLastDays(int numDays, int page, int size) {
        return repository.findByLastModifiedDateBetween(LocalDate.now().atStartOfDay().minusDays(numDays),
                LocalDateTime.now(),
                PageRequest.of(page - 1, size)).stream().map(mapper::toBasicDto).toList();
    }

    //TODO: add method to return addresses with observations
    public Optional<AddressDto> getDetailById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public boolean save(AddressDto address) {

        try {
            repository.save(mapper.toEntity(address));
            return true;
        } catch (Exception e) {
            log.warn("AddressService: save(): Failed to save address with id: {} and message: {}", address.id(), e.getMessage());
            return false;
        }
    }
}
