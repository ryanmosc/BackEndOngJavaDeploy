package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.model.Evento;
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

    public Atualizacoes salvar(Atualizacoes evento, MultipartFile imagem) throws IOException {
        if (evento.getTexto() == null || evento.getTexto().isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        if (imagem != null && !imagem.isEmpty()) {
            String imageUrl = fileStorageService.salvarArquivo(imagem, "eventosImages");
            evento.setImagem(imageUrl);
        }

        return repository.save(evento);
    }

    public Atualizacoes atualizar(Long id, String texto, MultipartFile imagem) throws IOException {
        Atualizacoes evento = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }
        evento.setTexto(texto);

        if (imagem != null && !imagem.isEmpty()) {
            fileStorageService.deletarArquivo(evento.getImagem(), "eventosImages");
            String novaImagem = fileStorageService.salvarArquivo(imagem, "eventosImages");
            evento.setImagem(novaImagem);
        }

        return repository.save(evento);
    }

    public void deletar(Long id) throws IOException {
        Atualizacoes evento = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        fileStorageService.deletarArquivo(evento.getImagem(), "eventosImages");
        repository.deleteById(Math.toIntExact(id));
    }

    public List<Atualizacoes> listarTodos() {
        return repository.findTop3ByOrderByIdDesc();
    }
}