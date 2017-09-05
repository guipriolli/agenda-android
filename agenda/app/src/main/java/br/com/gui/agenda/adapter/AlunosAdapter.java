package br.com.gui.agenda.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.gui.agenda.R;
import br.com.gui.agenda.model.Aluno;

//Adapter customizado para lista de alunos
public class AlunosAdapter extends BaseAdapter {

    private final List<Aluno> alunos;
    private final Context context;

    public AlunosAdapter(Context context, List<Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    //Pega o total de itens
    public int getCount() {
        return alunos.size();
    }

    @Override
    //Pega o item selecionado
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    //Pega o id do item selecionado
    public long getItemId(int position) {
        return 0;
    }

    @Override
    //Pega a view que exibe os dados na posição especificada no conjunto de dados
    public View getView(int position, View convertView, ViewGroup parent) {

        Aluno aluno = alunos.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);

        //Pega o layout customizada (list_item.xml)
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        //Nome
        TextView campoNome = (TextView) view.findViewById(R.id.item_nome);
        campoNome.setText(aluno.getNome());

        //Telefone
        TextView campoTelefone = (TextView) view.findViewById(R.id.item_telefone);
        campoTelefone.setText(aluno.getTelefone());

        //Endereço
        TextView campoEndereco = (TextView) view.findViewById(R.id.item_endereco);
        if (campoEndereco != null) {
            campoEndereco.setText(aluno.getEndereco());
        }

        //Site
        TextView campoSite = (TextView) view.findViewById(R.id.item_site);
        if (campoSite != null) {
            campoSite.setText(aluno.getSite());
        }

        //Foto
        ImageView campoFoto = (ImageView) view.findViewById(R.id.item_foto);
        String caminhoFoto = aluno.getCaminhoFoto();
        if (caminhoFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            campoFoto.setImageBitmap(bitmapReduzido);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        return view;
    }
}
