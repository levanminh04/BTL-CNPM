package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    List<Contract> findByCustomerId(Integer customerId);
    List<Contract> findByContractDate(Date contractDate);
    List<Contract> findByStatus(String status);
}
