package br.com.gui.agenda.preferences;

import android.content.Context;
import android.content.SharedPreferences;

//Classe para salvar dados de Preferências compartilhadas do app. Armazena dados primitivos privados em pares chave-valor.
public class AlunoPreferences {

    private static final String ALUNO_PREFERENCES = "br.com.gui.agenda.preferences.AlunoPreferences";
    private static final String VERSAO_DO_DADO = "versao_do_dado";
    private Context context;

    public AlunoPreferences(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(ALUNO_PREFERENCES, context.MODE_PRIVATE);
    }

    //Salva a versão dos dados
    public void salvaVersao(String versao) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSAO_DO_DADO, versao);
        editor.commit();
    }

    //Pega a versão salva
    public String getVersao() {
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(VERSAO_DO_DADO, "");
    }

    //Verifica se o app já possui uma versão de dados
    public boolean possuiVersao() {
        return !getVersao().isEmpty();
    }
}
