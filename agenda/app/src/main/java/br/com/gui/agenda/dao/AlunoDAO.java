package br.com.gui.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.gui.agenda.model.Aluno;

public class AlunoDAO extends SQLiteOpenHelper {

    //Versão do banco de dados. Utilizada para atualização do banco na função onUpgrade
    public static final int DB_VERSION = 5;

    public AlunoDAO(Context context) {
        super(context, "Agenda", null, DB_VERSION);
    }

    @Override
    //Criação do banco de dados
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Alunos (" +
                "id CHAR(36) PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL, " +
                "caminhoFoto TEXT, " +
                "sincronizado INT DEFAULT 0, " +
                "desativado INT DEFAULT 0);";

        db.execSQL(sql);
    }

    @Override
    //Atualização do banco de dados de acordo com a versão do App instalada
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "";
        switch (oldVersion) {
            case 1:
                //Adiciona nova coluna
                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT";
                db.execSQL(sql);

            case 2:
                //Cria nova tabela
                sql = "CREATE TABLE Alunos_novo (" +
                        "id CHAR(36) PRIMARY KEY, " +
                        "nome TEXT NOT NULL, " +
                        "endereco TEXT, " +
                        "telefone TEXT, " +
                        "site TEXT, " +
                        "nota REAL, " +
                        "caminhoFoto TEXT);";
                db.execSQL(sql);

                //Insere os dados antigos
                sql = "INSERT INTO Alunos_novo " +
                        "(id, nome, endereco, telefone, site, nota, caminhoFoto) " +
                        "SELECT id, nome, endereco, telefone, site, nota, caminhoFoto " +
                        "FROM Alunos";
                db.execSQL(sql);

                //Dropa tabela antiga
                sql = "DROP TABLE IF EXISTS Alunos";
                db.execSQL(sql);

                //Renomeia nova tabela
                sql = "ALTER TABLE Alunos_novo RENAME TO Alunos";
                db.execSQL(sql);

                //Busca todos os registros
                String buscaAlunos = "SELECT * FROM Alunos";
                Cursor cursor = db.rawQuery(buscaAlunos, null);
                List<Aluno> alunos = populaAlunos(cursor);

                //Altera o ID de cada registro para o UUID
                String alteraIdAluno = "UPDATE Alunos SET id=? WHERE id=?";
                for (Aluno aluno : alunos) {
                    db.execSQL(alteraIdAluno, new String[]{geraUUID(), aluno.getId()});
                }

            case 3:
                sql = "ALTER TABLE Alunos ADD COLUMN sincronizado INT DEFAULT 0";
                db.execSQL(sql);

            case 4:
                sql = "ALTER TABLE Alunos ADD COLUMN desativado INT DEFAULT 0";
                db.execSQL(sql);
        }
    }

    //Gera os UUID randômicos
    public String geraUUID() {
        return UUID.randomUUID().toString();
    }

    //Create
    public void create(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        //Se não tiver UUID, gera um novo
        if (aluno.getId() == null) {
            aluno.setId(geraUUID());
        }
        ContentValues dados = getValuesAlunos(aluno);
        db.insert("Alunos", null, dados);
    }

    //Create todos os alunos
    public void sincroniza(List<Aluno> alunos) {
        //Para cada aluno
        for (Aluno aluno : alunos) {

            //Marca aluno como sincronizado
            aluno.sincroniza();

            //Se existir, altera. Senão, create
            if (existe(aluno)) {
                //Se estiver desativado, deleta
                if (aluno.getDesativado() == 1) {
                    delete(aluno);
                } else {
                    update(aluno);
                }
            //Se não tiver na base e não estiver desativado
            } else if (aluno.getDesativado() == 0) {
                create(aluno);
            }
        }
    }

    //Verifica se o aluno já está cadastrado
    private boolean existe(Aluno aluno) {
        SQLiteDatabase db = getReadableDatabase();
        String existe = "SELECT id FROM Alunos WHERE id = ? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{aluno.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    @NonNull
    //Monta dados do aluno para a inserção no banco (Chave -> Valor)
    private ContentValues getValuesAlunos(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("id", aluno.getId());
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        dados.put("sincronizado", aluno.getSincronizado());
        dados.put("desativado", aluno.getDesativado());
        return dados;
    }

    //Read All
    public List<Aluno> readAll() {

        String sql = "SELECT * FROM Alunos WHERE desativado = 0;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        List<Aluno> alunos = populaAlunos(c);
        c.close();

        return alunos;
    }

    @NonNull
    //Monta o objeto Aluno
    private List<Aluno> populaAlunos(Cursor cursor) {
        List<Aluno> alunos = new ArrayList<Aluno>();
        while (cursor.moveToNext()) {
            Aluno aluno = new Aluno();
            aluno.setId(cursor.getString(cursor.getColumnIndex("id")));
            aluno.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            aluno.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
            aluno.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
            aluno.setSite(cursor.getString(cursor.getColumnIndex("site")));
            aluno.setNota(cursor.getDouble(cursor.getColumnIndex("nota")));
            aluno.setCaminhoFoto(cursor.getString(cursor.getColumnIndex("caminhoFoto")));
            aluno.setSincronizado(cursor.getInt(cursor.getColumnIndex("sincronizado")));
            aluno.setDesativado(cursor.getInt(cursor.getColumnIndex("desativado")));
            alunos.add(aluno);
        }
        return alunos;
    }

    //Delete
    public void delete(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {aluno.getId().toString()};
        if (aluno.getDesativado() == 1) {
            db.delete("Alunos", "id = ?", params);
        } else {
            aluno.desativa();
            update(aluno);
        }
    }

    //Update
    public void update(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = getValuesAlunos(aluno);
        String[] params = {aluno.getId().toString()};
        db.update("Alunos", dados, "id = ?", params);
    }

    //Verifica se o número de telefone é de algum aluno
    public boolean isAluno(String telefone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE telefone = ?", new String[]{telefone});
        int resultados = c.getCount();
        c.close();
        return resultados > 0;
    }

    //Lista os alunos não sincronizados com o servidor
    public List<Aluno> listaNaoSincronizados() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Alunos WHERE sincronizado = 0";
        Cursor cursor = db.rawQuery(sql, null);
        return populaAlunos(cursor);
    }
}
