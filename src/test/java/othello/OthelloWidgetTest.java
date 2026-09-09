package othello;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit tests for the game logic implemented inside OthelloWidget
 * (isLegalMove, flipPieces, checkWin, checkDraw, hasLegalMoves,
 * getColor, inBounds). GUI behaviour (painting, mouse events) is not
 * covered here, only the game-rule logic embedded in the class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OthelloWidgetTest {

    private OthelloWidget widget;

    @BeforeEach
    public void beforeEach() {
        widget = new OthelloWidget();
    }

    // Fills the whole 8x8 board with a single color so that no empty
    // spot remains, guaranteeing hasLegalMoves() becomes false.
    private void fillBoard(Color color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Spot s = widget.getSpotAt(x, y);
                s.setSpotColor(color);
                s.setSpot();
            }
        }
    }

    // Fills the whole board with a split of black/white pieces
    // (no empty spots) using the first `blackCount` cells (row-major)
    // as black and the rest as white.
    private void fillBoardSplit(int blackCount) {
        int placed = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Spot s = widget.getSpotAt(x, y);
                s.setSpotColor(placed < blackCount ? Color.BLACK : Color.WHITE);
                s.setSpot();
                placed++;
            }
        }
    }

    @Test
    @Order(1)
    public void constructorSetsUpStandardStartingPosition() {
        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 4).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(4, 3).getSpotColor());
        assertEquals(Color.WHITE, widget.getSpotAt(4, 4).getSpotColor());
        assertFalse(widget.getSpotAt(3, 3).isEmpty());
        assertTrue(widget.getSpotAt(0, 0).isEmpty());
    }

    @Test
    @Order(2)
    public void isLegalMoveReturnsFalseForOccupiedSpot() {
        assertFalse(widget.isLegalMove(widget.getSpotAt(3, 3)));
    }

    @Test
    @Order(3)
    public void isLegalMoveReturnsTrueForCapturingMove() {
        assertTrue(widget.isLegalMove(widget.getSpotAt(2, 3)));
    }

    @ParameterizedTest
    @Order(4)
    @CsvFileSource(resources = "/legalmoves.csv", numLinesToSkip = 1)
    public void isLegalMoveParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.isLegalMove(widget.getSpotAt(x, y)));
    }

    @ParameterizedTest
    @Order(5)
    @CsvFileSource(resources = "/inbounds.csv", numLinesToSkip = 1)
    public void inBoundsParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.inBounds(x, y));
    }

    @Test
    @Order(6)
    public void inBoundsSpotOverloadMatchesCoordinateOverload() {
        assertTrue(widget.inBounds(widget.getSpotAt(0, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 8, 8)));
    }

    @Test
    @Order(7)
    public void flipPiecesFlipsCapturedPieceInOneDirection() {
        widget.flipPieces(widget.getSpotAt(2, 3));
        assertEquals(Color.BLACK, widget.getSpotAt(3, 3).getSpotColor());
        assertTrue(widget.getSpotAt(2, 3).isEmpty());
    }

    @Test
    @Order(8)
    public void flipPiecesDoesNothingWhenNoCaptureChainCompletes() {
        widget.flipPieces(widget.getSpotAt(0, 0));
        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 4).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(4, 3).getSpotColor());
        assertEquals(Color.WHITE, widget.getSpotAt(4, 4).getSpotColor());
    }

    @Test
    @Order(9)
    public void flipPiecesFlipsMultiplePiecesInChain() {
        Spot mid1 = widget.getSpotAt(5, 3);
        Spot mid2 = widget.getSpotAt(6, 3);
        mid1.setSpotColor(Color.WHITE);
        mid1.setSpot();
        mid2.setSpotColor(Color.WHITE);
        mid2.setSpot();

        widget.flipPieces(widget.getSpotAt(7, 3));

        assertEquals(Color.BLACK, mid1.getSpotColor());
        assertEquals(Color.BLACK, mid2.getSpotColor());
    }

    @Test
    @Order(10)
    public void hasLegalMovesTrueAtGameStart() {
        assertTrue(widget.hasLegalMoves());
    }

    @Test
    @Order(11)
    public void hasLegalMovesFalseWhenBoardFull() {
        fillBoard(Color.BLACK);
        assertFalse(widget.hasLegalMoves());
    }

    @Test
    @Order(12)
    public void hasLegalMovesWithArgsThrowsDueToKnownBug() {
        // hasLegalMoves(int, Spot) dereferences an uninitialized Point
        // and always throws NullPointerException - documents the bug.
        assertThrows(NullPointerException.class,
                () -> widget.hasLegalMoves(0, widget.getSpotAt(0, 0)));
    }

    @Test
    @Order(13)
    public void checkWinFalseWhileLegalMovesRemain() {
        assertFalse(widget.checkWin());
    }

    @Test
    @Order(14)
    public void checkWinTrueAndBlackWinsWhenBoardFullOfBlack() {
        fillBoard(Color.BLACK);
        assertTrue(widget.checkWin());
        assertTrue(widget._noBlack > widget._noWhite);
        // second call must short-circuit through the already-won branch
        assertTrue(widget.checkWin());
    }

    @Test
    @Order(15)
    public void checkWinTrueAndWhiteWinsWhenBoardFullOfWhite() {
        fillBoard(Color.WHITE);
        assertTrue(widget.checkWin());
        assertTrue(widget._noWhite > widget._noBlack);
    }

    @Test
    @Order(16)
    public void checkWinTrueAndDrawWhenCountsAreEqual() {
        fillBoardSplit(32);
        assertTrue(widget.checkWin());
        assertEquals(widget._noBlack, widget._noWhite);
    }

    @Test
    @Order(17)
    public void checkDrawAlwaysReturnsFalseDueToKnownBug() {
        // checkDraw() computes counts but never uses them - documents the bug.
        assertFalse(widget.checkDraw());
        fillBoard(Color.WHITE);
        assertFalse(widget.checkDraw());
    }

    @Test
    @Order(18)
    public void getColorReturnsEmptyBlackAndWhite() {
        assertEquals(-1, widget.getColor(0, 0));
        assertEquals(0, widget.getColor(3, 4));
        assertEquals(1, widget.getColor(3, 3));
    }

    @Test
    @Order(19)
    public void getColorSpotOverloadCoversAllBranches() {
        assertEquals(-1, widget.getColor(widget.getSpotAt(0, 0)));
        assertEquals(0, widget.getColor(widget.getSpotAt(3, 4)));
        assertEquals(1, widget.getColor(widget.getSpotAt(3, 3)));

        Spot weird = widget.getSpotAt(1, 1);
        weird.setSpotColor(Color.RED);
        assertEquals(-2, widget.getColor(weird));
    }
}
