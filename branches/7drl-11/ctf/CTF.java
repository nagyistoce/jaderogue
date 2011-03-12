package ctf;

import jade.ui.TermPanel;
import jade.ui.Terminal;
import ctf.flagger.Player;
import ctf.map.CTFMap;

public class CTF
{
    private static Terminal term;
    private static Player player;
    private static CTFMap field;

    public static void main(String[] args)
    {
        init();

        while(field.playing())
        {
            displayField();
            field.tick();
        }

        displayWin();

        term.exit();
    }

    private static void init()
    {
        term = TermPanel.getFramedTerm("CTF RL");
        player = new Player(term);
        field = new CTFMap(player);
    }

    private static void displayField()
    {
        for(int x = 0; x < field.width(); x++)
            for(int y = 0; y < field.height(); y++)
                term.bufferChar(x, y, field.look(x, y));
        term.updateScreen();
    }

    private static void displayWin()
    {
        displayField();
        if(player.team() == field.winner())
            term.bufferString(36, 12, "Congrats!");
        else
            term.bufferString(38, 12, "Loser!");
        term.updateScreen();
        term.getKey();
    }
}
