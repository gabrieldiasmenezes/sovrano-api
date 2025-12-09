# Sovrano - Sistema de Reserva Online 🍽️

## ✨ **Descrição do Projeto**

**Sovrano** é um sofisticado sistema de reservas online desenvolvido para garantir uma experiência ágil e elegante aos clientes de um restaurante de alto padrão. Pensado para ser intuitivo e eficiente, o **Sovrano** permite que os usuários façam suas reservas de forma simples, mantendo sempre a excelência e sofisticação que o público espera.

Este sistema oferece não apenas funcionalidades práticas, mas também uma interface de alto nível para que cada interação com o restaurante seja uma experiência memorável.

---

# Sovrano — API de Reservas

Este repositório contém a API de reservas "Sovrano", uma aplicação backend em Java (Spring Boot) que gerencia reservas, filas de espera (waitlist), usuários, mesas e controles administrativos para um restaurante.

O README abaixo descreve as funcionalidades, como executar a aplicação em desenvolvimento (com Postgres e MailHog), como testar endpoints básicos e onde encontrar documentação adicional (Swagger/OpenAPI).

## Conteúdo

- Visão geral
- Funcionalidades principais
- Tecnologias
- Como executar (desenvolvimento)
- Configurações importantes
- Endpoints principais
- Debug de e-mail (MailHog)
- Testes e comandos úteis
- Observações e próximos passos

## Visão geral

Sovrano expõe uma API REST para: gerenciar reservas (criar, confirmar, cancelar, completar), administrar a fila de espera com notificação automática, gerenciar usuários e mesas, e endpoints administrativos protegidos por JWT.

A aplicação usa Spring Security (JWT) para autenticação e autorização. Alguns endpoints são públicos (login), a maioria exige um token Bearer.

## Funcionalidades principais

- Autenticação via JWT (`/login`).
- CRUD de reservas (endpoints para clientes e administração).
- Fila de espera (waitlist): entrar na fila, notificar próximo, expirar, posições por período/data.
- Notificações por e-mail (suporte a MailHog em dev e integração com provedores SMTP reais).
- Endpoints administrativos para gerenciamento de mesas, blackout times e visualização da waitlist.
- Swagger/OpenAPI annotations para documentação automática.

## Tecnologias

- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- Maven
- PostgreSQL (produção/dev via Docker)
- MailHog (dev SMTP testing)

## Como executar (desenvolvimento)

Pré-requisitos:

- Java 17
- Maven (ou use `./mvnw` no projeto)
- Docker / Docker Compose

1) Subir dependências (Postgres + MailHog) via Docker Compose:

```bash
docker compose up -d
```

O `compose.yaml` já inclui serviços para `postgres` e `mailhog`.

2) Ajustar variáveis de ambiente (opcionais)

As seguintes variáveis podem ser definidas no ambiente ou no arquivo `application.properties`:

- `DB_HOST` (padrão: `localhost`)
- `DB_PORT` (padrão: `5432`)
- `DB_NAME` (padrão: configurado em `application.properties`)
- `DB_USER` / `DB_PASS`
- `app.frontend.url` — usado nas mensagens de confirmação enviadas por e-mail.

3) Executar a aplicação:

```bash
./mvnw spring-boot:run
```

Após a aplicação subir, a API estará disponível em `http://localhost:8080`.

## Configurações importantes

- Banco de dados: `spring.datasource.*` configurado para PostgreSQL em `application.properties`.
- SMTP de desenvolvimento: configurado para `localhost:1025` (MailHog). Se usar um provedor real, substitua as propriedades `spring.mail.*`.
- `waitlist.confirmation.timeout.minutes` — tempo em minutos que um usuário tem para confirmar a reserva temporária ao ser notificado da fila.

## Endpoints principais (resumo)

Observação: a maioria dos endpoints listados exige autenticação via JWT (Bearer token).

- `POST /login` — autenticação; envia `{ "email": "...", "password": "..." }` e retorna `{ "token": "..." }`.

- Reservas (cliente):
	- `GET /reservations/me` — listar minhas reservas (paginação + filtro por status).
	- `POST /reservations/me` — criar reserva (autenticado).
	- `PUT /reservations/me/{id}/confirm` — confirmar minha reserva.
	- `PUT /reservations/me/{id}/cancel` — cancelar minha reserva.
	- `PUT /reservations/me/{id}` — atualizar minha reserva.

- Reservas (admin):
	- `GET /reservations` — listar reservas com filtros (ADMIN).
	- `GET /reservations/user/{email}` — listar reservas de um usuário (ADMIN).
	- `GET /reservations/table/{tableId}` — reservas por mesa (ADMIN).
	- `POST /reservations` — criar reserva manual (ADMIN).
	- `PUT /reservations/{id}/confirm` — confirmar reserva (ADMIN).
	- `PUT /reservations/{id}/cancel` — cancelar reserva (ADMIN).
	- `PATCH /reservations/{id}/complete` — marcar como concluída.
	- `PUT /reservations/{id}` — atualizar (ADMIN).
	- `DELETE /reservations/{id}` — deletar (ADMIN).

- Waitlist (cliente):
	- `GET /waitlist/me` — listar minhas entradas na fila.
	- `DELETE /waitlist/me/{id}` — sair da fila.
	- `GET /waitlist/me/position` — posição em data e período.

- Waitlist (admin):
	- `POST /waitlist/join` — adicionar (também existe fluxo cliente).
	- `GET /waitlist/admin` — visualizar fila por data/periodo.
	- `PUT /waitlist/{id}/notify` — notificar manualmente e criar reserva PENDING.
	- `PUT /waitlist/{id}/expire` — expirar entrada.
	- `DELETE /waitlist/{id}` — deletar entrada.

- Utilitários:
	- `GET /admin/test-email?email=you@example.com&name=...` — envia e-mail de teste (útil para debug de SMTP/MailHog).
	- Swagger UI (se habilitado) geralmente disponível em `/swagger-ui.html` ou `/swagger-ui/index.html`.

## Debug de e-mail (MailHog)

Se você estiver em ambiente de desenvolvimento, a solução mais rápida para visualizar e-mails é usar MailHog. O compose já inclui MailHog com:

- SMTP: `localhost:1025`
- UI: `http://localhost:8025`

Fluxo de verificação rápida:

1. Suba o compose: `docker compose up -d`.
2. Rode a aplicação: `./mvnw spring-boot:run`.
3. Chame o endpoint de teste: `curl "http://localhost:8080/admin/test-email?email=seu.email@exemplo.com&name=Carla"`.
4. Abra `http://localhost:8025` e verifique a mensagem na interface do MailHog.

Se o log da aplicação mostrar algo como `[Mail fallback] To: ...`, significa que a bean `JavaMailSender` não foi criada — verifique se o MailHog está rodando e se a aplicação foi reiniciada após alterações em `application.properties`.

## Testes

Para compilar e empacotar (ignora testes):

```bash
./mvnw -DskipTests package
```

Para rodar testes (requer DB disponível e configuração adequada):

```bash
./mvnw test
```


