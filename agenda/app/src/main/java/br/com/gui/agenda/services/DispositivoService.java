package br.com.gui.agenda.services;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

//Interface para facilitar a chamada do WebService
public interface DispositivoService {

    @POST("firebase/dispositivo")
    Call<Void> enviaToken(@Header("token") String token);
}
