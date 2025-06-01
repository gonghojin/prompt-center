package com.gongdel.promptserver.adapter.in.rest.request;

import com.gongdel.promptserver.domain.model.InputVariable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputVariableDto {
    private String name;
    private String type;
    private String description;
    private boolean required;
    private String defaultValue;

    public static InputVariableDto fromDomain(InputVariable domain) {
        return InputVariableDto.builder()
            .name(domain.getName())
            .type(domain.getType())
            .description(domain.getDescription())
            .required(domain.isRequired())
            .defaultValue(domain.getDefaultValue())
            .build();
    }

    public InputVariable toDomain() {
        return InputVariable.builder()
            .name(this.name)
            .type(this.type)
            .description(this.description)
            .required(this.required)
            .defaultValue(this.defaultValue)
            .build();
    }
}
