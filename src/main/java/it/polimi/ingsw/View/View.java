package it.polimi.ingsw.View;

import it.polimi.ingsw.Listener.ViewListener;
import it.polimi.ingsw.Messages.Message;

public interface View {

    void update(Message m);

    void notifyListeners(Message m);

    void addListener(ViewListener listener);

    void run();

}
