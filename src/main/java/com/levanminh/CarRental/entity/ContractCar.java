package com.levanminh.CarRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblContractCar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
      @Column(name = "startDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Column(name = "endDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @Column(name = "pricePerDay", nullable = false)
    private Float pricePerDay;
    
    @Column(name = "returnedDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date returnedDate;
    
    @Column(name = "initCarStatus", nullable = false)
    private String initCarStatus;
    
    @Column(name = "returnCarStatus", nullable = false)
    private String returnCarStatus;
    
    @ManyToOne
    @JoinColumn(name = "tblCarID", nullable = false)
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "tblContractID", nullable = false)
    private Contract contract;
    
    @OneToMany(mappedBy = "contractCar")
    private List<InvoiceDetail> invoiceDetails;
      @OneToMany(mappedBy = "contractCar")
    private List<DamageItem> damageItems;
    
    /**
     * Tính số ngày thuê xe từ startDate đến endDate
     * @return số ngày thuê, tối thiểu là 1 ngày
     */
    @Transient
    public int getRentalDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        
        // Tính số mili giây giữa hai ngày
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        // Chuyển đổi sang số ngày
        int diffInDays = (int) (diffInMillies / (24 * 60 * 60 * 1000));
        
        // Nếu thuê trong cùng một ngày, tính là 1 ngày
        return Math.max(1, diffInDays);
    }
}
