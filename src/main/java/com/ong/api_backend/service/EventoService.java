package com.ong.api_backend.service;

import com.ong.api_backend.exceptions.DadosInvalidosException;
import com.ong.api_backend.exceptions.DadosNaoEncontrados;
import com.ong.api_backend.model.Evento;
import com.ong.api_backend.repository.EventoRepository;
import com.ong.api_backend.service.storage.EventoFileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final EventoFileStorageService fileStorageService;

    public EventoService(EventoRepository repository, EventoFileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    public Evento salvar(Evento evento, MultipartFile imagem) throws IOException {
        if (evento.getTexto() == null || evento.getTexto().isBlank()) {
            throw new DadosInvalidosException("Texto é obrigatório");
        }

        if (imagem != null && !imagem.isEmpty()) {
            String imageUrl = fileStorageService.salvarArquivo(imagem, "eventosImages");
            evento.setImagem(imageUrl);
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
            fileStorageService.deletarArquivo(evento.getImagem(), "eventosImages");
            String novaImagem = fileStorageService.salvarArquivo(imagem, "eventosImages");
            evento.setImagem(novaImagem);
        }

        return repository.save(evento);
    }

    public void deletar(Long id) throws IOException {
        Evento evento = repository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new DadosNaoEncontrados("Evento não encontrado"));

        fileStorageService.deletarArquivo(evento.getImagem(), "eventosImages");
        repository.deleteById(Math.toIntExact(id));
    }

    public List<Evento> listarTodos() {
        return repository.findTop3ByOrderByIdDesc();
    }
}
