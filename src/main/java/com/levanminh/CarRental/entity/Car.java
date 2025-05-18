package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblCar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "licensePlate", nullable = false, unique = true, length = 20)
    private String licensePlate;
    
    @Column(name = "brand", nullable = false, length = 25)
    private String brand;
    
    @Column(name = "model", nullable = false, length = 25)
    private String model;
    
    @Column(name = "name", nullable = false, length = 25)
    private String name;
    
    @Column(name = "status", nullable = false, length = 25)
    private String status;
    
    @OneToMany(mappedBy = "car")
    private List<ContractCar> contractCars;
    
    @OneToMany(mappedBy = "car")
    private List<DamageItem> damageItems;
}
