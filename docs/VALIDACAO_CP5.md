# Validação do CP5 - 17/10/2026

Matheus Bispo Faria Barbosa - RM562140 - entrega individual.

## Resultado verificado

Os três projetos Java passaram em `mvn clean verify` usando **Java 21.0.12.1** e Spring Boot 4.1.1.
O Angular passou em produção nos perfis padrão e `enunciado`, além de iniciar pelo perfil do enunciado.
Foram aprovados **21 testes automatizados** (3 Core, 9 Plataforma, 3 Mesa e 6 Angular/Chrome Headless),
**14 verificações de integração real** e **4 verificações negativas de arquitetura**.
O PDF do cartão-resposta foi renderizado e suas duas páginas foram inspecionadas.

| Critério da rubrica | Peso | Evidência / situação |
|---|---:|---|
| DDD e módulos Maven | 1,0 | Dois backends com quatro módulos; Enforcer e verificação estática; domínio sem dependências externas. |
| Refatoração DDD e Lombok | 1,0 | Entidades JPA separadas; adapters; Lombok nos dois backends; Cpf, Email e Saldo; motor de concessão puro. |
| RESTful | 2,0 | CRUD do Core; recursos de crédito e PUT de status; contratos legados preservados em controller adaptador. |
| Spring Data JPA | 2,0 | Entidades, repositórios Spring Data e adapters na infraestrutura de ambos os backends; H2 real nos testes. |
| Validação e exceções | 1,0 | Records com Bean Validation; @Valid nas rotas legadas; handlers globais; testes de 400, 404 e 409. |
| Retrocompatibilidade | 1,0 | Feign, formulários Thymeleaf, contratos Angular e decisões de aprovação/reprovação testados. |
| Avaliação teórica | 2,0 | Cartão-resposta em Markdown e PDF, baseado no código inicial 3515708. Requer revisão e domínio do aluno. |

Esta tabela é uma conferência de evidências, não uma garantia de nota.

## Alterações relevantes

- Portas e URLs dos frontends corrigidas; configuração da Plataforma movida para o módulo executável.
- Core continua em `presentation/src/main/resources/application.properties`, com `server.port=8081`.
- Perfil `enunciado` permite Core 8080 e Plataforma 8081, conforme o PDF, sem apagar o padrão local.
- Integração preserva a lista retornada pelo Core e o mapa consumido pelo legado; distingue 404 de falha de rede/serviço.
- Angular registra o locale brasileiro, valida entradas, evita envio simultâneo e não incrementa o saldo sem operação no Core.
- Falha no histórico é apresentada como erro, em vez de lista vazia; extrato demonstrativo é identificado na tela.
- Mesa usa a mesma versão Spring Cloud da Plataforma; corrigida expressão Angular indevida em template Thymeleaf.
- Rotas REST de decisão passam a usar `/creditos/{id}/status`; aprovação/reprovação legadas continuam funcionando.
- Propostas finalizadas não aceitam decisão contraditória.

## Limites e pendências reais

1. **Desembolso automático ainda não implementado.** A aprovação altera a proposta e simula a notificação; não deposita no Core.
   A consulta ao Core permanece informativa e não bloqueia a concessão por conta inativa, sem conta ou indisponibilidade.
   Isso não cobre integralmente a jornada de negócio descrita na seção 1, embora os fluxos explicitamente avaliados acima funcionem.
2. SCR, Bureau, CRM e notificações são simulações. Não há autenticação nem garantias de transação distribuída.
3. O H2 perde os dados ao reiniciar. O Core inicia vazio; somente a Plataforma pré-carrega propostas.
4. Os IDs públicos continuam Long e a API de conta conserva `ativa`; o diagrama conceitual apresenta UUID e outros estados.
   A compatibilidade foi preservada, portanto não se declara equivalência literal de todo o diagrama.
5. A operação incremental de saldo usa PUT conforme o enunciado, mas não é idempotente sem chave de operação.
6. As alterações desta revisão precisam de commit e push. Nenhum push foi executado.
   O repositório remoto foi confirmado como fork correto do projeto do professor; antes da revisão havia 16 commits locais à frente.
7. Revisar o cartão-resposta e anexar PDF, ZIP e link do GitHub no Teams. O PDF menciona "Checkpoint 4" na seção de entrega do CP5;
   confirmar a tarefa correta no Teams. Nenhuma submissão externa foi realizada.

## Integração real

Foram usados processos isolados nas portas 18081/18080/18082, encerrados ao final, sem alterar as instâncias do usuário.

- Criacao real de cliente
- Criacao real de conta
- Feign real: conta ativa e saldo 12700
- 404 real do Core vira SEM_CONTA
- Criacao de proposta para aprovar
- Thymeleaf integrado: analise 4
- Formulario real da Mesa: aprovar
- Contrato do Angular recebe APROVADO
- Criacao de proposta para reprovar
- Thymeleaf integrado: analise 5
- Formulario real da Mesa: reprovar
- Contrato do Angular recebe REPROVADO
- Conta inativa mantem saldo real 12700
- Core desligado vira INDISPONIVEL

## Proteção arquitetural

Em cada backend, uma referência proibida na apresentação e uma dependência Spring no domínio foram inseridas temporariamente.
Os quatro builds falharam como esperado; os arquivos foram restaurados em seguida.

- fiap-bank-core-bancario: referencia proibida bloqueada
- fiap-bank-core-bancario: dependencia Spring no dominio bloqueada
- fiap-bank-plataforma-credito: referencia proibida bloqueada
- fiap-bank-plataforma-credito: dependencia Spring no dominio bloqueada
