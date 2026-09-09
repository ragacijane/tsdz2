package othello;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OthelloWidgetTest {

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

    // pocetni raspored 4 figure u centru table
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

    // ne moze da se igra na vec zauzeto polje
    @Test
    @Order(2)
    public void isLegalMoveReturnsFalseForOccupiedSpot() {
        assertFalse(widget.isLegalMove(widget.getSpotAt(3, 3)));
    }

    // potez koji hvata protivnika mora da bude dozvoljen
    @Test
    @Order(3)
    public void isLegalMoveReturnsTrueForCapturingMove() {
        assertTrue(widget.isLegalMove(widget.getSpotAt(2, 3)));
    }

    // gomila slucajeva legalnih i nelegalnih poteza iz csv fajla
    @ParameterizedTest
    @Order(4)
    @CsvFileSource(resources = "/legalmoves.csv", numLinesToSkip = 1)
    public void isLegalMoveParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.isLegalMove(widget.getSpotAt(x, y)));
    }

    // provera granica table za gomilu koordinata iz csv fajla
    @ParameterizedTest
    @Order(5)
    @CsvFileSource(resources = "/inbounds.csv", numLinesToSkip = 1)
    public void inBoundsParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.inBounds(x, y));
    }

    // da li se verzija sa spotom i verzija sa koordinatama slazu
    @Test
    @Order(6)
    public void inBoundsSpotOverloadMatchesCoordinateOverload() {
        assertTrue(widget.inBounds(widget.getSpotAt(0, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 8, 8)));
    }

    // okretanje figure kad je uhvacena u jednom pravcu
    @Test
    @Order(7)
    public void flipPiecesFlipsCapturedPieceInOneDirection() {
        widget.flipPieces(widget.getSpotAt(2, 3));
        assertEquals(Color.BLACK, widget.getSpotAt(3, 3).getSpotColor());
        assertTrue(widget.getSpotAt(2, 3).isEmpty());
    }

    // nista se ne okrece ako niz hvatanja nije zavrsen do kraja
    @Test
    @Order(8)
    public void flipPiecesDoesNothingWhenNoCaptureChainCompletes() {
        widget.flipPieces(widget.getSpotAt(0, 0));
        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 4).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(4, 3).getSpotColor());
        assertEquals(Color.WHITE, widget.getSpotAt(4, 4).getSpotColor());
    }

    // okretanje vise figura zaredom u istom nizu
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

    // na pocetku igre mora da postoji bar jedan potez
    @Test
    @Order(10)
    public void hasLegalMovesTrueAtGameStart() {
        assertTrue(widget.hasLegalMoves());
    }

    // puna tabla znaci da vise nema poteza
    @Test
    @Order(11)
    public void hasLegalMovesFalseWhenBoardFull() {
        fillBoard(Color.BLACK);
        assertFalse(widget.hasLegalMoves());
    }

    // ista provera poteza samo preko preopterecene verzije sa argumentima
    @Test
    @Order(12)
    public void hasLegalMovesWithArgsShouldFindLegalMoveForBlackAtStart() {
        assertTrue(widget.hasLegalMoves(0, widget.getSpotAt(3, 4)));
    }

    // igra nije gotova dok god postoje potezi
    @Test
    @Order(13)
    public void checkWinFalseWhileLegalMovesRemain() {
        assertFalse(widget.checkWin());
    }

    // pobeda crnog kad je cela tabla crna
    @Test
    @Order(14)
    public void checkWinTrueAndBlackWinsWhenBoardFullOfBlack() {
        fillBoard(Color.BLACK);
        assertTrue(widget.checkWin());
        assertTrue(widget._noBlack > widget._noWhite);
        assertTrue(widget.checkWin());
    }

    // pobeda belog kad je cela tabla bela
    @Test
    @Order(15)
    public void checkWinTrueAndWhiteWinsWhenBoardFullOfWhite() {
        fillBoard(Color.WHITE);
        assertTrue(widget.checkWin());
        assertTrue(widget._noWhite > widget._noBlack);
    }

    // nereseno kad je broj figura izjednacen na popunjenoj tabli
    @Test
    @Order(16)
    public void checkWinTrueAndDrawWhenCountsAreEqual() {
        fillBoardSplit(32);
        assertTrue(widget.checkWin());
        assertEquals(widget._noBlack, widget._noWhite);
    }

    // na pocetku igre brojevi figura nisu isti, nema neresenog
    @Test
    @Order(17)
    public void checkDrawFalseWhenCountsAreUnequal() {
        assertFalse(widget.checkDraw());
    }

    // nereseno kad su brojevi figura izjednaceni
    @Test
    @Order(18)
    public void checkDrawShouldBeTrueWhenCountsAreEqual() {
        fillBoardSplit(32);
        assertTrue(widget.checkDraw());
    }

    // vraca boju polja, prazno/crno/belo, po koordinatama
    @Test
    @Order(19)
    public void getColorReturnsEmptyBlackAndWhite() {
        assertEquals(-1, widget.getColor(0, 0));
        assertEquals(0, widget.getColor(3, 4));
        assertEquals(1, widget.getColor(3, 3));
    }

    // ista provera boje preko spota, ukljucujuci i cudnu boju van skupa
    @Test
    @Order(20)
    public void getColorSpotOverloadCoversAllBranches() {
        assertEquals(-1, widget.getColor(widget.getSpotAt(0, 0)));
        assertEquals(0, widget.getColor(widget.getSpotAt(3, 4)));
        assertEquals(1, widget.getColor(widget.getSpotAt(3, 3)));

        Spot weird = widget.getSpotAt(1, 1);
        weird.setSpotColor(Color.RED);
        assertEquals(-2, widget.getColor(weird));
    }

    // vraca sirovu boju polja bez prevoda u brojeve
    @Test
    @Order(21)
    public void getColorCReturnsRawSpotColor() {
        assertEquals(Color.WHITE, widget.getColorC(3, 3));
        assertEquals(Color.BLACK, widget.getColorC(3, 4));
    }

    // granice table proverene za sva cetiri ruba posebno
    @Test
    @Order(22)
    public void inBoundsSpotOverloadCoversAllFourConditions() {
        assertTrue(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 0, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, -1, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 0, -1)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 3, 8)));
    }

    // klik na prazno legalno polje postavlja figuru i hvata protivnika
    @Test
    @Order(23)
    public void spotClickedOnEmptyLegalSpotPlacesPieceAndFlipsCapture() {
        widget.spotClicked(widget.getSpotAt(2, 3));

        assertFalse(widget.getSpotAt(2, 3).isEmpty());
        assertEquals(Color.BLACK, widget.getSpotAt(2, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 3).getSpotColor());
    }

    // klik na vec zauzeto polje se ignorise, nista se ne menja
    @Test
    @Order(24)
    public void spotClickedOnOccupiedSpotAfterSetupIsIgnored() {
        widget.spotClicked(widget.getSpotAt(3, 3));

        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertFalse(widget.getSpotAt(3, 3).isEmpty());
    }
}
