package br.com.fiap.bank.plataforma.application.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record AtualizarStatusRequest(
    @NotBlank @Pattern(regexp="APROVADO|REPROVADO") String status,
    String parecerOperador) {}
