package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Integer> {
    List<Collateral> findByContractId(Integer contractId);
    List<Collateral> findByType(String type);
    List<Collateral> findByReturnStatus(String returnStatus);
}
