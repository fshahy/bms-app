package com.bms.users;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class UserService implements HttpService {

    private final UserRepository users;

    public UserService() {
        this.users = new UserRepository();
    }

    @Override
    public void routing(HttpRules rules) {
        rules
            .post("/", this::save)
            .get("/", this::getAll)
            .delete("/{userId}", this::delete);
    }

    private void save(ServerRequest req, ServerResponse res) {
        User user = req.content().as(User.class);
        users.save(user);
        res.status(201).send(user);
    }

    private void getAll(ServerRequest req, ServerResponse res) {
        res.send(users.getAll());
    }

    private void delete(ServerRequest req, ServerResponse res) {
        String userId = req.path().pathParameters().get("userId");
        if (users.delete(userId) == 0) {
            res.status(404).send();
        } else {
            res.status(204).send();
        }
    }
    
}
