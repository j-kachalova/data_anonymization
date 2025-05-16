package com.kachalova.distributor.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OriginalDataDto {
    @NotBlank
    @Pattern(regexp = "^\\d{4}\\s\\d{6}$", message = "passport must be in format '1234 567890'")
    private String passport;
    @NotBlank
    @Pattern(regexp = "^(89|\\+79)\\d{9}$", message = "phone must be a valid Russian mobile number")
    private String phone;
    @NotBlank
    @Email(message = "email must be a valid email address")
    @Pattern(regexp = "^[\\w.-]+@(?:mail\\.ru|gmail\\.com|yandex\\.ru|bk\\.ru|outlook\\.com|icloud\\.com|rambler\\.ru)$",
            message = "email domain must be allowed")
    private String email;
}
