package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompilationRequest extends CompilationUpdate {

    @NotBlank(message = "empty title")
    @Length(max = 50)
    private String title;

}
