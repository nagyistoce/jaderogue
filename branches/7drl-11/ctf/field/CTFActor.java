package ctf.field;

import ctf.field.CTFMap.Team;
import jade.core.Actor;
import jade.util.ColoredChar;

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

    public Team otherteam()
    {
        switch(team())
        {
        case A:
            return Team.B;
        case B:
            return Team.A;
        }
        return null;
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