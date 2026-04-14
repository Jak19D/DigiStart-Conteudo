# Configuração do RabbitMQ para DigiStart-Conteudo

## Opções de Instalação

### Opção 1: Docker (Recomendado para Desenvolvimento)
```bash
# Iniciar RabbitMQ com Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Acessar interface web: http://localhost:15672
# Usuário: guest / Senha: guest
```

### Opção 2: Instalação Local (Windows)
1. **Baixe o Erlang/OTP** (pré-requisito):
   - https://erlang.org/download/otp_versions_tree.html
   - Escolha a versão compatível com RabbitMQ

2. **Baixe o RabbitMQ**:
   - https://www.rabbitmq.com/install-windows.html
   - Download do instalador .exe

3. **Instale como Administrador**:
   - Execute ambos os instaladores como Administrator
   - Erlang primeiro, depois RabbitMQ

4. **Iniciar o serviço**:
   ```cmd
   # Como Administrator
   net start RabbitMQ
   ```

### Opção 3: Cloud (Gratuito)
1. Acesse: https://www.cloudamqp.com/
2. Crie conta gratuita
3. Crie um "Little Lemur" instance
4. Use as credenciais fornecidas no application.properties

## Configuração no application.properties

Após instalar, descomente e configure:

```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
```

Para CloudAMQP, use:
```properties
spring.rabbitmq.host=seu-host.cloudamqp.com
spring.rabbitmq.port=5672
spring.rabbitmq.username=seu-usuario
spring.rabbitmq.password=sua-senha
spring.rabbitmq.virtual-host=seu-vhost
```

## Verificação

Após configurar, inicie a aplicação:
```bash
./mvnw spring-boot:run
```

Se tudo estiver correto, não haverá erros de conexão com RabbitMQ.
