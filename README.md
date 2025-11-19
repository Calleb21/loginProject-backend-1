# 💻 loginProject-backend: Sistema de Autenticação e Gestão de Usuários

Este projeto é o backend de um sistema de login e cadastro, desenvolvido em Java com o framework Spring Boot. Ele foi projetado para fornecer uma API RESTful segura e robusta para gerenciar o ciclo de vida de autenticação de usuários, incluindo cadastro, login com JWT e um mecanismo de segurança contra ataques de força bruta.

## 🚀 Tecnologias Utilizadas

*   **Linguagem:** Java
*   **Framework:** Spring Boot
*   **Gerenciador de Dependências:** Maven
*   **Segurança:** Spring Security (para `BCryptPasswordEncoder`)
*   **Banco de Dados:** (Assumindo um banco de dados relacional, como H2, MySQL ou PostgreSQL, configurado via `application.properties`)
*   **Documentação:** Swagger/OpenAPI (conforme anotações no código)

## ✨ Funcionalidades Principais

O backend expõe três endpoints principais, com foco em segurança e tratamento de erros:

1.  **Cadastro de Usuário (Signup):** Permite o registro de novos usuários com validação de e-mail único.
2.  **Autenticação (Login):** Valida as credenciais e emite um Token JWT para sessões seguras.
3.  **Redefinição de Senha (Reset Password):** Permite a alteração da senha do usuário.

## 🛡️ Destaques de Segurança

*   **Criptografia de Senha:** Utiliza o **`BCryptPasswordEncoder`** para garantir que as senhas sejam armazenadas de forma segura (hashing irreversível).
*   **Autenticação Stateless:** Implementa o padrão **JWT (JSON Web Token)** para criar sessões sem estado, onde o token assinado é a única credencial necessária após o login.
*   **Mecanismo Anti-Força Bruta:** O serviço de autenticação (`UsuarioService`) possui uma lógica que **bloqueia o usuário por 5 minutos** após 5 tentativas de login mal-sucedidas, mitigando ataques automatizados.

## 📐 Arquitetura do Projeto

O projeto segue a arquitetura em camadas, padrão em aplicações Spring:

| Camada | Classe Principal | Responsabilidade |
| :--- | :--- | :--- |
| **Controller** | `UsuarioController` | Recebe requisições HTTP, valida DTOs e delega a lógica para o Service. |
| **Service** | `UsuarioService` | Contém a lógica de negócio (regras de validação, criptografia, bloqueio). |
| **Repository** | `UsuarioRepository` | Interface de acesso e persistência de dados (JPA/Hibernate). |
| **Utils** | `JwtUtil` | Responsável pela geração e manipulação do Token JWT. |

## 🔗 Endpoints da API

Todos os endpoints estão sob o prefixo `/api/usuarios`.

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/usuarios/signup` | Cadastra um novo usuário. |
| `POST` | `/api/usuarios/login` | Autentica o usuário e retorna o Token JWT. |
| `POST` | `/api/usuarios/resetPassword` | Altera a senha do usuário. |

## 🛠️ Como Iniciar o Projeto

### Pré-requisitos

*   Java Development Kit (JDK) 17+
*   Maven
*   Git

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/Calleb21/loginProject-backend-1.git
    cd loginProject-backend-1/loginProjectBackend
    ```
2.  **Configuração do Banco de Dados:**
    *   Verifique e ajuste as configurações de conexão do banco de dados no arquivo `src/main/resources/application.properties` (ou `application.yml`).
3.  **Compilar e Executar:**
    ```bash
    ./mvnw spring-boot:run
    ```
    O servidor estará acessível em `http://localhost:8080`.

## 📞 Contato

Para dúvidas, sugestões ou colaborações, sinta-se à vontade para entrar em contato:

| Plataforma | Link |
| :--- | :--- |
| **LinkedIn** | [linkedin.com/in/calleb-camargo-682321237](https://www.linkedin.com/in/calleb-camargo-682321237) |
| **GitHub** | [github.com/Calleb21](https://github.com/Calleb21) |
| **E-mail** | camargocalleb12@gmail.com |
