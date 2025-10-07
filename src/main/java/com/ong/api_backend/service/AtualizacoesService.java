package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.repository.AtualizacoesRepository;
import com.ong.api_backend.service.storage.AtualizacoesFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AtualizacoesService {
    private static final Logger logger = LoggerFactory.getLogger(AtualizacoesService.class);

    private final AtualizacoesRepository repository;
    private final AtualizacoesFileStorageService fileStorageService;

    public AtualizacoesService(AtualizacoesRepository repository, AtualizacoesFileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        logger.debug("AtualizacoesService initialized");
    }

    public Atualizacoes salvar(Atualizacoes atualizacao, MultipartFile imagem) throws IOException {
        logger.info("Attempting to save Atualizacoes with texto: {}", atualizacao.getTexto());
        if (atualizacao.getTexto() == null || atualizacao.getTexto().isBlank()) {
            logger.error("Texto is mandatory for Atualizacoes");
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        try {
            if (imagem != null && !imagem.isEmpty()) {
                logger.debug("Processing image upload for Atualizacoes");
                String imageUrl = fileStorageService.salvarArquivo(imagem);
                atualizacao.setImagem(imageUrl);
                logger.info("Image uploaded successfully: {}", imageUrl);
            }
            Atualizacoes saved = repository.save(atualizacao);
            logger.info("Atualizacoes saved successfully with ID: {}", saved.getId());
            return saved;
        } catch (IOException e) {
            logger.error("Error saving Atualizacoes: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Atualizacoes atualizar(Integer id, String texto, MultipartFile imagem) throws IOException {
        logger.info("Attempting to update Atualizacoes with ID: {}", id);
        Atualizacoes atualizacao = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Atualizacoes not found with ID: {}", id);
                    return new DadosNaoEncontrados("Atualização não encontrada");
                });

        if (texto == null || texto.isBlank()) {
            logger.error("Texto is mandatory for updating Atualizacoes with ID: {}", id);
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        try {
            atualizacao.setTexto(texto);
            if (imagem != null && !imagem.isEmpty()) {
                logger.debug("Processing image update for Atualizacoes with ID: {}", id);
                fileStorageService.deletarArquivo(atualizacao.getImagem());
                String novaImagemUrl = fileStorageService.salvarArquivo(imagem);
                atualizacao.setImagem(novaImagemUrl);
                logger.info("Image updated successfully for Atualizacoes ID: {}", id);
            }
            Atualizacoes updated = repository.save(atualizacao);
            logger.info("Atualizacoes updated successfully with ID: {}", id);
            return updated;
        } catch (IOException e) {
            logger.error("Error updating Atualizacoes with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deletar(Integer id) {
        logger.info("Attempting to delete Atualizacoes with ID: {}", id);
        Atualizacoes atualizacao = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Atualizacoes not found with ID: {}", id);
                    return new DadosNaoEncontrados("Atualização não encontrada");
                });

        try {
            fileStorageService.deletarArquivo(atualizacao.getImagem());
            logger.debug("Image deleted for Atualizacoes with ID: {}", id);
            repository.delete(atualizacao);
            logger.info("Atualizacoes deleted successfully with ID: {}", id);
        } catch (IOException e) {
            logger.warn("Failed to delete image for Atualizacoes with ID {}: {}", id, e.getMessage());
            repository.delete(atualizacao);
            logger.info("Atualizacoes deleted successfully with ID: {}", id);
        }
    }

    public List<Atualizacoes> listarTodos() {
        logger.info("Listing all Atualizacoes");
        List<Atualizacoes> atualizacoes = repository.findAll();
        logger.debug("Returning {} Atualizacoes", atualizacoes.size());
        return atualizacoes;
    }
}