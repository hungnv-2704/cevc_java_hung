package com.travel.tourbooking.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingTourRequest {

    @NotNull
    private Long tourId;

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @Min(1)
    private Integer adults;

    @NotNull
    @Min(0)
    private Integer children;

    @NotBlank
    private String contactFullName;

    @NotBlank
    @Email
    private String contactEmail;

    @NotBlank
    @Pattern(regexp = "^[+\\d][\\d\\s\\-()]{6,20}$", message = "Invalid phone number")
    private String contactPhone;

    private String specialRequests;

    private String couponCode;
}