package com.duberlyguarnizo.plh.address;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByRegion(String region, Pageable pageable);

    List<Address> findByRegionAndProvince(String region, String province, Pageable pageable);

    List<Address> findByRegionAndProvinceAndDistrict(String region, String province, String district, Pageable pageable);

    List<Address> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
