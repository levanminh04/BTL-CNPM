package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblInvoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "invoiceDate", nullable = false)
    private Integer invoiceDate;
    
    @Column(name = "paymentMethod", nullable = false, length = 25)
    private String paymentMethod;
    
    @Column(name = "status", nullable = false, length = 25)
    private String status;
    
    @Column(name = "invoiceType", nullable = false, length = 25)
    private String invoiceType;
    
    @Column(name = "paymentDate", nullable = false, length = 25)
    private String paymentDate;
    
    @Column(name = "discountAmount", nullable = false)
    private Float discountAmount;
    
    @Column(name = "amountPaid")
    private Float amountPaid;
    
    @ManyToOne
    @JoinColumn(name = "tblUserID", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "tblContractID", nullable = false)
    private Contract contract;
    
    @OneToMany(mappedBy = "invoice")
    private List<InvoiceDetail> invoiceDetails;
}
