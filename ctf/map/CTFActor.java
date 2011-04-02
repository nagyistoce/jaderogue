package ctf.map;

import jade.core.Actor;
import jade.util.ColoredChar;
import ctf.map.CTFMap.Team;

public abstract class CTFActor extends Actor
{
    private final Team team;

    public CTFActor(Team team, char ch)
    {
        super(new ColoredChar(ch, team.color()));
        this.team = team;
    }

    public Team team()
    {
        return team;
    }

    public Team otherTeam()
    {
        return team.otherTeam();
    }

    @Override
    public CTFMap world()
    {
        return (CTFMap)super.world();
    }

    public boolean onside()
    {
        if(team() == Team.A)
            return x() <= world().divide();
        else
            return x() >= world().divide();
    }
}
