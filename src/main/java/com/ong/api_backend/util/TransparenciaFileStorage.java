package com.ong.api_backend.util;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.TransparenciaRequestDTO;
import com.ong.api_backend.model.user.Transparencia;
import com.ong.api_backend.repository.TransparenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransparenciaFileStorage {

    private final TransparenciaRepository transparenciaRepository;

    public void salvarBalancete(MultipartFile file, String texto){

        if (file == null || file.isEmpty()){
            throw  new DadosInvalidosException("Dados enviados estão vazios");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new DadosInvalidosException("Apenas arquivos PDF são permitidos");
        }


        try {
            Transparencia transparencia = new Transparencia(
                    file.getBytes(),
                    texto,
                    LocalDateTime.now()
            );
            transparenciaRepository.save(transparencia);

        }catch (IOException e) {
            throw new DadosInvalidosException("Erro ao ler o arquivo de balancete: " + e.getMessage());
    }

}
    public ResponseEntity<byte[]> visualizarBalancete() {

        Optional<Transparencia> dados = transparenciaRepository.findTopByOrderByCreatedAtDesc();

        if (dados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Transparencia transparencia = dados.get();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"balancete.pdf\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(transparencia.getArquivo().length))
                .body(transparencia.getArquivo());
    }



}
