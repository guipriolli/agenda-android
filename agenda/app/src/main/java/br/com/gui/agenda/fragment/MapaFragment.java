package br.com.gui.agenda.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.gui.agenda.util.Localizador;
import br.com.gui.agenda.dao.AlunoDAO;
import br.com.gui.agenda.model.Aluno;

//Fragmento de Mapa. É melhor que usar a MapActivity pronta
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Chama o mapa assíncrono
        getMapAsync(this);
    }

    @Override
    //Quando o mapa carregar
    public void onMapReady(GoogleMap googleMap) {

        //Define posição inicial do mapa
        LatLng posicaoInicial = getCoordenada("São José do Rio Preto - SP");
        if (posicaoInicial != null) {
            //Atualiza a posição da câmera no mapa
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicaoInicial, 12);
            googleMap.moveCamera(update);
        }

        //Para cada aluno pega a coordenada a partir do endereço
        AlunoDAO alunoDAO = new AlunoDAO(getContext());
        for (Aluno aluno : alunoDAO.readAll()) {
            LatLng coordenada = getCoordenada(aluno.getEndereco());
            if (coordenada != null) {
                //Cria o marcador e adiciona no mapa
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(aluno.getNome());
                marcador.snippet(String.valueOf(aluno.getNota()));
                googleMap.addMarker(marcador);
            }
        }
        alunoDAO.close();

        new Localizador(getContext(), googleMap);
    }

    private LatLng getCoordenada(String endereco) {
        try {
            //Usa o Geocoder para transformar endereço em coordenada (latitude e longitude)
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);
            if (!resultados.isEmpty()) {
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
