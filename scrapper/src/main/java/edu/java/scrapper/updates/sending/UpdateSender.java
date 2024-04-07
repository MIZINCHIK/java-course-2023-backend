package edu.java.scrapper.updates.sending;

import edu.java.model.dto.LinkUpdate;

public interface UpdateSender {
    void sendUpdate(LinkUpdate update);
}
