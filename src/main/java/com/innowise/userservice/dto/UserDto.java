package com.innowise.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name should be between 3 and 100 symbols")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 3, max = 100, message = "Surname should be between 3 and 100 symbols")
    private String surname;

    @Past(message = "Birth date must be in past")
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @Email(message = "Should have correct email")
    @NotBlank(message = "Email is required")
    private String email;

    private boolean active;

    private List<PaymentCardDto> paymentCards;
}
