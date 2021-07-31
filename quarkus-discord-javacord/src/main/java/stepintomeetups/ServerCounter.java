package stepintomeetups;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Builder
@MongoEntity
@NoArgsConstructor
@AllArgsConstructor
public class ServerCounter extends PanacheMongoEntity {

    public String serverId;
    public int nextValue;
    public String lastUserId;

    public static ServerCounter findByServerId(String serverId) {
        return find(new Document("serverId", serverId)).firstResult();
    }

}
