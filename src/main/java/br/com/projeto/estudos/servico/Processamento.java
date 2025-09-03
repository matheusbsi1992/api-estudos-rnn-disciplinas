package br.com.projeto.estudos.servico;

import br.com.projeto.estudos.dto.RegistroDTO;
import br.com.projeto.estudos.ia.DisciplinaRNNModel;
import br.com.projeto.estudos.modelo.Registro;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static br.com.projeto.estudos.identificacao.PreIdentificacao.abrirSessaoConjuntodeDados;

@RequiredArgsConstructor
@Service
@Getter
@Setter
public class Processamento {

    private static List<DataSet> trainData = new ArrayList<>();
    private static List<DataSet> testData = new ArrayList<>();

    private static DataSetIterator trainIterator = null;
    private static DataSetIterator testIterator = null;

    private static DisciplinaRNNModel disciplinaRNNModel = new DisciplinaRNNModel();

    private static List<RegistroDTO> registroDTOS = new ArrayList<>();

    public static void processamentoIdentificadoDisciplinaTreinoResultado() {

        Map<String, List<Registro>> dadosRegistro = abrirSessaoConjuntodeDados();

        for (String disciplina : dadosRegistro.keySet()) {

            List<Registro> registros = dadosRegistro.get(disciplina);

            int valores[] = registros
                    .stream()
                    .mapToInt(Registro::getQuantidadeDisciplinasRegistro)
                    .toArray();

            /*if (valores.length < 2)
                continue;*/

            // Identificar: dado de periodo para o calculo temporal
            int periodoTempo = 1;

            List<DataSet> dataSets = new ArrayList<>();

            for (int i = 0; i < valores.length - periodoTempo; i++) {

                // Input: 1 valor
                INDArray input = Nd4j.create(new double[]{valores[i]}).reshape(1, periodoTempo, 1);

                // Label: próximo valor
                INDArray label = Nd4j.create(new double[]{valores[i + 1]}).reshape(1, 1, 1);

                dataSets.add(new DataSet(input, label));

            }
            divisaoTreinoTeste(dataSets);
            previsaoProximoDozeMeses(periodoTempo, valores, disciplina);
        }
    }

    private static void divisaoTreinoTeste(List<DataSet> dataSets) {

        Collections.shuffle(dataSets);

        int divisaoDataset = (int) (dataSets.size() * 0.8);

        trainData = dataSets.subList(0, divisaoDataset);
        testData = dataSets.subList(divisaoDataset, dataSets.size());

        trainIterator = new ListDataSetIterator<>(trainData, 1);
        testIterator = new ListDataSetIterator<>(testData, 1);
    }

    private static void treinamentoRedeNeural() {

        int numeroDePassagens = 100;

        for (int i = 0; i < numeroDePassagens; i++) {

            disciplinaRNNModel.getModel().fit(trainIterator);
            trainIterator.reset();
            testIterator.reset();

        }
    }

    private static List<RegistroDTO> previsaoProximoDozeMeses(int periodoTempo, int valores[], String disciplina) {

        treinamentoRedeNeural();

        INDArray entrada = Nd4j.create(new double[]{valores[valores.length - 1]}).reshape(1, periodoTempo, 1);

        List<Double> previsoes = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            INDArray saida = disciplinaRNNModel.getModel().output(entrada, false);

            double valor = saida.getDouble(0);
            previsoes.add(valor);

            // Atualiza entrada com a nova previsão
            entrada = Nd4j.create(new double[]{valor}).reshape(1, periodoTempo, 1);
        }

        return resultadoRealizado(disciplina, previsoes);
    }

    private static List<RegistroDTO> resultadoRealizado(String disciplina, List<Double> previsoes) {
        System.out.println("\n \uD83D\uDCDA Disciplina: " + disciplina);

        int ano = 2025;

        for (int i = 0; i < previsoes.size(); i++) {
            int semestre = (i % 2) + 1;
            if (semestre == 1 && i != 0) ano++;
            if (previsoes.get(i) < 0)
                continue;
            BigDecimal quantidadeResultados = new BigDecimal(Double.toString(previsoes.get(i)))
                    .setScale(2, 0);

            registroDTOS.add(new RegistroDTO(disciplina, ano, semestre, quantidadeResultados.doubleValue()));
            System.out.printf("✍\uD83C\uDFFC reprovados & rep. falta & cancelado %d.%d ➡ %.1f  %n", ano, semestre, previsoes.get(i));
        }
        return registroDTOS;
    }

    public static List<RegistroDTO> listarValoresResultados() {
        return registroDTOS
                .stream()
                .toList();
    }

}