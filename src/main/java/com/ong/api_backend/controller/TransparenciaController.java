package com.ong.api_backend.controller;

import com.ong.api_backend.model.TransparenciaRequestDTO;
import com.ong.api_backend.util.TransparenciaFileStorage;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transparencia")
@RequiredArgsConstructor
public class TransparenciaController {

    private final TransparenciaFileStorage fileStorage;

    @GetMapping("/visualizar")
    public ResponseEntity<byte[]> visualizarBalancete() {
        return fileStorage.visualizarBalancete();
    }

    @PatchMapping(value = "/enviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarBalancete(@RequestParam ("balancete")MultipartFile balancete,  @RequestParam("texto") String texto){
        fileStorage.salvarBalancete(balancete, texto);
        return ResponseEntity.noContent().build();
    }

}
