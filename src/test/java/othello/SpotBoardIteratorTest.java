package othello;

import org.junit.jupiter.api.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpotBoardIteratorTest {

    private SpotBoard board;

    @BeforeEach
    public void beforeEach() {
        board = new JSpotBoard(2, 2);
    }

    // хеснект тачно на почетку, пре иједног некста
    @Test
    @Order(1)
    public void hasNextIsTrueWhenIterationNotStarted() {
        SpotBoardIterator it = new SpotBoardIterator(board);
        assertTrue(it.hasNext());
    }

    // редослед обиласка поља, ред по ред од горе ка доле
    @Test
    @Order(2)
    public void nextVisitsEverySpotInRowMajorOrder() {
        SpotBoardIterator it = new SpotBoardIterator(board);

        Spot s1 = it.next();
        assertEquals(0, s1.getSpotX());
        assertEquals(0, s1.getSpotY());

        Spot s2 = it.next();
        assertEquals(1, s2.getSpotX());
        assertEquals(0, s2.getSpotY());

        Spot s3 = it.next();
        assertEquals(0, s3.getSpotX());
        assertEquals(1, s3.getSpotY());

        Spot s4 = it.next();
        assertEquals(1, s4.getSpotX());
        assertEquals(1, s4.getSpotY());
    }

    // кад се обиђу сва поља хеснект пада на фалс
    @Test
    @Order(3)
    public void hasNextIsFalseAfterLastSpotVisited() {
        SpotBoardIterator it = new SpotBoardIterator(board);
        for (int i = 0; i < 4; i++) {
            it.next();
        }
        assertFalse(it.hasNext());
    }

    // позив некста после краја баца изузетак
    @Test
    @Order(4)
    public void nextThrowsWhenExhausted() {
        SpotBoardIterator it = new SpotBoardIterator(board);
        for (int i = 0; i < 4; i++) {
            it.next();
        }
        assertThrows(NoSuchElementException.class, it::next);
    }

    // фор-ич петља прође свако поље тачно једном
    @Test
    @Order(5)
    public void boardForEachLoopVisitsAllSpotsExactlyOnce() {
        int count = 0;
        for (Spot s : board) {
            assertNotNull(s);
            count++;
        }
        assertEquals(4, count);
    }
}
