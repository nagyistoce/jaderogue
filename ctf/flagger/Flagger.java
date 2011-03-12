package ctf.flagger;

import ctf.map.CTFActor;
import ctf.map.CTFMap.Team;

public abstract class Flagger extends CTFActor
{
    public Flagger(Team team)
    {
        super(team, '@');
    }

    @Override
    public void setPos(int x, int y)
    {
        Flagger flagger = world().getActorAt(Flagger.class, x, y);
        if(flagger == null && world().passable(x, y))
            super.setPos(x, y);
        else if(flagger != null && flagger.team() != team())
            wrestle(flagger);
    }

    private void wrestle(Flagger flagger)
    {
        if(onside())
            flagger.expire();
    }
}
