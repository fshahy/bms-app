package com.bms.users;

import java.util.List;

import io.helidon.common.context.Contexts;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;

public class UserRepository {

    private final DbClient dbClient;

    UserRepository() {
        Config dbConfig = Config.global().get("db");
        dbClient = Contexts.globalContext()
                        .get(DbClient.class)
                        .orElseGet(() -> DbClient.create(dbConfig));
    }

    public User save(User user) {
        dbClient.execute()
            .createNamedInsert("create-user")
            .addParam("id", user.getId())
            .addParam("firstName", user.getFirstName())
            .addParam("lastName", user.getLastName())
            .addParam("email", user.getEmail())
            .addParam("password", user.getPassword())
            .execute();
        return user;
    }
    
    public List<User> getAll() {
        return dbClient.execute()
                    .createNamedQuery("select-all-users")
                    .execute()
                    .map(UserDbMapper::read)
                    .toList();  
    }

    public long delete(String userId) {
        return dbClient.execute().createNamedDelete("delete-user")
                            .addParam("id", userId)
                            .execute();
    }


    private final class UserDbMapper {
        private UserDbMapper() {}

        static User read(DbRow row) {
            return User.of(row.column("id").get(String.class), 
            row.column("firstName").get(String.class), 
            row.column("lastName").get(String.class), 
            row.column("email").get(String.class), 
            row.column("password").get(String.class));
        }
    }

}
