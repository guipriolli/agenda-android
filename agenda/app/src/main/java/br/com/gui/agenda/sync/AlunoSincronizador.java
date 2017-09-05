package br.com.gui.agenda.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.dto.AlunoSync;
import br.com.gui.agenda.event.AtualizaListaAlunoEvent;
import br.com.gui.agenda.model.Aluno;
import br.com.gui.agenda.preferences.AlunoPreferences;
import br.com.gui.agenda.retrofit.RetrofitInit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Sincroniza os dados dos alunos do servidor com os do app
public class AlunoSincronizador {

    private final Context context;
    private AlunoPreferences preferences;
    private EventBus bus = EventBus.getDefault();

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    //Busca todos os alunos no servidor
    public void buscaAlunosSever() {
        //Se possui versão
        if (preferences.possuiVersao()) {
            //Busca apenas os alunos que ainda não estão no app
            buscaNovosAlunos();
        } else {
            //Busca todos os alunos
            buscaTodosAlunos();
        }
    }

    //Busca apenas os novos alunos inseridos no servidor
    private void buscaNovosAlunos() {
        String versao = preferences.getVersao();
        Call<AlunoSync> call = new RetrofitInit().getAlunoService().readNovo(versao);
        call.enqueue(buscaAlunosCallback());
    }

    //Busca todos os alunos no servidor
    private void buscaTodosAlunos() {
        //Faz a chamada usando retrofit
        Call<AlunoSync> call = new RetrofitInit().getAlunoService().read();
        call.enqueue(buscaAlunosCallback());
    }

    @NonNull
    //Faz a busca e atualização dos alunos vindos do servidor
    private Callback<AlunoSync> buscaAlunosCallback() {
        return new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                //Pega os dados do aluno retornado
                AlunoSync alunoSync = response.body();
                //Sincroniza o aluno
                sincroniza(alunoSync);

                Log.i("versao", preferences.getVersao());

                bus.post(new AtualizaListaAlunoEvent());
                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                bus.post(new AtualizaListaAlunoEvent());
            }
        };
    }

    //Sincroniza os dados
    public void sincroniza(AlunoSync alunoSync) {

        //Pega a versão
        String versao = alunoSync.getMomentoDaUltimaModificacao();

        //Se possuir uma nova versão no servidor, ou seja, os dados estão desatualizados
        if (possuiNovaVersao(versao)) {
            //Salva a nova versão no app
            preferences.salvaVersao(versao);
        }

        //Sincroniza os dados
        AlunoDAO dao = new AlunoDAO(context);
        dao.sincroniza(alunoSync.getAlunos());
        dao.close();
    }

    //Verifica se possui uma nova versão dos dados no servidor para fazer a sincronização e salvar a datahora da nova versão
    private boolean possuiNovaVersao(String versao) {

        //Se não possui versão, ou seja, está desatualizado
        if (!preferences.possuiVersao()) {
            return true;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {
            //Compara as datas da versão do app com a do servidor
            Date dataExterna = format.parse(versao);
            String versaoInterna = preferences.getVersao();
            Date dataInterna = format.parse(versaoInterna);

            return dataExterna.after(dataInterna);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Sincroniza os alunos do app
    private void sincronizaAlunosInternos() {

        //Busca os alunos não sincronizados
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.listaNaoSincronizados();
        dao.close();

        //Atualiza os alunos no servidor
        Call<AlunoSync> call = new RetrofitInit().getAlunoService().update(alunos);
        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                //Depois de atualizar no servidor, sincroniza com o app
                AlunoSync alunoSync = response.body();
                sincroniza(alunoSync);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {

            }
        });
    }

    //Deleta
    public void delete(final Aluno aluno) {
        //Deleta no servidor
        Call<Void> call = new RetrofitInit().getAlunoService().delete(aluno.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //Depois de deletar no servidor, deleta no app
                AlunoDAO dao = new AlunoDAO(context);
                dao.delete(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}