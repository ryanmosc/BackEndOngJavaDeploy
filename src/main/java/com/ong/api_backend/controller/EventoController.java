package com.ong.api_backend.controller;

import com.ong.api_backend.model.Evento;
import com.ong.api_backend.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Evento> adicionar(
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        Evento evento = new Evento();
        evento.setTexto(texto);

        try {
            Evento saved = service.salvar(evento, imagem);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")  // Novo: Update por ID
    public ResponseEntity<Evento> atualizar(
            @PathVariable Long id,
            @RequestParam("texto") String texto,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        try {
            Evento updated = service.atualizar(id, texto, imagem);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();  // 404 se ID não existir
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.ok("Evento deletado");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
