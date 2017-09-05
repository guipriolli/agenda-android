package br.com.gui.agenda.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.dto.AlunoSync;
import br.com.gui.agenda.event.AtualizaListaAlunoEvent;
import br.com.gui.agenda.sync.AlunoSincronizador;

//Configura a troca de mensagem entre o app e o servidor
public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    //Recebe mensagem do servidor
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Recebe os dados do aluno
        Map<String, String> mensagem = remoteMessage.getData();

        try {
            converteParaAluno(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Converte os dados recebidos do servidor e converte para Aluno
    private void converteParaAluno(Map<String, String> mensagem) throws IOException {

        String chaveDeAcesso = "alunoSync";

        if (mensagem.containsKey(chaveDeAcesso)) {

            //Transforma de JSON para objeto Aluno
            String json = mensagem.get(chaveDeAcesso);
            ObjectMapper mapper = new ObjectMapper();

            try {

                //Sincroniza os dados
                AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);
                new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunoSync);

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunoEvent());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
