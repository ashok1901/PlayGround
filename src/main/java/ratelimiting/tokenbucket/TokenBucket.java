package ratelimiting.tokenbucket;

import java.util.HashMap;
import java.util.Map;

public class TokenBucket {
    // Per if tokens information
    // TODO: we should cleanup the tokens those are not accessed since long time.
    private Map<String, Token> tokenMap = new HashMap<>();

    public Token getToken(String id) {
        // If we have no token for this user, it means this is first time entry.
        if (!tokenMap.containsKey(id)) {
            tokenMap.put(id, new Token());
        }
        return tokenMap.get(id);
    }
}


