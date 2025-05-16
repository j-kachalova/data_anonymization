package com.kachalova.distributor.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$", message = "birthDate must be in format DD.MM.YYYY")
    public String birthDate;

    @NotBlank
    @Size(min = 2, message = "birthPlace must be at least 2 characters")
    @Pattern(regexp = "^[А-Яа-я\\-\\s]+$", message = "birthPlace must contain only Cyrillic letters and spaces")
    public String birthPlace;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\s\\d{6}$", message = "passport must be in format '1234 567890'")
    public String passport;

    @NotBlank
    @Size(min = 5, message = "address must be at least 5 characters")
    public String address;

    @NotBlank
    @Pattern(regexp = "^(89|\\+79)\\d{9}$", message = "phone must be a valid Russian mobile number")
    public String phone;

    @NotBlank
    @Email(message = "email must be a valid email address")
    @Pattern(regexp = "^[\\w.-]+@(?:mail\\.ru|gmail\\.com|yandex\\.ru|bk\\.ru|outlook\\.com|icloud\\.com|rambler\\.ru)$",
            message = "email domain must be allowed")
    public String email;

    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "INN must contain exactly 12 digits")
    public String inn;

    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{3}\\s\\d{2}$", message = "SNILS must be in format '123-456-789 00'")
    public String snils;

    @NotBlank
    @Pattern(regexp = "^\\d{4}([ -]?\\d{4}){3}$", message = "card must be 16 digits, optionally grouped")
    public String card;
}
