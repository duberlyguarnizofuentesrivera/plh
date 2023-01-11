package com.duberlyguarnizo.plh.shipment;

import com.duberlyguarnizo.plh.enums.ShipmentProblem;
import com.duberlyguarnizo.plh.enums.ShipmentStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByCode(String shipmentCode);

    List<Shipment> findByReceptionDateNotNull(PageRequest pageRequest);

    List<Shipment> findByOnRouteDateNotNull(PageRequest pageRequest);

    List<Shipment> findByOnReturnDateNotNull(PageRequest pageRequest);

    List<Shipment> findByReturnDateNotNull(PageRequest pageRequest);

    List<Shipment> findByDeliveryDateNotNull(PageRequest pageRequest);

    List<Shipment> findByClient_Id(Long clientId);//NOSONAR

    List<Shipment> findByReceiver_Id(Long receiverId);//NOSONAR

    List<Shipment> findByDeliveryTransporter_Id(Long userId, PageRequest pageRequest);//NOSONAR

    List<Shipment> findByPickUpTransporter_Id(Long userId, PageRequest pageRequest);//NOSONAR

    List<Shipment> findByReceiverUser_Id(Long userId, PageRequest pageRequest);//NOSONAR

    List<Shipment> findByModificationDateBetween(LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);

    List<Shipment> findByProblems(ShipmentProblem shipmentProblem, PageRequest pageRequest);

    List<Shipment> findByStatus(ShipmentStatus shipmentStatus, PageRequest pageRequest);
}
