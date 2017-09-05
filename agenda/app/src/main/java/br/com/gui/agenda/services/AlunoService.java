package br.com.gui.agenda.services;

import java.util.List;

import br.com.gui.agenda.dto.AlunoSync;
import br.com.gui.agenda.model.Aluno;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

//Interface para facilitar a chamada do WebService
public interface AlunoService {

    @POST("aluno")
    Call<Void> create(@Body Aluno aluno);

    @GET("aluno")
    Call<AlunoSync> read();

    @DELETE("aluno/{id}")
    Call<Void> delete(@Path("id") String id);

    @GET("aluno/diff")
    Call<AlunoSync> readNovo(@Header("datahora") String versao);

    @PUT("aluno/lista")
    Call<AlunoSync> update(@Body List<Aluno> alunos);
}
