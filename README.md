# apix2019-microservice-finder-java :rocket:

### Como executar via docker:

1 - Execute o script 'generate-image.sh', presente na pasta raiz do projeto, para gerar a imagem docker da aplicação;

2 - Execute o script 'docker-start.sh' para iniciar o container da aplicação;

OBS: Caso tenha feito alguma alteração de código e queira vê-la refletida na aplicação, repita o primeiro passo e execute o script 'update-finder' na sequência.

### Como executar localmente:

1 - Localize o arquivo 'application-conf.json';

  - Altere o valor da propriedade elastic_search.hostname para `localhost`;

  - Altere o valor da propriedade rabbit.connectionUrl para `amqp://localhost`;

2 - Execute o script 'run.sh'.

### Como fazer debug da aplicação:

1 - Execute a aplicação localmente seguindo os passos das instrução anterior;

2 - Crie um remote debug pelo IntelliJ, apontando para a porta 8005. Siga o exemplo da imagem abaixo:

![criando_remote_debug](https://user-images.githubusercontent.com/38056234/58743250-62924700-8404-11e9-8a3f-8c612060d6b0.png)

OBS: Para alterar a porta de 8005 para qualquer outra, edite o arquivo 'run.sh'.

### Como testar se o microserviço está montando as recomendações de acordo com os dados vindos do Elastic Search:

Opção 1 - Para testar o fluxo completo, acesse o README do microserviço de kit e inicie criando um kit, fazendo o post descrito lá. Neste caso, o crawler também precisa estar executando.

Opção 2 - Para testar o microserviço sem ter que passar pelo fluxo todo é necessário que o Elastic Search possua dados. Ou seja, o crawler precisa ter alimentado esses dados antes. Então, para garantir, inicie este teste partindo do fluxo do crawler. Então, você pode postar uma mensagem diretamente no rabbit, na fila 'apix-kit-queue' com o seguinte payload:

```
{
   "id":5f3671ae-c0da-454b-9da3-7d22bbc068cb,
   "phone":"+5519999999999",
   "gender":"F",
   "specifications":[
      { 
         "type":"PANT",
         "color":"BLUE"
      },
      {
         "type":"SHIRT",
         "color":"WHITE"
      },
      {
         "type":"SHOES",
         "color":"BLACK"
      }
   ]
}
```
Quando o crawler concluir a busca, uma mensagem será enviada para a fila 'apix-specification-queue' e na sequência será consumida por este microserviço.

##### Para acessar o console de administração do RabbitMQ:
http://[docker host IP]:15672/#/

