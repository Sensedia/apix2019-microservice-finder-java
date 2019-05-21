# apix2019-microservice-finder-java

Versão em vertx do microserviço de finder para o APIX2019

Como usar localmente:

1- Acesse a pasta 'src/main/application-conf.json' e altere o valor do domínio do 'connectionUrl' para 'localhost'

1- Gere o jar do projeto usando o comando 'mvn clean install';

2- Execute o comando 'run.sh' para iniciar a aplicação;

Usar com instância docker:

1- Execute o comando 'sh generate-image.sh' para gerar a imagem docker do projeto

2- Acesse a pasta 'docker' e execute o comando 'sh docker-start.sh' para iniciar o container;

OBS: Lembre-se de subir uma instância de RabbitMQ via docker e coloca-lá no network 'apix2019'. 

OBS1: Caso esteja usando o finder em docker, certifique-se de que o valor do domínio no atributo 'connectionUrl' do arquivo 'src/main/application-conf.json'está apontando para o docker do rabbitmq, e não para 'localhost';

OBS2: Caso a fila no RabbitMQ não exista, a aplicação criará automaticamente.

