package br.com.gui.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.gui.agenda.R;
import br.com.gui.agenda.dao.AlunoDAO;

//Classe para receber e verificar cada SMS que chega no dispositivo
public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Pega a mensagem SMS
        //PDU - Protocol Data Unit - formato padrão de uma mensagem SMS
        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        byte[] pdu = (byte[]) pdus[0];
        String formato = (String) intent.getSerializableExtra("format");
        SmsMessage sms = SmsMessage.createFromPdu(pdu, formato);

        //Pega o número do telefone que enviou o SMS
        String telefone = sms.getDisplayOriginatingAddress();

        AlunoDAO dao = new AlunoDAO(context);
        //Se o telefone for de algum aluno, exibe o Toast
        if (dao.isAluno(telefone)) {
            Toast.makeText(context, "Chegou um SMS de Aluno!", Toast.LENGTH_SHORT).show();
            //Som de notificação
            MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
            mp.start();
        }
        dao.close();
    }
}
