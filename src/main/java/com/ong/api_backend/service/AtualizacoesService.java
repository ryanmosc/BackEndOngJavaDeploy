package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Atualizacoes;
import com.ong.api_backend.repository.AtualizacoesRepository;
import com.ong.api_backend.service.storage.AtualizacoesFileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AtualizacoesService {

    private final AtualizacoesRepository repository;
    private final AtualizacoesFileStorageService fileStorageService;

    public AtualizacoesService(AtualizacoesRepository repository, AtualizacoesFileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    public Atualizacoes salvar(Atualizacoes atualizacao, MultipartFile imagem) throws IOException {
        if (atualizacao.getTexto() == null || atualizacao.getTexto().isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        if (imagem != null && !imagem.isEmpty()) {
            String imageUrl = fileStorageService.salvarArquivo(imagem);
            atualizacao.setImagem(imageUrl);
        }

        return repository.save(atualizacao);
    }

    public Atualizacoes atualizar(Integer id, String texto, MultipartFile imagem) throws IOException {
        Atualizacoes atualizacao = repository.findById(id)
                .orElseThrow(() -> new DadosNaoEncontrados("Atualização não encontrada"));

        if (texto == null || texto.isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        atualizacao.setTexto(texto);

        if (imagem != null && !imagem.isEmpty()) {
            fileStorageService.deletarArquivo(atualizacao.getImagem());
            String novaImagemUrl = fileStorageService.salvarArquivo(imagem);
            atualizacao.setImagem(novaImagemUrl);
        }

        return repository.save(atualizacao);
    }

    public void deletar(Integer id) {
        Atualizacoes atualizacao = repository.findById(id)
                .orElseThrow(() -> new DadosNaoEncontrados("Atualização não encontrada"));

        try {
            fileStorageService.deletarArquivo(atualizacao.getImagem());
        } catch (IOException ignored) {}

        repository.delete(atualizacao);
    }

    public List<Atualizacoes> listarTodos() {
        return repository.findAll();
    }
}
