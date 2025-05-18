package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblContract")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "contractDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    
    @Column(name = "status", nullable = false, length = 25)
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "tblCusomerID", nullable = false)
    private Customer customer;
    
    @OneToMany(mappedBy = "contract")
    private List<ContractCar> contractCars;
    
    @OneToMany(mappedBy = "contract")
    private List<Collateral> collaterals;
    
    @OneToMany(mappedBy = "contract")
    private List<Invoice> invoices;
}
