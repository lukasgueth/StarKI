package de.uni_passau.fim.sommercamp.sc2.bots;

import com.github.ocraft.s2client.api.S2Client;
import de.uni_passau.fim.sommercamp.sc2.BaseBot;

public class EmptyBot extends BaseBot {

    public EmptyBot(S2Client client) {
        super(client);
    }

    @Override
    protected void onStep() {

    }
}
