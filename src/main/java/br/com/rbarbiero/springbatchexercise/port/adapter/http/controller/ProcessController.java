package br.com.rbarbiero.springbatchexercise.port.adapter.http.controller;

import br.com.rbarbiero.springbatchexercise.application.ProcessApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.batch.core.JobExecution;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ProcessController {

    final ProcessApplicationService processApplicationService;

    public ProcessController(ProcessApplicationService processApplicationService) {
        this.processApplicationService = processApplicationService;
    }

    @PostMapping("file")
    public ResponseEntity<String> input(MultipartFile file) throws Exception {
        final JobExecution jobExecution = processApplicationService.process(file);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(jobExecution.getJobParameters().getString("uuid"))
                .toUri())
                .body(jobExecution.getStatus().toString());
    }

    @GetMapping("file/{id}")
    public ResponseEntity<FileSystemResource> output(@PathVariable UUID id) {
        final File output = processApplicationService.getFile(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s.csv", id))
                .contentLength(output.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(output));
    }
}
