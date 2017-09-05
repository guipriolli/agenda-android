package br.com.gui.agenda.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.gui.agenda.R;
import br.com.gui.agenda.fragment.DetalhesProvaFragment;
import br.com.gui.agenda.fragment.ListaProvasFragment;
import br.com.gui.agenda.model.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        //Monta o primeiro fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.frame_principal, new ListaProvasFragment());

        //Se tiver no modo paisagem adiciona o outro fragmento
        if (modoPaisagem()) {
            tx.replace(R.id.frame_secundario, new DetalhesProvaFragment());
        }

        tx.commit();
    }

    //Verifica se o celular está no modo paisagem
    private boolean modoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {

        FragmentManager manager = getSupportFragmentManager();

        //Se não tiver no modo paisagem
        if (!modoPaisagem()) {

            //Monta o outro fragmento
            FragmentTransaction tx = manager.beginTransaction();

            //Sobrepõe o primeiro fragmento pelo segundo e envia os parâmetros
            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();
            Bundle parametros = new Bundle();
            parametros.putSerializable("prova", prova);
            detalhesFragment.setArguments(parametros);

            tx.replace(R.id.frame_principal, detalhesFragment);
            tx.addToBackStack(null); //Coloca na pilha para quando clicar no botão "Voltar", não sair da tela
            tx.commit();

        } else {
            //No modo paisagem só carrega os dados, já que o fragmento já foi adicionado anteriormente
            DetalhesProvaFragment detalhesFragment = (DetalhesProvaFragment) manager.findFragmentById(R.id.frame_secundario);
            detalhesFragment.populaCamposCom(prova);
        }
    }
}





























