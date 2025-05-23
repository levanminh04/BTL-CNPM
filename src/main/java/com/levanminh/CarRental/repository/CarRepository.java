package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    Optional<Car> findByLicensePlate(String licensePlate);
}
