package br.com.gui.agenda.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import br.com.gui.agenda.R;
import br.com.gui.agenda.adapter.AlunosAdapter;
import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.event.AtualizaListaAlunoEvent;
import br.com.gui.agenda.model.Aluno;
import br.com.gui.agenda.sync.AlunoSincronizador;
import br.com.gui.agenda.tasks.EnviaAlunosTask;

public class ListaAlunosActivity extends AppCompatActivity {

    private final AlunoSincronizador sincronizador = new AlunoSincronizador(this);
    private ListView listaAlunos;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_lista_aluno);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sincronizador.buscaAlunosSever();
            }
        });

        //Lista de alunos - Ao clicar em um aluno específico chama a FormularioActivity e passa os dados do aluno
        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);

                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentVaiProFormulario.putExtra("aluno", aluno);
                startActivity(intentVaiProFormulario);
            }
        });

        //Botão "Novo Aluno" - Ao clicar chama a FormularioActivity
        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);

        sincronizador.buscaAlunosSever();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    //Subscribe do EventBus
    public void atualizaListaAlunoEvent(AtualizaListaAlunoEvent event) {
        //Se o swipe está rodando, para ele
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
        }
        carregaLista();
    }

    //Carrega a lista dos alunos
    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.readAll();
        dao.close();

        //Monta o Adapter customizado
        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu); //menu_lista_alunos.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_enviar_notas:
                new EnviaAlunosTask(this).execute(); //Faz a requisição com o WebService
                break;

            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;

            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaMapa);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    //Cria menu de contexto
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        //Ligar
        MenuItem itemLigar = menu.add("Ligar");
        //Ação ao clicar em "Ligar"
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Verifica se o usuário já deu permissão
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //Se não tiver permissão, solicita permissão ao usuário
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    //Se já tiver permissão, chama o App de ligação
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        //Enviar SMS
        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);

        //Visualizar no mapa
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        //Visitar site
        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        String site = aluno.getSite();
        if (!site.startsWith("http://")) {
            site = "http://" + site;
        }

        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);

        //Deletar
        final MenuItem deletar = menu.add("Deletar");
        //Ação ao clicar em "Deletar"
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Deleta o item selecionado
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.delete(aluno);
                dao.close();
                //Recarrega a lista
                carregaLista();
                //Deleta no sincronizador
                sincronizador.delete(aluno);

                return false;
            }


        });
    }
}
