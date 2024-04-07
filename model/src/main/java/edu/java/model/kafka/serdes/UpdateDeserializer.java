package edu.java.model.kafka.serdes;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.java.model.dto.LinkUpdate;
import edu.java.model.protobuf.ProtoUpdateOuterClass;
import java.net.URI;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class UpdateDeserializer implements Deserializer<LinkUpdate> {
    @Override
    public LinkUpdate deserialize(String s, byte[] bytes) {
        try {
            var protoUpdate = ProtoUpdateOuterClass.ProtoUpdate.parseFrom(bytes);
            return new LinkUpdate(
                protoUpdate.getId(),
                URI.create(protoUpdate.getUri()),
                protoUpdate.getDescription(),
                protoUpdate.getTgChatIdsList()
            );
        } catch (InvalidProtocolBufferException e) {
            throw new SerializationException("Error when deserializing byte[] to protobuf", e);
        }
    }
}
