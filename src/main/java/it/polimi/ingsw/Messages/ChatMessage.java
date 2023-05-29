package it.polimi.ingsw.Messages;

public class ChatMessage implements Message{

    private String mittente;

    private String destinatario;

    private String messaggio;

    public ChatMessage(String mittente, String messaggio) {
        this(mittente,null,messaggio);
    }

    public ChatMessage(String mittente, String messaggio,String destinatario) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.messaggio = messaggio;
    }

    public String getMittente() {
        return mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }
}
