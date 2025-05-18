package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblCollateral")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collateral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "value", nullable = false)
    private Float value;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "returnStatus", nullable = false, length = 20)
    private String returnStatus;
    
    @ManyToOne
    @JoinColumn(name = "tblContractID", nullable = false)
    private Contract contract;
}
