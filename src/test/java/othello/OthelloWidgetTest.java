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

    // почетни распоред 4 фигуре у центру табле
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

    // не може да се игра на већ заузето поље
    @Test
    @Order(2)
    public void isLegalMoveReturnsFalseForOccupiedSpot() {
        assertFalse(widget.isLegalMove(widget.getSpotAt(3, 3)));
    }

    // потез који хвата противника мора да буде дозвољен
    @Test
    @Order(3)
    public void isLegalMoveReturnsTrueForCapturingMove() {
        assertTrue(widget.isLegalMove(widget.getSpotAt(2, 3)));
    }

    // гомила случајева легалних и нелегалних потеза из csv фајла
    @ParameterizedTest
    @Order(4)
    @CsvFileSource(resources = "/legalmoves.csv", numLinesToSkip = 1)
    public void isLegalMoveParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.isLegalMove(widget.getSpotAt(x, y)));
    }

    // провера граница табле за гомилу координата из csv фајла
    @ParameterizedTest
    @Order(5)
    @CsvFileSource(resources = "/inbounds.csv", numLinesToSkip = 1)
    public void inBoundsParameterizedTest(int x, int y, boolean expected) {
        assertEquals(expected, widget.inBounds(x, y));
    }

    // да ли се верзија са спотом и верзија са координатама слажу
    @Test
    @Order(6)
    public void inBoundsSpotOverloadMatchesCoordinateOverload() {
        assertTrue(widget.inBounds(widget.getSpotAt(0, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 8, 8)));
    }

    // окретање фигуре кад је ухваћена у једном правцу
    @Test
    @Order(7)
    public void flipPiecesFlipsCapturedPieceInOneDirection() {
        widget.flipPieces(widget.getSpotAt(2, 3));
        assertEquals(Color.BLACK, widget.getSpotAt(3, 3).getSpotColor());
        assertTrue(widget.getSpotAt(2, 3).isEmpty());
    }

    // ништа се не окреће ако низ хватања није завршен до краја
    @Test
    @Order(8)
    public void flipPiecesDoesNothingWhenNoCaptureChainCompletes() {
        widget.flipPieces(widget.getSpotAt(0, 0));
        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 4).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(4, 3).getSpotColor());
        assertEquals(Color.WHITE, widget.getSpotAt(4, 4).getSpotColor());
    }

    // окретање више фигура заредом у истом низу
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

    // на почетку игре мора да постоји бар један потез
    @Test
    @Order(10)
    public void hasLegalMovesTrueAtGameStart() {
        assertTrue(widget.hasLegalMoves());
    }

    // пуна табла значи да више нема потеза
    @Test
    @Order(11)
    public void hasLegalMovesFalseWhenBoardFull() {
        fillBoard(Color.BLACK);
        assertFalse(widget.hasLegalMoves());
    }

    // иста провера потеза само преко преоптерећене верзије са аргументима
    @Test
    @Order(12)
    public void hasLegalMovesWithArgsShouldFindLegalMoveForBlackAtStart() {
        assertTrue(widget.hasLegalMoves(0, widget.getSpotAt(3, 4)));
    }

    // игра није готова док год постоје потези
    @Test
    @Order(13)
    public void checkWinFalseWhileLegalMovesRemain() {
        assertFalse(widget.checkWin());
    }

    // победа црног кад је цела табла црна
    @Test
    @Order(14)
    public void checkWinTrueAndBlackWinsWhenBoardFullOfBlack() {
        fillBoard(Color.BLACK);
        assertTrue(widget.checkWin());
        assertTrue(widget._noBlack > widget._noWhite);
        assertTrue(widget.checkWin());
    }

    // победа белог кад је цела табла бела
    @Test
    @Order(15)
    public void checkWinTrueAndWhiteWinsWhenBoardFullOfWhite() {
        fillBoard(Color.WHITE);
        assertTrue(widget.checkWin());
        assertTrue(widget._noWhite > widget._noBlack);
    }

    // нерешено кад је број фигура изједначен на попуњеној табли
    @Test
    @Order(16)
    public void checkWinTrueAndDrawWhenCountsAreEqual() {
        fillBoardSplit(32);
        assertTrue(widget.checkWin());
        assertEquals(widget._noBlack, widget._noWhite);
    }

    // на почетку игре бројеви фигура нису исти, нема нерешеног
    @Test
    @Order(17)
    public void checkDrawFalseWhenCountsAreUnequal() {
        assertFalse(widget.checkDraw());
    }

    // нерешено кад су бројеви фигура изједначени
    @Test
    @Order(18)
    public void checkDrawShouldBeTrueWhenCountsAreEqual() {
        fillBoardSplit(32);
        assertTrue(widget.checkDraw());
    }

    // враћа боју поља, празно/црно/бело, по координатама
    @Test
    @Order(19)
    public void getColorReturnsEmptyBlackAndWhite() {
        assertEquals(-1, widget.getColor(0, 0));
        assertEquals(0, widget.getColor(3, 4));
        assertEquals(1, widget.getColor(3, 3));
    }

    // иста провера боје преко спота, укључујући и чудну боју ван скупа
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

    // враћа сирову боју поља без превода у бројеве
    @Test
    @Order(21)
    public void getColorCReturnsRawSpotColor() {
        assertEquals(Color.WHITE, widget.getColorC(3, 3));
        assertEquals(Color.BLACK, widget.getColorC(3, 4));
    }

    // границе табле проверене за сва четири руба посебно
    @Test
    @Order(22)
    public void inBoundsSpotOverloadCoversAllFourConditions() {
        assertTrue(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 0, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, -1, 0)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 0, -1)));
        assertFalse(widget.inBounds(new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 3, 8)));
    }

    // клик на празно легално поље поставља фигуру и хвата противника
    @Test
    @Order(23)
    public void spotClickedOnEmptyLegalSpotPlacesPieceAndFlipsCapture() {
        widget.spotClicked(widget.getSpotAt(2, 3));

        assertFalse(widget.getSpotAt(2, 3).isEmpty());
        assertEquals(Color.BLACK, widget.getSpotAt(2, 3).getSpotColor());
        assertEquals(Color.BLACK, widget.getSpotAt(3, 3).getSpotColor());
    }

    // клик на већ заузето поље се игнорише, ништа се не мења
    @Test
    @Order(24)
    public void spotClickedOnOccupiedSpotAfterSetupIsIgnored() {
        widget.spotClicked(widget.getSpotAt(3, 3));

        assertEquals(Color.WHITE, widget.getSpotAt(3, 3).getSpotColor());
        assertFalse(widget.getSpotAt(3, 3).isEmpty());
    }
}
