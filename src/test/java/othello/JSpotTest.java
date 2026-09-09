package othello;

import org.junit.jupiter.api.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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

    // pocetno stanje, boje i koordinate postavljene preko konstruktora
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

    // set/clear/toggle menjaju prazno stanje polja, to se kasnije i crta
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

    // highlight/unhighlight i toggle highlighta, za zuti okvir oko polja
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

    // null boja figure nije dozvoljena
    @Test
    @Order(4)
    public void setSpotColorNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> spot.setSpotColor(null));
    }

    // null boja highlighta nije dozvoljena
    @Test
    @Order(5)
    public void setHighlightNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> spot.setHighlight(null));
    }

    // klik misem se prevodi u spotClicked poziv svim registrovanim slusaocima
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

    // ulazak i izlazak misa se prevode u entered/exited pozive
    @Test
    @Order(7)
    public void mouseEnteredAndExitedNotifyListener() {
        RecordingListener l = new RecordingListener();
        spot.addSpotListener(l);

        spot.mouseEntered(null);
        spot.mouseExited(null);

        assertEquals(List.of("entered", "exited"), l.events);
    }

    // uklonjeni slusalac vise ne dobija obavestenja
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

    // pritisak i otpustanje dugmeta misa namerno nista ne rade
    @Test
    @Order(9)
    public void mousePressedAndReleasedAreNoOps() {
        RecordingListener l = new RecordingListener();
        spot.addSpotListener(l);

        spot.mousePressed(null);
        spot.mouseReleased(null);

        assertTrue(l.events.isEmpty());
    }

    // koordinatni string iz podrazumevane metode interfejsa
    @Test
    @Order(10)
    public void coordStringMatchesXAndY() {
        assertEquals("(2, 3)", spot.getCoordString());
    }

    // set highlight sa validnom bojom pokriva suprotnu granu od null provere
    // pronasao claude - nedostajala grana u coverage izvestaju
    @Test
    @Order(11)
    public void setHighlightWithValidColorUpdatesHighlight() {
        spot.setHighlight(Color.RED);
        assertEquals(Color.RED, spot.getHighlight());
    }

    // paintComponent pokriva obe grane - hajlajtovano+popunjeno polje i prazno+neobelezeno
    // pronasao claude - paintComponent se nigde drugde ne poziva pa je ostajao nepokriven
    @Test
    @Order(12)
    public void paintComponentCoversHighlightAndFillBranches() {
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        spot.highlightSpot();
        spot.setSpot();
        assertDoesNotThrow(() -> spot.paintComponent(g));

        spot.unhighlightSpot();
        spot.clearSpot();
        assertDoesNotThrow(() -> spot.paintComponent(g));

        g.dispose();
    }
}
