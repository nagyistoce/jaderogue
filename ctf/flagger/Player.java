package ctf.flagger;

import jade.ui.Terminal;
import jade.util.ColoredChar;
import jade.util.Direction;
import ctf.map.CTFMap.Team;

public class Player extends Flagger
{
    private Terminal term;

    public Player(Terminal term)
    {
        super(Team.A);
        setFace(new ColoredChar('@'));
        this.term = term;
    }

    @Override
    public void act()
    {
        char key = term.getKey();
        switch(key)
        {
        case Terminal.ESC:
        case 'q':
            expire();
            break;
        default:
            Direction dir = Direction.keyDir(key);
            if(dir != null)
                move(dir);
            break;
        }
    }

    @Override
    public void expire()
    {
        world().declareWinner(otherTeam());
        super.expire();
    }
}
