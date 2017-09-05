package br.com.gui.agenda.web;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WebClient {

    public String post(String json) {
        //URL da alura já configurada para essa tarefa
        String endereco = "https://www.caelum.com.br/mobile";
        return realizaConexao(json, endereco);
    }

    //Faz a conexão no webservice
    public String realizaConexao(String json, String endereco) {
        try {
            //Faz a requisição para a URL
            URL url = new URL(endereco);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            connection.connect();

            //Pega o resultado
            Scanner scanner = new Scanner(connection.getInputStream());
            String resposta = scanner.next();

            return resposta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}