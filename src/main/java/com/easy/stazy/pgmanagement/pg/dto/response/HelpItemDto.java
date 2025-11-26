package com.easy.stazy.pgmanagement.pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HelpItemDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 8879789653506826462L;
    private String question;
    private String answer;
}

