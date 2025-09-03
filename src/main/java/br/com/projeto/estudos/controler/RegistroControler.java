package br.com.projeto.estudos.controler;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import static br.com.projeto.estudos.servico.Processamento.listarValoresResultados;
import static br.com.projeto.estudos.servico.Processamento.processamentoIdentificadoDisciplinaTreinoResultado;

@RestController
@RequestMapping("/api/disciplinas")
public class RegistroControler {

    @PatchMapping("/processo")
    public ResponseEntity<?> inicializacaoProcessamento() {
        processamentoIdentificadoDisciplinaTreinoResultado();
        return ResponseEntity.ok("Processamento Realizado com Sucesso !!!");
    }

    @GetMapping(value = "/resultado")
    public ResponseEntity<?> listarResultadoDisciplinas() {
        return ResponseEntity.status(202).body(listarValoresResultados());
    }

}

