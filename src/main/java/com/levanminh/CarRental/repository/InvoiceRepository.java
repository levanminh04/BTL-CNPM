package com.levanminh.CarRental.repository;

import com.levanminh.CarRental.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByUserId(Integer userId);
    List<Invoice> findByContractId(Integer contractId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByPaymentMethod(String paymentMethod);
    List<Invoice> findByInvoiceType(String invoiceType);
}
