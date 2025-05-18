package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblDamageItem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DamageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "repairCostEstimate", nullable = false)
    private Float repairCostEstimate;
    
    @Column(name = "DamageReportID", nullable = false)
    private Integer damageReportId;
    
    @Column(name = "image", nullable = false, length = 10)
    private String image;
    
    @ManyToOne
    @JoinColumn(name = "tblCarID", nullable = false)
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "tblContractCarID", nullable = false)
    private ContractCar contractCar;
}
