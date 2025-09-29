package com.ong.api_backend.service;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class EventoService {
    @Value("${app.upload.dir}")  // Pega do yml
    private String uploadDir;

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento salvar(Evento evento, MultipartFile imagem) throws IOException {
        if (evento.getTexto() == null || evento.getTexto().isBlank()) {
            throw new RuntimeException("Texto é obrigatório");
        }
        if (imagem != null && !imagem.isEmpty()) {
            String contentType = imagem.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new RuntimeException("Apenas JPEG ou PNG permitidos");
            }


            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);  // Caminho completo


            Files.copy(imagem.getInputStream(), filePath);


            evento.setImagem("/uploads/" + fileName);
        }


        return repository.save(evento);
    }
}