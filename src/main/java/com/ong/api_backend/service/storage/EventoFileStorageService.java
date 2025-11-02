package com.ong.api_backend.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ong.api_backend.exceptions.DadosInvalidosException;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class EventoFileStorageService {

    private final Cloudinary cloudinary;

    public EventoFileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String salvarArquivo(MultipartFile arquivo, String subpasta) throws IOException {
        validarArquivo(arquivo);
        try {
            Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(),
                    ObjectUtils.asMap("folder", subpasta));
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new IOException("Erro ao enviar imagem para o Cloudinary: " + e.getMessage(), e);
        }
    }

    public void deletarArquivo(String urlArquivo, String subpasta) {

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
