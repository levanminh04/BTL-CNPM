package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    List<InvoiceDetail> findByInvoiceId(Integer invoiceId);
    List<InvoiceDetail> findByContractCarId(Integer contractCarId);
    List<InvoiceDetail> findByFeeType(String feeType);
}
