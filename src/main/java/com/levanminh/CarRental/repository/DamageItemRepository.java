package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.DamageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageItemRepository extends JpaRepository<DamageItem, Integer> {
    List<DamageItem> findByCarId(Integer carId);
    List<DamageItem> findByContractCarId(Integer contractCarId);
    List<DamageItem> findByDamageReportId(Integer damageReportId);
}
