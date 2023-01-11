package com.duberlyguarnizo.plh.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByRegion(String region);

    List<Address> findByRegionAndProvince(String region, String province);

    List<Address> findByRegionAndProvinceAndDistrict(String region, String province, String district);

    List<Address> findByModificationDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
