package com.travel.tourbooking.dto;

import com.travel.tourbooking.model.Tour;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourRequest {

    @NotBlank(message = "Tên tour là bắt buộc")
    @Size(max = 255, message = "Tên tour không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả là bắt buộc")
    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Số ngày là bắt buộc")
    @Positive(message = "Số ngày phải lớn hơn 0")
    private Integer durationDays;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @NotNull(message = "Số người tối đa là bắt buộc")
    @Positive(message = "Số người tối đa phải lớn hơn 0")
    private Integer maxGroupSize;

    @NotNull(message = "Trạng thái là bắt buộc")
    private Tour.TourStatus status;
}
