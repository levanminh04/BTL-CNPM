package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tblCusomer") // Note: keeping the original table name with typo
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "fullname", nullable = false)
    private String fullname;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "note")
    private String note;
    
    @OneToMany(mappedBy = "customer")
    private List<Contract> contracts;
}
