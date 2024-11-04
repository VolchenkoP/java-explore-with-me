package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
public class CompilationUpdate {

    private Integer id;

    @Length(max = 50)
    private String title;
    
    private Boolean pinned;

    private List<Long> events;

}