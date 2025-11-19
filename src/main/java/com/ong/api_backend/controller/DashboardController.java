package com.ong.api_backend.controller;

import com.ong.api_backend.model.EmailDashboardRequest;
import com.ong.api_backend.repository.FaleConoscoRepository;
import com.ong.api_backend.repository.FormularioCadastroVoluntarioRepository;
import com.ong.api_backend.repository.FormularioDoacaoMensalRepository;
import com.ong.api_backend.service.EmailService;
import com.ong.api_backend.service.RespostaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final EmailService emailService;
    private RespostaService respostaService;
    private final FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository;
    private final FormularioDoacaoMensalRepository formularioDoacaoMensalRepository;
    private final FaleConoscoRepository faleConoscoRepository;

    @Autowired
    public DashboardController(
            EmailService emailService,
            RespostaService respostaService,
            FormularioCadastroVoluntarioRepository formularioCadastroVoluntarioRepository,
            FormularioDoacaoMensalRepository formularioDoacaoMensalRepository,
            FaleConoscoRepository faleConoscoRepository){

        this.emailService = emailService;
        this.faleConoscoRepository = faleConoscoRepository;
        this.formularioDoacaoMensalRepository = formularioDoacaoMensalRepository;
        this.formularioCadastroVoluntarioRepository = formularioCadastroVoluntarioRepository;
        this.respostaService = respostaService;
    }

    @GetMapping("/formularios")
    public ResponseEntity<?> listarTodosForms(){

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("voluntarios", formularioCadastroVoluntarioRepository.findAll());
        resposta.put("doancoesMensais", formularioDoacaoMensalRepository.findAll());
        resposta.put("faleConosco", faleConoscoRepository.findAll());

        return  ResponseEntity.ok(resposta);
    }

    @PostMapping("/email")
    public ResponseEntity<?> enviarRespostaEmail(@RequestBody EmailDashboardRequest request){


        emailService.enviarEmail(
                request.email(),
                request.assunto(),
                request.mensagem()
        );


        respostaService.registrar(
                request.nomeFormulario(),
                request.nome(),
                request.email(),
                request.mensagemOriginal(),
                request.mensagem() // resposta enviada
        );

        return ResponseEntity.ok("Email enviado e resposta salva com sucesso!");
    }

}


