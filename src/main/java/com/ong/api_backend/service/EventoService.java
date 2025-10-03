package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Evento;
import com.ong.api_backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class EventoService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento salvar(Evento evento, MultipartFile imagem) throws IOException {
        if (evento.getTexto() == null || evento.getTexto().isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }
        if (imagem != null && !imagem.isEmpty()) {
            String contentType = imagem.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new DadosInvalidosException("Apenas JPEG ou PNG permitidos");
            }

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);

            Files.copy(imagem.getInputStream(), filePath);

            evento.setImagem(baseUrl + "/uploads/eventosImages/" + fileName);
            System.out.println("Path salvo: " + evento.getImagem());
        }

        return repository.save(evento);
    }

    public Evento atualizar(Long id, String texto, MultipartFile imagem) throws IOException {
        Evento evento = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }
        evento.setTexto(texto);

        if (imagem != null && !imagem.isEmpty()) {
            String contentType = imagem.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new DadosInvalidosException("Apenas JPEG ou PNG permitidos");
            }

            if (evento.getImagem() != null) {
                String oldFileName = evento.getImagem().replace(baseUrl + "/uploads/eventosImages/", "");
                Path oldImagePath = Paths.get(uploadDir).resolve(oldFileName);
                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            }

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String newFileName = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
            Path newFilePath = dirPath.resolve(newFileName);
            Files.copy(imagem.getInputStream(), newFilePath);

            evento.setImagem(baseUrl + "/uploads/eventosImages/" + newFileName);
        }
        return repository.save(evento);
    }

    //Delete mapping
    public void deletar(Long id) {
        Evento evento = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        if (evento.getImagem() != null) {
            // Extrai nome do arquivo do path
            String fileName = evento.getImagem().replace(baseUrl + "/uploads/eventosImages/", "");
            Path imagePath = Paths.get(uploadDir).resolve(fileName);

            try {
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                }
            } catch (IOException e) {
            }
        }

        repository.deleteById(Math.toIntExact(id));
    }

    public List<Evento> listarTodos() {
        return repository.findAll();
    }
}