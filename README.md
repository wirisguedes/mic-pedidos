# pedidos

Projeto Spring Boot para gestão de pedidos, com integração a Kafka, PostgreSQL e comunicação via Feign.

## Tecnologias
- Java 21
- Spring Boot 3.5.0
- Spring Data JPA
- Spring Kafka
- Spring Cloud OpenFeign
- MapStruct
- Lombok
- PostgreSQL
- Docker Compose (serviços externos)

## Estrutura
- `src/main/java`: código-fonte principal
- `src/test/java`: testes automatizados
- `src/main/resources`: configurações (ex: application.properties)
- Integração com outros serviços via Kafka e Feign

## Comunicação entre serviços

### Kafka
O serviço de pedidos utiliza tópicos Kafka para comunicação assíncrona com outros microsserviços:
- **Pedidos Pagos:** Ao registrar um pagamento, publica um evento no tópico `${icompras.config.kafka.topics.pedidos-pagos}`.
- **Pedidos Faturados e Enviados:** Consome eventos dos tópicos `${icompras.config.kafka.topics.pedidos-faturados}` e `${icompras.config.kafka.topics.pedidos-enviados}` para atualizar o status do pedido.
- A configuração dos tópicos e servidores Kafka é feita via `application.properties`.

#### Exemplo de publicação (pagamento):
```java
pagamentoPublisher.publicar(pedido);
```

#### Exemplo de consumo (atualização de status):
```java
@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = { ... })
public void receberAtualizacao(String payload) { ... }
```

### Feign
A comunicação síncrona com outros microsserviços é realizada via Feign Clients, facilitando chamadas HTTP REST:
- Os clientes Feign são configurados no pacote `pedidos.client`.
- A anotação `@EnableFeignClients` ativa a detecção automática dos clientes.

#### Exemplo de uso:
```java
@FeignClient(name = "servico-exemplo", url = "http://localhost:8081")
public interface ExemploClient {
    @GetMapping("/api/exemplo/{id}")
    ExemploDTO buscarPorId(@PathVariable Long id);
}
```

## Requisitos
- Java 21+
- Maven
- Docker e Docker Compose (para dependências externas)

## Como executar
1. **Suba os serviços necessários:**
   - Kafka/Zookeeper: `docker-compose -f ../broker/docker-compose.yml up -d`
   - PostgreSQL: `docker-compose -f ../database/docker-compose.yml up -d`
2. **Compile e rode o projeto:**
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Testes
Execute:
```sh
mvn test
```

## Observações
- O projeto utiliza tópicos Kafka para eventos de pedidos pagos, faturados e enviados.
- Comunicação com outros microsserviços via Feign.
- Dados sensíveis e arquivos temporários estão protegidos pelo `.gitignore`.
- As configurações de endpoints, tópicos e URLs dos serviços devem ser ajustadas conforme o ambiente.
