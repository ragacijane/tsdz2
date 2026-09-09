package othello;

import org.junit.jupiter.api.*;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OthelloWidgetGuiTest {

    private OthelloWidget widget;

    @BeforeEach
    public void beforeEach() {
        widget = new OthelloWidget();
    }

    private void fillBoard(Color color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Spot s = widget.getSpotAt(x, y);
                s.setSpotColor(color);
                s.setSpot();
            }
        }
    }

    // дугме restart враћа таблу на почетну позицију после промена
    @Test
    @Order(1)
    public void actionPerformedResetsBoardToStartingPosition() {
        widget.spotClicked(widget.getSpotAt(2, 3));
        assertFalse(widget.getSpotAt(2, 3).isEmpty());

        widget.actionPerformed(null);

        assertTrue(widget.getSpotAt(2, 3).isEmpty());
        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 4).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(4, 3).getSpotColor());
        assertEquals(Color.WHITE, widget.getSpotAt(4, 4).getSpotColor());
    }

    // мишем се прелази преко дозвољеног поља, оно се хајлајтује
    @Test
    @Order(2)
    public void spotEnteredHighlightsLegalMoveSpot() {
        Spot legal = widget.getSpotAt(2, 3);
        widget.spotEntered(legal);
        assertTrue(legal.isHighlighted());
    }

    // мишем се прелази преко заузетог поља, оно се не хајлајтује
    @Test
    @Order(3)
    public void spotEnteredDoesNotHighlightIllegalMoveSpot() {
        Spot occupied = widget.getSpotAt(3, 3);
        widget.spotEntered(occupied);
        assertFalse(occupied.isHighlighted());
    }

    // напуштање поља мишем скида хајлајт без обзира да ли је потез легалан
    @Test
    @Order(4)
    public void spotExitedUnhighlightsSpot() {
        Spot legal = widget.getSpotAt(2, 3);
        legal.highlightSpot();

        widget.spotExited(legal);

        assertFalse(legal.isHighlighted());
    }

    // кад је игра већ добијена, прелазак мишем преко поља више не хајлајтује
    @Test
    @Order(5)
    public void spotEnteredDoesNothingAfterGameIsWon() {
        fillBoard(Color.BLACK);
        assertTrue(widget.checkWin());

        Spot s = widget.getSpotAt(0, 0);
        widget.spotEntered(s);

        assertFalse(s.isHighlighted());
    }

    // кад је игра већ добијена, напуштање поља мишем не скида постојећи хајлајт
    @Test
    @Order(6)
    public void spotExitedDoesNothingAfterGameIsWon() {
        Spot s = widget.getSpotAt(2, 3);
        s.highlightSpot();

        fillBoard(Color.BLACK);
        assertTrue(widget.checkWin());

        widget.spotExited(s);

        assertTrue(s.isHighlighted());
    }
}
