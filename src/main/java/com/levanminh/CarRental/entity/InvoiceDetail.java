package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblInvoiceDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "feeType", nullable = false)
    private String feeType;
    
    @Column(name = "feeAmount", nullable = false)
    private Float feeAmount;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "discount", nullable = false)
    private Float discount;
    
    @ManyToOne
    @JoinColumn(name = "tblContractCarID", nullable = false)
    private ContractCar contractCar;
    
    @ManyToOne
    @JoinColumn(name = "tblInvoiceID", nullable = false)
    private Invoice invoice;
}
