package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.ContractCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractCarRepository extends JpaRepository<ContractCar, Integer> {
    List<ContractCar> findByCarId(Integer carId);
    List<ContractCar> findByContractId(Integer contractId);
    List<ContractCar> findByStartDate(Date startDate);
    List<ContractCar> findByReturnedDate(Date returnedDate);
}
