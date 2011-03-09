package ctf.field;

import jade.core.World;
import jade.gen.feature.Fence;
import jade.gen.maps.Terrain;
import jade.ui.TermPanel;
import jade.util.ColoredChar;
import jade.util.Coord;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ctf.ai.AttractiveField;
import ctf.ai.FieldBehavior;
import ctf.ai.PotentialField;
import ctf.ai.RepulsiveField;
import ctf.flagger.Flagger;
import ctf.flagger.FlaggerBot;
import ctf.flagger.Player;

public class CTFMap extends World
{
    private int divide;
    private Team winner;
    private FieldBehavior behavior;

    public CTFMap(Player player)
    {
        super(TermPanel.DEFAULT_COLS, TermPanel.DEFAULT_ROWS);
        generate();
        makeDivide();
        addTeamA(player);
        addTeamB();
        makeBehavior();
    }

    private void generate()
    {
        new Fence(new Terrain(0)).generate(this);
    }

    private void makeDivide()
    {
        divide = width() / 2;
        ColoredChar face = new ColoredChar('|', Color.darkGray);
        for(int y = 1; y < height() - 1; y++)
            setTile(divide, y, face, true);
    }

    private void clearBase(Team team, Coord flagpos)
    {
        ColoredChar face = new ColoredChar('.', team.color());
        for(int x = flagpos.x() - 1; x <= flagpos.x() + 1; x++)
            for(int y = flagpos.y() - 1; y <= flagpos.y() + 1; y++)
                setTile(x, y, face, true);
    }

    private void addTeamA(Player player)
    {
        Coord flagpos = getOpenTile(2, 2, divide / 2, height() - 3);
        clearBase(Team.A, flagpos);
        addActor(player, flagpos);
        while(player.pos().equals(flagpos))
            player.setPos(getOpenTile(flagpos.x() - 1, flagpos.y() - 1, flagpos
                    .x() + 1, flagpos.y() + 1));
        for(int x = flagpos.x() - 1; x <= flagpos.x() + 1; x++)
            for(int y = flagpos.y() - 1; y <= flagpos.y() + 1; y++)
            {
                Coord pos = new Coord(x, y);
                if(!pos.equals(player.pos()) && !pos.equals(flagpos))
                    addActor(new FlaggerBot(Team.A), pos);
            }
        addActor(new Flag(Team.A), flagpos);
    }

    private void addTeamB()
    {
        Coord flagpos = getOpenTile(divide * 3 / 2, 2, width() - 3,
                height() - 3);
        clearBase(Team.B, flagpos);
        for(int x = flagpos.x() - 1; x <= flagpos.x() + 1; x++)
            for(int y = flagpos.y() - 1; y <= flagpos.y() + 1; y++)
            {
                if(x != flagpos.x() || y != flagpos.y())
                    addActor(new FlaggerBot(Team.B), x, y);
            }
        // addActor(new Flag(Team.B), flagpos);
        addActor(new FlaggerBot(Team.B), flagpos);
    }

    private void makeBehavior()
    {
        behavior = new FieldBehavior();
        for(Flagger flagger : getActors(Flagger.class))
        {
            PotentialField field = new RepulsiveField();
            field.attach(flagger);
            behavior.setWeight(field, 1);
        }
        for(Flag flag : getActors(Flag.class))
        {
            PotentialField field = new AttractiveField();
            field.attach(flag);
            behavior.setWeight(field, 1);
        }
    }

    @Override
    public List<ColoredChar> lookAll(int x, int y)
    {
        List<ColoredChar> look = new ArrayList<ColoredChar>();
        for(Flagger flagger : getActorsAt(Flagger.class, x, y))
            look.add(flagger.face());
        for(Flag flag : getActorsAt(Flag.class, x, y))
            look.add(flag.face());
        look.add(tileAt(x, y));
        return look;
    }

    @Override
    public void tick()
    {
        for(Flagger flagger : getActors(Team.A))
            flagger.act();
        for(Flagger flagger : getActors(Team.B))
            flagger.act();
        for(Flag flag : getActors(Flag.class))
            flag.act();
        removeExpired();
        behavior.removeExpired();
    }

    public Set<Flagger> getActors(Team team)
    {
        Set<Flagger> flaggers = new HashSet<Flagger>();
        for(Flagger flagger : getActors(Flagger.class))
            if(flagger.team() == team)
                flaggers.add(flagger);
        return flaggers;
    }

    public Flagger getActorAt(Team team, int x, int y)
    {
        for(Flagger flagger : getActorsAt(Flagger.class, x, y))
            if(flagger.team() == team)
                return flagger;
        return null;
    }

    public int divide()
    {
        return divide;
    }

    public enum Team
    {
        A(Color.blue), B(Color.red);

        private Color color;

        private Team(Color color)
        {
            this.color = color;
        }

        public Color color()
        {
            return color;
        }
    }

    public void declareWinner(Team team)
    {
        winner = team;
    }

    public boolean playing()
    {
        return winner() == null;
    }

    public Team winner()
    {
        return winner;
    }

    public FieldBehavior behavior()
    {
        return behavior;
    }
}
