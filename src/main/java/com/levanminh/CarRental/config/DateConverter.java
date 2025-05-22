package com.levanminh.CarRental.config;

import com.levanminh.CarRental.entity.ContractCar;
import com.levanminh.CarRental.repository.ContractCarRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Lớp hỗ trợ chuyển đổi dữ liệu khi thay đổi kiểu dữ liệu của endDate
 * từ Integer (số ngày) sang Date (ngày kết thúc)
 */
@Component
public class DateConverter {

    @Autowired
    private ContractCarRepository contractCarRepository;

    /**
     * Phương thức chạy sau khi Spring khởi tạo bean, để chuyển đổi dữ liệu
     * Lưu ý: Phương thức này chỉ nên chạy một lần sau khi update schema
     */
    //@PostConstruct
    public void convertEndDates() {
        List<ContractCar> contractCars = contractCarRepository.findAll();
        
        for (ContractCar contractCar : contractCars) {
            Date startDate = contractCar.getStartDate();
            Date endDate = contractCar.getEndDate();
            
            // Nếu endDate là null hoặc bằng startDate, tiến hành tính toán
            if (endDate == null || endDate.equals(startDate)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                
                // Giả sử rằng giá trị Integer cũ (số ngày) đã được lưu trong một trường tạm
                // Integer rentalDays = ... (lấy từ trường tạm)
                // Ví dụ: nếu không có trường tạm, có thể đặt mặc định là 1 ngày
                int rentalDays = 1;
                
                // Thêm số ngày vào startDate để tính ra endDate mới
                calendar.add(Calendar.DAY_OF_MONTH, rentalDays);
                contractCar.setEndDate(calendar.getTime());
                
                contractCarRepository.save(contractCar);
            }
        }
        
        System.out.println("Đã chuyển đổi dữ liệu endDate từ Integer sang Date thành công");
    }
}
