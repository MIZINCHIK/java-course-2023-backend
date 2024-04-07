package edu.java.model.kafka.serdes;

import edu.java.model.dto.LinkUpdate;
import edu.java.model.protobuf.ProtoUpdateOuterClass;
import org.apache.kafka.common.serialization.Serializer;

public class UpdateSerializer implements Serializer<LinkUpdate> {
    @Override
    public byte[] serialize(String s, LinkUpdate linkUpdate) {
        var builder = ProtoUpdateOuterClass.ProtoUpdate.newBuilder();
        builder.setId(linkUpdate.id());
        builder.setUri(linkUpdate.url().toString());
        builder.setDescription(linkUpdate.description());
        for (var tgChatId : linkUpdate.tgChatIds()) {
            builder.addTgChatIds(tgChatId);
        }
        return builder.build().toByteArray();
    }
}
