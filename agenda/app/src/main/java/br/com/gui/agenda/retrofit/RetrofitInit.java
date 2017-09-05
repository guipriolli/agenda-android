package br.com.gui.agenda.retrofit;

import br.com.gui.agenda.services.AlunoService;
import br.com.gui.agenda.services.DispositivoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

//Retrofit auxilia no acesso a WebServices
public class RetrofitInit {

    private final Retrofit retrofit;

    public RetrofitInit() {

        //Interceptador para ver os logs
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor);

        //URL do service criado pela alura (service.jar)
        this. retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.15.20:8080/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }

    public DispositivoService getDispositivoService() {
        return retrofit.create(DispositivoService.class);
    }
}
