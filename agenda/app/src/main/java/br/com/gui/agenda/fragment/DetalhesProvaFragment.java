package br.com.gui.agenda.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import br.com.gui.agenda.R;
import br.com.gui.agenda.model.Prova;

//Fragmento dos Detalhes das Provas
public class DetalhesProvaFragment extends Fragment {

    private TextView campoMateria;
    private TextView campoData;
    private ListView listaTopicos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Carrega a view de detalhes
        View view = inflater.inflate(R.layout.fragment_detalhes_prova, container, false);

        //Pega os campos
        campoMateria = (TextView) view.findViewById(R.id.detalhes_prova_materia);
        campoData = (TextView) view.findViewById(R.id.detalhes_prova_data);
        listaTopicos = (ListView) view.findViewById(R.id.detalhes_prova_topicos);

        //Seta os dados
        Bundle parametros = getArguments();
        if (parametros != null) {
            Prova prova = (Prova) parametros.getSerializable("prova");
            populaCamposCom(prova);
        }

        return view;
    }

    //Carrega os dados nas Views
    public void populaCamposCom(Prova prova) {
        campoMateria.setText(prova.getMateria());
        campoData.setText(prova.getData());
        ArrayAdapter<String> adapterTopicos = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, prova.getTopicos());
        listaTopicos.setAdapter(adapterTopicos);
    }
}















