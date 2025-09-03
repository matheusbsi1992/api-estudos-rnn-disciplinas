package br.com.projeto.estudos.identificacao;

import br.com.projeto.estudos.modelo.Registro;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Configuration
public class PreIdentificacao {

    @Autowired
    public static Map<String, List<Registro>> dadosRegistro = new HashMap<>();

    public static Map<String, List<Registro>> abrirSessaoConjuntodeDados() {

        Map<String, Map<String, Integer>> dadosRegistroReprovacao = new HashMap<>();

        Registro registro = null;

        try (Reader reader = new FileReader("disciplinas/disciplinas_padrao.csv", StandardCharsets.UTF_8)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            int i = 0;

            for (CSVRecord csvRecord : records) {
                registro = new Registro();

                if (i == 0) {
                    i++;
                    continue;
                }

                String itemv[] = /*tipoAtributos*/(Arrays.toString(csvRecord
                                .values())
                        .replace("|",",")
                        .split(","));

                registro.setDisciplinaRegistro(itemv[1].trim());
                registro.setSemestreRegistro(itemv[2].trim());
                registro.setNomeAlunoRegistro(itemv[3].trim());
                registro.setSituacaoRegistro(itemv[4].trim());

                if (!registro.getSituacaoRegistro().equalsIgnoreCase("APROVADO")//registro.getSituacaoRegistro().equalsIgnoreCase("reprovado")
                        /*|| registro.getSituacaoRegistro().equalsIgnoreCase("cancelado")
                        || registro.getSituacaoRegistro().equalsIgnoreCase("rep. falta")*/) {
                    dadosRegistroReprovacao
                            .computeIfAbsent(registro.getDisciplinaRegistro(), k -> new HashMap<>())
                            .merge(registro.getSemestreRegistro(), 1, Integer::sum);
                }
            }

            for (Map.Entry<String, Map<String, Integer>> identificarDisciplina : dadosRegistroReprovacao.entrySet()) {

                String disciplinaSemestre = identificarDisciplina.getKey();

                List<Registro> registros = new ArrayList<>();

                for (Map.Entry<String, Integer> identificarSemestre : identificarDisciplina.getValue().entrySet()) {
                    registros.add(new Registro(identificarSemestre.getKey(), identificarSemestre.getValue()));
                }

                dadosRegistro.put(disciplinaSemestre, registros);
            }

            return dadosRegistro;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



/*
        var novoRegistroQuantidade = abrirSessaoConjuntodeDados()
                .stream()
                .filter(k -> k.getSituacaoRegistro().equalsIgnoreCase("reprovado") && k.getDisciplinaRegistro() != null)
                .count();
*/

/*
        dadosRegistro.computeIfAbsent(registro.getDisciplinaRegistro(), x -> new ArrayList<>())
                .add(new Registro(registro.getSemestreRegistro(), registro.getQuantidadeDisciplinasRegistro()));*/



