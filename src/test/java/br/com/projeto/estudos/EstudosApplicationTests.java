package br.com.projeto.estudos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.com.projeto.estudos.servico.Processamento.processamentoIdentificadoDisciplinaTreinoResultado;

//@SpringBootTest
class EstudosApplicationTests {

    @Test
    @DisplayName("Realizar a leitura dos dados")
    void leituraDoConjunto() throws Exception {
        //abrirSessaoConjuntodeDados();
        processamentoIdentificadoDisciplinaTreinoResultado();

    }


}
