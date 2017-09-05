package br.com.gui.agenda.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.gui.agenda.R;
import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.helper.FormularioHelper;
import br.com.gui.agenda.model.Aluno;
import br.com.gui.agenda.retrofit.RetrofitInit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Tela do Formulário
public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        //Recebe os dados do aluno
        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        if (aluno != null) {
            helper.preencheFormulario(aluno);
        }

        //Botão Foto - Ao clicar abre a câmera para tirar uma nova foto do aluno
        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Abre a câmera
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg"; //Salva o caminho da foto
                File arquivoFoto = new File(caminhoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto)); //Salva a foto
                startActivityForResult(intentCamera, CODIGO_CAMERA); //Inicia a activity esperando uma resposta
            }
        });
    }

    @Override
    //Resposta da Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Se deu tudo certo
        if (resultCode == Activity.RESULT_OK) {
            //Se retornou o código da câmera passado na chamada da Activity
            if (requestCode == CODIGO_CAMERA) {
                //Carrega a foto
                helper.carregaImagem(caminhoFoto);
            }
        }
    }

    @Override
    //Cria opções no menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu); //menu_formulario.xml
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Ao selecionar o item do menu
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //Executa ação do menu
            case R.id.menu_formulario_ok:

                //Pega aluno
                Aluno aluno = helper.getAluno();
                //Deixa o aluno dessincronizado
                aluno.dessincroniza();

                AlunoDAO dao = new AlunoDAO(this);
                //Salva ou update
                if (aluno.getId() != null) {
                    dao.update(aluno);
                } else {
                    dao.create(aluno);
                }
                dao.close();

                //Insere o aluno no servidor
                Call call = new RetrofitInit().getAlunoService().create(aluno);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.i("onResponse", "Requisição com sucesso!");
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("onFailure", "Requisição falhou!");
                    }
                });

                //Exibe uma mensagem de sucesso
                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " salvo!", Toast.LENGTH_SHORT).show();

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
