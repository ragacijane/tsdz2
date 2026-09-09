package othello;

import org.junit.jupiter.api.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JSpotBoardTest {

    private JSpotBoard board;

    private static class RecordingListener implements SpotListener {
        List<Spot> clicked = new ArrayList<>();

        @Override
        public void spotClicked(Spot s) {
            clicked.add(s);
        }

        @Override
        public void spotEntered(Spot s) {
        }

        @Override
        public void spotExited(Spot s) {
        }
    }

    @BeforeEach
    public void beforeEach() {
        board = new JSpotBoard(3, 2);
    }

    // димензије табле одговарају прослеђеним width/height
    @Test
    @Order(1)
    public void constructorSetsUpCorrectDimensions() {
        assertEquals(3, board.getSpotWidth());
        assertEquals(2, board.getSpotHeight());
    }

    // недозвољена геометрија табле баца изузетак
    @Test
    @Order(2)
    public void illegalGeometryThrows() {
        assertThrows(IllegalArgumentException.class, () -> new JSpotBoard(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new JSpotBoard(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new JSpotBoard(51, 5));
        assertThrows(IllegalArgumentException.class, () -> new JSpotBoard(5, 51));
    }

    // свако поље на табли постоји и зна своје координате
    @Test
    @Order(3)
    public void getSpotAtReturnsSpotWithMatchingCoordinates() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                Spot s = board.getSpotAt(x, y);
                assertNotNull(s);
                assertEquals(x, s.getSpotX());
                assertEquals(y, s.getSpotY());
            }
        }
    }

    // ван граница табле баца изузетак
    @Test
    @Order(4)
    public void getSpotAtOutOfBoundsThrows() {
        assertThrows(IllegalArgumentException.class, () -> board.getSpotAt(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getSpotAt(0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.getSpotAt(3, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getSpotAt(0, 2));
    }

    // addSpotListener региструје слушаоца на свако поље табле
    @Test
    @Order(5)
    public void addSpotListenerRegistersOnEverySpot() {
        RecordingListener l = new RecordingListener();
        board.addSpotListener(l);

        for (Spot s : board) {
            ((JSpot) s).mouseClicked(null);
        }

        assertEquals(6, l.clicked.size());
    }

    // removeSpotListener скида слушаоца са свих поља
    @Test
    @Order(6)
    public void removeSpotListenerUnregistersFromEverySpot() {
        RecordingListener l = new RecordingListener();
        board.addSpotListener(l);
        board.removeSpotListener(l);

        for (Spot s : board) {
            ((JSpot) s).mouseClicked(null);
        }

        assertTrue(l.clicked.isEmpty());
    }

    // итератор() враћа праву spotboarditerator инстанцу
    @Test
    @Order(7)
    public void iteratorReturnsSpotBoardIterator() {
        assertInstanceOf(SpotBoardIterator.class, board.iterator());
    }

    // конструктор са две боје шаховски распоређује позадине по колонама
    @Test
    @Order(8)
    public void twoColorConstructorAlternatesBackgroundsByColumn() {
        JSpotBoard twoColorBoard = new JSpotBoard(2, 2, Color.RED, Color.BLUE);

        assertEquals(Color.RED, twoColorBoard.getSpotAt(0, 0).getBackground());
        assertEquals(Color.BLUE, twoColorBoard.getSpotAt(1, 0).getBackground());
        assertEquals(Color.RED, twoColorBoard.getSpotAt(0, 1).getBackground());
        assertEquals(Color.BLUE, twoColorBoard.getSpotAt(1, 1).getBackground());
    }

    // конструктор са једном додатном бојом прави шаховски распоред по (x+y)
    @Test
    @Order(9)
    public void singleColorConstructorAlternatesBackgroundsCheckerboard() {
        JSpotBoard singleColorBoard = new JSpotBoard(2, 2, Color.PINK);

        assertEquals(Color.PINK, singleColorBoard.getSpotAt(1, 0).getBackground());
        assertEquals(Color.PINK, singleColorBoard.getSpotAt(0, 1).getBackground());
        assertNotEquals(Color.PINK, singleColorBoard.getSpotAt(0, 0).getBackground());
        assertNotEquals(Color.PINK, singleColorBoard.getSpotAt(1, 1).getBackground());
    }
}
