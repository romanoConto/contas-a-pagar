<h1 align="center">
  Spring Security JWT
</h1>

Romano de Conto Pasqualotto - [Linkedin](www.linkedin.com/in/romano-de-conto-pasqualotto)

## Intro
Desafio proposto pela TOTVS:

Aplicação web api simples de gerenciamento de contas, contendo api Rest com documentação swagger, implementação de autenticação basic e jwt (utilizando basic nas requisicoes do swagger), utilizando versões mais atualizadas das tecnologias.

## Tecnologias

* [Java 23](https://www.oracle.com/java/technologies/javase-jdk23-doc-downloads.html)
* [Postgres Sql](https://www.postgresql.org/docs/)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.3/reference/web/servlet.html)
* [Spring Doc](https://springdoc.org)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://spring.io/projects/spring-security)
* [Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
* [Opencsv](https://opencsv.sourceforge.net)
* [OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.4.3/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.4.3/reference/features/dev-services.html#features.dev-services.docker-compose)

## Como debugar com intellij

- Clonar repositório git:

```
git clone https://github.com/romanoConto/contas-a-pagar.git
```

- Instalar o apache-maven-3.9.9
```
https://maven.apache.org/download.cgi
```

- Instalar o Java 23-jdk
```
https://www.oracle.com/br/java/technologies/downloads/#java23
```

- Instalar o docker
```
https://www.docker.com
```

- Instalar o docker-compose 
```
https://docs.docker.com/compose/
 ```

- Debugar pela IDE ou executar pelo comando
```
mvn sprin-boot:run
```

- Acessar o swagger
```
http://localhost:8080/swagger-ui/index.html#
```

- Utilizar as credenciais do usuario inicial
```
usuario:admin
senha:admin
```

- Deslogar:
```
logout:logout@http://localhost:8080/swagger-ui/index.html#
```

## Como gerar artefato e rodar api no docker:

Para coseguir executar o comando mvn clean install é necessario ter o banco de dados no ar, entao para conseguir gerar o artefato, e depois rodar toda a aplicação em um unico container, vamos precisar de dois terminais e executar na sequencia correta.

- Abrir 2 terminais
- No terminal 1 executar o comando para subir o banco de dados:
```
docker-compose up
```

- No terminal 2 executar para gerar o artefato:
```
mvn clean install
```

- No terminal 1 parar a execução do docker com o comando:
```
ctrl+c
```

- No terminal 2 executar o comando para gerar a imagem do docker:
```
docker build -t contas-a-pagar-api .
```

- No terminal 2 executar a api:
```
docker-compose -f compose-api.yaml up --build
```

- Logar
```
usuario: admin
senha: admin
```

- Testar:

``` 
http://localhost:8080/swagger-ui/index.html#
```
- Deslogar:
```
logout:logout@http://localhost:8080/swagger-ui/index.html#
```