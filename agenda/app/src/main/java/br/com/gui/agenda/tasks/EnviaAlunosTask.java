package br.com.gui.agenda.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.gui.agenda.web.WebClient;
import br.com.gui.agenda.util.AlunoConverter;
import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.model.Aluno;

//Classe que cria uma tarefa assíncrona para auxiliar no WebService
public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context) {
        this.context = context;
    }

    @Override
    //Exibe um diálogo antes de executar a tarefa
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando alunos...", true, true);
    }

    @Override
    //Executa a tarefa em background
    protected String doInBackground(Void... params) {

        //Busca os alunos
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.readAll();
        dao.close();

        //Converte os dados dos alunos para JSON
        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converteParaJSON(alunos);

        //Faz a chamada no WebService
        WebClient client = new WebClient();
        String resposta = client.post(json);
        return resposta;
    }

    @Override
    //Fecha o diálogo após a execução
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }
}
