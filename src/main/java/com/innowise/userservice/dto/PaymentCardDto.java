package com.innowise.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "User is required")
    private Long userId;

    @NotBlank(message = "Number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Number should contains 16 symbols from 0 to 9")
    private String number;

    @NotBlank(message = "Holder is required")
    @Size(min = 3, max = 100, message = "Holder name should be between 2 and 100 symbols")
    private String holder;

    @NotNull
    @Future(message = "Card should not be expired")
    private LocalDate expirationDate;

    private boolean active;

}
