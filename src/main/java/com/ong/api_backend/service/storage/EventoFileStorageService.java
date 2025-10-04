package com.ong.api_backend.service.storage;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class EventoFileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public String salvarArquivo(MultipartFile arquivo, String subpasta) throws IOException {
        validarArquivo(arquivo);

        Path dirPath = Paths.get(uploadDir, subpasta);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        Path caminhoArquivo = dirPath.resolve(nomeArquivo);

        Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/uploads/" + subpasta + "/" + nomeArquivo;
    }

    public void deletarArquivo(String urlArquivo, String subpasta) throws IOException {
        if (urlArquivo == null || urlArquivo.isBlank()) return;

        String nomeArquivo = urlArquivo.replace(baseUrl + "/uploads/" + subpasta + "/", "");
        Path caminhoArquivo = Paths.get(uploadDir, subpasta).resolve(nomeArquivo);

        if (Files.exists(caminhoArquivo)) {
            Files.delete(caminhoArquivo);
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new DadosInvalidosException("Arquivo é obrigatório");
        }

        String tipo = arquivo.getContentType();
        if (tipo == null || (!tipo.equals("image/jpeg") && !tipo.equals("image/png"))) {
            throw new DadosInvalidosException("Apenas imagens JPEG ou PNG são permitidas");
        }
    }
}
