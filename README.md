# apix2019-microservice-finder-java

### Como usar com docker:

1 - Execute o script 'generate-image.sh', presente na pasta raiz do projeto, para gerar a imagem docker da aplicação;

2 - Execute o comando 'docker-start.sh' para iniciar o container da aplicação;

OBS: Caso tenha feito alguma alteração de código, gere novamente a imagem docker usando o comando 'generate-image.sh' e depois use o comando 'update-finder.sh', na pasta 'docker', para subir um novo container do docker. 

### Como usar localmente:

1 - Localize o arquivo application-conf.json;

  - 1.1 - Edite a propriedade elastic_search.hostname apontando o ip do container elasticsearch_apix2019;

  - 1.2 - Edite a propriedade rabbit.connectionUrl substituindo o valor "rabbitmq" pelo ip do container rabbitmq_apix2019;

  - 1.3 - Para descobrir o ip do container, execute o seguinte comando: docker inspect <container_name>. Valor: IPAddress;
  
2 - Execute o script run.sh.

### Como realizar debug da aplicação:

1 - Execute a aplicação localmente seguindo os passos das instrução anterior.

2 - Crie um remote debug pela IDE, apontando para a porta 8005.

![criando_remote_debug](https://user-images.githubusercontent.com/38056234/58743250-62924700-8404-11e9-8a3f-8c612060d6b0.png)


##### Para acessar o console de administração do RabbitMQ:
http://[docker host IP]:15672/#/

