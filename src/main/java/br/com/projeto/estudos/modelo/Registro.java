package br.com.projeto.estudos.modelo;

import lombok.*;

import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Registro implements Serializable {

    private String disciplinaRegistro;
    private String situacaoRegistro;
    private String semestreRegistro;
    private String nomeAlunoRegistro;
    private int quantidadeDisciplinasRegistro;

    public Registro(String semestreRegistro, int quantidadeDisciplinasRegistro) {
        this.semestreRegistro = semestreRegistro;
        this.quantidadeDisciplinasRegistro = quantidadeDisciplinasRegistro;
    }

}