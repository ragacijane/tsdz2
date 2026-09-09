package othello;

import org.junit.jupiter.api.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JSpotTest {

    private JSpot spot;

    private static class RecordingListener implements SpotListener {
        List<String> events = new ArrayList<>();

        @Override
        public void spotClicked(Spot s) {
            events.add("clicked");
        }

        @Override
        public void spotEntered(Spot s) {
            events.add("entered");
        }

        @Override
        public void spotExited(Spot s) {
            events.add("exited");
        }
    }

    @BeforeEach
    public void beforeEach() {
        spot = new JSpot(Color.GRAY, Color.BLACK, Color.YELLOW, null, 2, 3);
    }

    // почетно стање, боје и координате постављене преко конструктора
    @Test
    @Order(1)
    public void constructorSetsColorsAndCoordinates() {
        assertEquals(Color.GRAY, spot.getBackground());
        assertEquals(Color.BLACK, spot.getSpotColor());
        assertEquals(Color.YELLOW, spot.getHighlight());
        assertEquals(2, spot.getSpotX());
        assertEquals(3, spot.getSpotY());
        assertNull(spot.getBoard());
        assertTrue(spot.isEmpty());
        assertFalse(spot.isHighlighted());
    }

    // сет/клир/тогл мењају празно стање поља, то се касније и црта
    @Test
    @Order(2)
    public void setClearAndToggleSpotChangeEmptyState() {
        spot.setSpot();
        assertFalse(spot.isEmpty());
        spot.clearSpot();
        assertTrue(spot.isEmpty());
        spot.toggleSpot();
        assertFalse(spot.isEmpty());
        spot.toggleSpot();
        assertTrue(spot.isEmpty());
    }

    // хајлајт/анхајлајт и тогл хајлајта, за жути оквир око поља
    @Test
    @Order(3)
    public void highlightUnhighlightAndToggleChangeHighlightState() {
        spot.highlightSpot();
        assertTrue(spot.isHighlighted());
        spot.unhighlightSpot();
        assertFalse(spot.isHighlighted());
        spot.toggleHighlight();
        assertTrue(spot.isHighlighted());
        spot.toggleHighlight();
        assertFalse(spot.isHighlighted());
    }

    // null боја фигуре није дозвољена
    @Test
    @Order(4)
    public void setSpotColorNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> spot.setSpotColor(null));
    }

    // null боја хајлајта није дозвољена
    @Test
    @Order(5)
    public void setHighlightNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> spot.setHighlight(null));
    }

    // клик мишем се преводи у spotClicked позив свим регистрованим слушаоцима
    @Test
    @Order(6)
    public void mouseClickedNotifiesAllListeners() {
        RecordingListener l1 = new RecordingListener();
        RecordingListener l2 = new RecordingListener();
        spot.addSpotListener(l1);
        spot.addSpotListener(l2);

        spot.mouseClicked(null);

        assertEquals(List.of("clicked"), l1.events);
        assertEquals(List.of("clicked"), l2.events);
    }

    // улазак и излазак миша се преводе у entered/exited позиве
    @Test
    @Order(7)
    public void mouseEnteredAndExitedNotifyListener() {
        RecordingListener l = new RecordingListener();
        spot.addSpotListener(l);

        spot.mouseEntered(null);
        spot.mouseExited(null);

        assertEquals(List.of("entered", "exited"), l.events);
    }

    // уклоњени слушалац више не добија обавештења
    @Test
    @Order(8)
    public void removedListenerReceivesNoMoreEvents() {
        RecordingListener l = new RecordingListener();
        spot.addSpotListener(l);
        spot.removeSpotListener(l);

        spot.mouseClicked(null);
        spot.mouseEntered(null);
        spot.mouseExited(null);

        assertTrue(l.events.isEmpty());
    }

    // притисак и отпуштање дугмета миша намерно ништа не раде
    @Test
    @Order(9)
    public void mousePressedAndReleasedAreNoOps() {
        RecordingListener l = new RecordingListener();
        spot.addSpotListener(l);

        spot.mousePressed(null);
        spot.mouseReleased(null);

        assertTrue(l.events.isEmpty());
    }

    // координатни стринг из подразумеване методе интерфејса
    @Test
    @Order(10)
    public void coordStringMatchesXAndY() {
        assertEquals("(2, 3)", spot.getCoordString());
    }
}
