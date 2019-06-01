# apix2019-microservice-finder-java :rocket:

### Como executar via docker:

1 - Execute o script 'generate-image.sh', presente na pasta raiz do projeto, para gerar a imagem docker da aplicação;

2 - Execute o comando 'docker-start.sh' para iniciar o container da aplicação;

OBS: Caso tenha feito alguma alteração de código, gere novamente a imagem docker usando o comando 'generate-image.sh' e depois use o comando 'update-finder.sh', na pasta 'docker', para subir um novo container do docker. 

### Como executar localmente:

1 - Localize o arquivo application-conf.json;

  - Altere o valor da propriedade elastic_search.hostname para `localhost`;

  - Altere o valor da propriedade rabbit.connectionUrl para `amqp://localhost`;

2 - Execute o script run.sh.

### Como fazer debug da aplicação:

1 - Execute a aplicação localmente seguindo os passos das instrução anterior;

2 - Crie um remote debug pelo IntelliJ, apontando para a porta 8005. Siga o exemplo da imagem abaixo:

![criando_remote_debug](https://user-images.githubusercontent.com/38056234/58743250-62924700-8404-11e9-8a3f-8c612060d6b0.png)

OBS: Para alterar a porta de 8005 para qualquer outra, edite o arquivo run.sh.

##### Para acessar o console de administração do RabbitMQ:
http://[docker host IP]:15672/#/

