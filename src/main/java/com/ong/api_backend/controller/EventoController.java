package com.ong.api_backend.controller;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.service.EventoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gerencia/eventos")
public class EventoController {
    private final EventoService service;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Evento> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {
        Evento evento = new Evento();
        evento.setTexto(texto);
        Evento saved = service.salvar(evento, imagem);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {
        Evento updated = service.atualizar(id, texto, imagem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) throws IOException {
        service.deletar(id);
        return ResponseEntity.ok("Evento deletado com sucesso");
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos() {
        List<Evento> eventos = service.listarTodos();
        return ResponseEntity.ok(eventos);
    }
}
