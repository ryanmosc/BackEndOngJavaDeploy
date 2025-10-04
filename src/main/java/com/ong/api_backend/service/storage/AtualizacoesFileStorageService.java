package com.ong.api_backend.service.storage;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class AtualizacoesFileStorageService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private final String folder = "/uploads/eventosImages/";

    /**
     * Salva o arquivo e retorna a URL pública.
     */
    public String salvarArquivo(MultipartFile arquivo) throws IOException {
        validarImagem(arquivo);

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String fileName = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        Path filePath = dirPath.resolve(fileName);

        Files.copy(arquivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + folder + fileName;
    }

    /**
     * Deleta o arquivo com base na URL salva no banco.
     */
    public void deletarArquivo(String imagemUrl) throws IOException {
        if (imagemUrl == null || imagemUrl.isBlank()) return;

        String fileName = imagemUrl.replace(baseUrl + folder, "");
        Path filePath = Paths.get(uploadDir).resolve(fileName);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    private void validarImagem(MultipartFile imagem) {
        String contentType = imagem.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new DadosInvalidosException("Apenas imagens JPEG ou PNG são permitidas");
        }
    }
}
