package com.travel.tourbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    // Note: password is not included in response for security
}
