# PractCook

## Base de Dados

// Planear a estrutura da DB

Usar 4 ramos: Users, Receitas, Ranking, Chats

Dados de cada usuário:
 - Nome
 - E-mail
 - Nível (Noob, Experiente, ...) (Talvez guardar em número? - 0 é noob,  1 é ......)
 - Foto (Fica aqui um link para a foto, o firebase guarda e trata de quase tudo)
 - Descrição / Bio (?)
 - Receitas (lista de IDs das receitas criadas pelo user - o firebase cria a string ID random por nós)
 - Chats * (Lista de IDs de chats) (Provisório, depois decidimos se é mais eficaz ter isto aqui ou noutro lado)
 - Listas (Playlists mas para receitas)
 - Amigos (Lista dos IDs dos amigos)

Dados de cada receita:
 - Nome da Receita
 - Data (será preciso ?)
 - Votos
 - Favoritos (Lista de pessoas que 'favoritou' esta receita - NÃO DEVE SER PRECISO, pensem se é ou não)
 - Ingredientes (nome e quantidade)
 - Passos a seguir
 - Foto(s)
 - Comentários (ficam aqui ?)


CHATS: Se cada chat, só com uma pessoa ou em grupo, tiver um ID, podemos guardar em cada utilizador uma lista dos IDs dos chats em que essa pessoa está.
Depois no ramo Chats temos esta estrutura: 

{
  12hdyu83l {
    participants {
      $IDdoUser1
      $IDdoUser2
      $IDdoUser3
    }
    name : "exemplo" (só se for chat em grupo)
    messages {
      736gdjja99 {
        sender:$IDdoUser1
        text: "O Chef Santiago é gay"
        date: "17-08-2017_17:27:21"
        read {
          $IDdoUser1: true
          $IDdoUser2: true
          $IDdoUser3: false //Isto não deve ser preciso. Não se põe até o User3 ler a mensagem
        }
      }
    }
  }
}

Assim quando a app precisa de carregar a janela das mensagens basta ir à parte da DB do seu user e estão lá todos os IDs das conversas que precisa de sacar para mostrar.