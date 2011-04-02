package ctf.flagger;

import jade.util.Direction;
import util.Vector;
import ctf.map.CTFMap.Team;

public class FlaggerBot extends Flagger
{
    public FlaggerBot(Team team)
    {
        super(team);
    }

    @Override
    public void act()
    {
        Vector move = world().behavior(this).getInfluence(pos());
        Direction dir = move.direction();
        move(dir);
    }
}
