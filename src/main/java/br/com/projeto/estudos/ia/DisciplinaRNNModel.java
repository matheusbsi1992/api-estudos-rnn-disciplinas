package br.com.projeto.estudos.ia;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class DisciplinaRNNModel {

    private final MultiLayerConfiguration conf;
    private final MultiLayerNetwork model;

    public DisciplinaRNNModel() {
        // Configuração da rede neural
        conf = new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.01)) // Otimizador Adam
                .list()
                .layer(new LSTM.Builder() // Camada LSTM
                        .nIn(1) // Entrada única (Quantidade de inadimplentes)
                        .nOut(64)
                        .activation(Activation.TANH)
                        .build())
                .layer(new RnnOutputLayer.Builder( // Camada de saida
                        LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(1)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();

    }
}
