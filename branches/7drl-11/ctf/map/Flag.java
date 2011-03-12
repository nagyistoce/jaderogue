package ctf.field;

import ctf.field.CTFMap.Team;
import ctf.flagger.Flagger;

public class Flag extends CTFActor
{
    public Flag(Team team)
    {
        super(team, 'F');
    }

    @Override
    public void act()
    {
        if(!held())
        {
            Flagger flagger = world().getActorAt(Flagger.class, pos());
            if(flagger != null && flagger.team() != team())
                attach(flagger);
        }
        else if(!onside())
            world().declareWinner(otherteam());
    }
}
