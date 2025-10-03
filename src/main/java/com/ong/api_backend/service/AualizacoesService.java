package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.repository.AtualizacoesRepository;
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
public class AualizacoesService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private final AtualizacoesRepository repository;

    public AualizacoesService(AtualizacoesRepository repository) {
        this.repository = repository;
    }

    public Atualizacoes salvar(Atualizacoes atualizacoes, MultipartFile imagem) throws IOException {
        if (atualizacoes.getTexto() == null || atualizacoes.getTexto().isBlank()) {
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

            atualizacoes.setImagem(baseUrl + "/uploads/eventosImages/" + fileName);
            System.out.println("Path salvo: " + atualizacoes.getImagem());
        }

        return repository.save(atualizacoes);
    }

    public Atualizacoes atualizar(Integer id, String texto, MultipartFile imagem) throws IOException {
        Atualizacoes atualizacoes = repository.findById(Math.toIntExact(id))
                .orElseThrow();

        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }
        atualizacoes.setTexto(texto);

        if (imagem != null && !imagem.isEmpty()) {
            String contentType = imagem.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new DadosInvalidosException("Apenas JPEG ou PNG permitidos");
            }

            if (atualizacoes.getImagem() != null) {
                String oldFileName = atualizacoes.getImagem().replace(baseUrl + "/uploads/eventosImages/", "");
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

            atualizacoes.setImagem(baseUrl + "/uploads/eventosImages/" + newFileName);
        }
        return repository.save(atualizacoes);
    }

    //Delete mapping
    public void deletar(Long id) {
        Atualizacoes atualizacoes = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        if (atualizacoes.getImagem() != null) {
            // Extrai nome do arquivo do path
            String fileName = atualizacoes.getImagem().replace(baseUrl + "/uploads/eventosImages/", "");
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

    public List<Atualizacoes> listarTodos() {
        return repository.findAll();
    }
}
