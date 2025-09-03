package br.com.projeto.estudos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistroDTO {

    private String disciplina;
    private int ano;
    private int semestre;
    private double quantidadeResultados;

}
