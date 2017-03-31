package de.lighti.clipper.gui;

import com.soywiz.korio.crypto.Base64;
import de.lighti.clipper.*;
import de.lighti.clipper.Clipper.*;
import de.lighti.clipper.Point.LongPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

public class PolygonCanvas extends JPanel {

    private final Paths subjects = new Paths();

    private final Paths clips = new Paths();

    private final Paths solution = new Paths();

    /**
     *
     */
    private static final long serialVersionUID = 3171660079906158448L;

    private final static int MARGIN = 10;

    private ClipType clipType = ClipType.INTERSECTION;

    private PolyFillType fillType = PolyFillType.EVEN_ODD;
    private float zoom = 0.000001f;
    private int vertexCount = ClipperDialog.DEFAULT_VERTEX_COUNT;

    private final StatusBar statusBar;
    private float offset;

    private Point origin;
    private final Point cur;

    public PolygonCanvas(StatusBar statusBar) {
        setBackground(Color.WHITE);
        this.statusBar = statusBar;
        zoom = 1f;
        origin = new Point();
        cur = new Point();

        addMouseWheelListener(e -> {

            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                zoom += zoom * e.getUnitsToScroll() / 10f;
                statusBar.setText("Zoomlevel: " + zoom);
                PolygonCanvas.this.repaint();
            }

        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                final Point p = e.getPoint();

                cur.x = p.x;
                cur.y = p.y;
                repaint();
            }
        });
    }

    private Polygon createPolygonFromPath(Path p) {
        final int[] x = new int[p.size()];
        final int[] y = new int[p.size()];

        for (int i = p.size() - 1; i >= 0; i--) {
            final LongPoint ip = p.get(i);
            x[i] = (int) (ip.getX() * zoom) + cur.x - origin.x;
            y[i] = (int) (ip.getY() * zoom) + cur.y - origin.y;
        }

        return new Polygon(x, y, p.size());
    }

    public void generateAustPlusRandomEllipses() {
        final int scale = 1;
        subjects.clear();
        //load map of Australia from resource ...
        LittleEndianDataInputStream polyStream = null;
        try {

            String austBase64 = "AgAAABYDAAAK19FDZ2YoQwrX0UPieipDKlzSQ1yPLENnZtNDmpktQ2dm00MVri9D" +
                    "Z2bTQ4/CMUOF69NDC9czQ6Nw1EOF6zVDo3DUQwAAOEPietVDAAA4Q+J61UOF6zVD" +
                    "H4XWQ0jhNEM9CtdDw/U2Q3sU2EMAADhDuB7ZQz4KOUOamdhDw/U2Q9ej2UPD9TZD" +
                    "9yjaQz4KOUP3KNpDuB47Q/co2kMzMz1D9yjaQ69HP0P3KNpDKVxBQxWu2kOkcEND" +
                    "UrjbQ6RwQ0OQwtxDH4VFQ83M3UOamUdDCtfeQ9ejSENI4d9DFa5JQ2dm4EOPwktD" +
                    "o3DhQwvXTUPieuJDSOFOQwAA40PD9VBDH4XjQz4KU0Ndj+RDuB5VQ5qZ5UMzM1dD" +
                    "exTlQ69HWUOameVDKlxbQ7ge5kOjcF1DuB7mQx+FX0O4HuZDmplhQ7ge5kMVrmND" +
                    "uB7mQ5DCZUPXo+ZDCtdnQ5qZ5UNI4WhD16PmQ8P1akP3KOdDPQptQxWu50O4Hm9D" +
                    "Fa7nQzMzcUMzM+hDr0dzQ1K46EMqXHVDUrjoQ6Nwd0NSuOhDH4V5QzMz6EOamXtD" +
                    "Fa7nQxWufUMVrudDkMJ/Q/co50OF64BD9yjnQ8P1gUPXo+ZDAACDQ9ej5kM+CoRD" +
                    "uB7mQ3sUhUOameVDuB6GQ5qZ5UP2KIdDuB7mQzMziEOameVDcT2JQ3sU5UOvR4pD" +
                    "exTlQ+tRi0Ndj+RDKVyMQx+F40NnZo1DH4XjQ6RwjkMAAOND4nqPQ8P14UMAAJBD" +
                    "hevgQz4KkUNI4d9DXI+RQwrX3kOamZJD61HeQ9ejk0PNzN1DFa6UQ5DC3ENSuJVD" +
                    "kMLcQ4/ClkNwPdxDzcyXQ1K420ML15hDFa7aQ0jhmUMVrtpDheuaQ/co2kPD9ZtD" +
                    "9yjaQwAAnUO4HtlDPgqeQ5qZ2EN7FJ9DexTYQ7geoEM9CtdD16OgQz0K10MVrqFD" +
                    "H4XWQ1K4okMfhdZDj8KjQx+F1kPNzKRDAADWQwvXpUPietVDSOGmQ+J61UOF66dD" +
                    "AADWQ8P1qEMAANZDAACqQ8P11EMAAKpDhevTQz4Kq0NI4dJDXI+rQwrX0UNcj6tD" +
                    "zczQQ1yPq0OQws9DXI+rQ1K4zkNcj6tDFa7NQ1yPq0PXo8xDexSsQ5qZy0N7FKxD" +
                    "PgrKQ5qZrEMAAMlDuB6tQ8P1x0P2KK5DhevGQxWurkNI4cVDUrivQwvXxENxPbBD" +
                    "zczDQ3E9sEOPwsJDcT2wQ4/CwkOvR7FDzczDQ+tRskOPwsJDzcyxQ1K4wUOPwrBD" +
                    "Fa7AQ69HsUP2KMBDcT2wQ7gev0NxPbBDexS+QzMzr0Oamb5D9iiuQ1yPvUPXo61D" +
                    "Pgq9QxWurkMAALxDMzOvQ8P1ukMVrq5DAAC8Q/YorkMfhbxDuB6tQwAAvEN7FKxD" +
                    "w/W6Q5qZrEOF67lDuB6tQ0jhuEO4Hq1Dheu5Q9ejrUNI4bhD9iiuQwvXt0MVrq5D" +
                    "zcy2Q1K4r0NxPbVDcT2wQzMztEOvR7FD9iizQ4/CsEO4HrJDcT2wQ3sUsUNSuK9D" +
                    "PgqwQzMzr0MAAK9DFa6uQ8P1rUMVrq5DheusQxWurkNI4atD9iiuQwvXqkP2KK5D" +
                    "zcypQxWurkOPwqhD9iiuQ1K4p0O4Hq1DFa6mQ7gerUPXo6VDuB6tQ5qZpEOamaxD" +
                    "XI+jQ1yPq0M+CqNDH4WqQwAAokMAAKpDw/WgQ8P1qEPD9aBDheunQ8P1oENI4aZD" +
                    "4nqhQwvXpUPD9aBDzcykQ6RwoEOPwqNDheufQ1K4okNI4Z5DFa6hQwvXnUPXo6BD" +
                    "zcycQ7geoEML151D16OgQ0jhnkMVrqFDheufQ1K4okNI4Z5DFa6hQwvXnUPXo6BD" +
                    "C9edQ5qZn0MpXJ5D16OgQylcnkOamZ9DKVyeQ1yPnkPrUZ1DexSfQ4/Cm0OamZ9D" +
                    "zcycQ5qZn0OPwptDuB6gQ1K4mkO4HqBDFa6ZQ9ejoEPXo5hD16OgQ7gemEOamZ9D" +
                    "9iiZQ3sUn0MzM5pDPgqeQzMzmkMAAJ1DMzOaQ8P1m0MVrplDheuaQ/YomUNI4ZlD" +
                    "16OYQwvXmEOamZdDKVyZQ3sUl0NnZppDexSXQ6Rwm0Ncj5ZD4nqcQz4KlkMfhZ1D" +
                    "AACVQx+FnUPD9ZNDPgqeQ4XrkkM+Cp5DSOGRQ1yPnkNnZpJDH4WdQ4XrkkPiepxD" +
                    "w/WTQ+J6nEMAAJVD4nqcQwAAlUOkcJtDAACVQ0jhmUMAAJVDC9eYQwAAlUPNzJdD" +
                    "H4WVQ4/ClkNcj5ZDUriVQ3sUl0MVrpRDexSXQ9ejk0OamZdDmpmSQ3sUl0Ncj5FD" +
                    "XI+WQx+FkENcj5ZDXI+RQz4KlkOamZJDAACVQ7gek0PiepRD9iiUQ8P1k0MzM5VD" +
                    "pHCTQ3E9lkNnZpJDcT2WQylckUOPwpZD61GQQ69Hl0OvR49DzcyXQ4/CjkML15hD" +
                    "UriNQ0jhmUMVroxDheuaQ/YojEPD9ZtDMzONQ6Rwm0MzM41D4nqcQ/YojEPiepxD" +
                    "uB6LQ+J6nEN7FIpDpHCbQz4KiUOF65pDexSKQ4XrmkN7FIpDSOGZQ1yPiUML15hD" +
                    "XI+JQ83Ml0MfhYhDj8KWQ+J6h0NxPZZD4nqHQzMzlUOkcIZD9iiUQ4XrhUO4HpND" +
                    "SOGEQ9ejk0ML14NDmpmSQ+tRg0Ncj5FDKVyEQz4KkUML14NDAACQQ83MgkMAAJBD" +
                    "r0eCQ8P1jkNxPYFDpHCOQzMzgEOkcI5D61F+Q6RwjkNwPXxDpHCOQ/coekNnZo1D" +
                    "exR4Q2dmjUMAAHZDZ2aNQ4Xrc0NnZo1DCtdxQ0jhjEOQwm9DKVyMQxWubUML14tD" +
                    "mplrQ+tRi0MfhWlDzcyKQ6NwZ0PNzIpDKlxlQ+tRi0OvR2ND61GLQzMzYUPrUYtD" +
                    "uB5fQ+tRi0M9Cl1D61GLQ8P1WkPrUYtDSOFYQ+tRi0PNzFZD61GLQ1K4VEML14tD" +
                    "16NSQwvXi0Ndj1BDC9eLQ+J6TkMpXIxDZ2ZMQ0jhjEPrUUpDZ2aNQ3E9SEOF641D" +
                    "9ihGQ4XrjUN7FERDpHCOQwAAQkOkcI5Dhes/Q8P1jkML1z1Dw/WOQ4/CO0PD9Y5D" +
                    "Fa45Q8P1jkOamTdDw/WOQx+FNUPD9Y5DpHAzQ8P1jkMpXDFDw/WOQ69HL0Pieo9D" +
                    "MzMtQx+FkEO4HitDH4WQQz4KKUM+CpFDw/UmQ1yPkUNI4SRDexSSQ83MIkN7FJJD" +
                    "UrggQ5qZkkPXox5D16OTQ5qZHUMVrpRDXI8cQ1K4lUPiehpDj8KWQ2dmGEOvR5dD" +
                    "61EWQ69Hl0NxPRRDr0eXQ/YoEkOvR5dDexQQQ69Hl0MAAA5Dr0eXQ4XrC0OvR5dD" +
                    "C9cJQ83Ml0PNzAhDj8KWQ1K4BkOvR5dD16MEQ4/ClkNcjwJDj8KWQ+J6AEOvR5dD" +
                    "zcz8Qq9Hl0PXo/hCr0eXQ+J69EKvR5dD61HwQs3Ml0N7FOpCr0eXQ4Xr5ULNzJdD" +
                    "kMLhQs3Ml0MVrt9CC9eYQ5qZ3UJI4ZlDo3DZQmdmmkOvR9VCZ2aaQ7ge0UJnZppD" +
                    "w/XMQoXrmkPNzMhCpHCbQ1K4xkLiepxDXI/CQuJ6nENnZr5CAACdQ3E9ukIfhZ1D" +
                    "exS2Qh+FnUOF67FCAACdQ4/CrUIAAJ1DmpmpQgAAnUOkcKVCAACdQ69HoUIAAJ1D" +
                    "uB6dQgAAnUPD9ZhC4nqcQ83MlELD9ZtD16OQQoXrmkPieoxCZ2aaQ+tRiEJI4ZlD" +
                    "9iiEQilcmUMAAIBCKVyZQwAAgELrUZhDAACAQq9Hl0MAAIBCcT2WQ/YohEJSuJVD" +
                    "61GIQlK4lUPieoxCFa6UQ1yPjkLXo5NDXI+OQpqZkkPieoxCXI+RQ+J6jEIfhZBD" +
                    "16OQQh+FkENcj45C4nqPQ1yPjkKkcI5DXI+OQmdmjUNcj45CKVyMQ+J6jELrUYtD" +
                    "Z2aKQq9HikPrUYhCcT2JQ3E9hkIzM4hD9iiEQvYoh0N7FIJCuB6GQwAAgEJ7FIVD" +
                    "AACAQj4KhEMAAIBCAACDQwrXe0LD9YFDAACAQoXrgEMK13tCkMJ/QwrXe0IVrn1D" +
                    "H4VzQpqZe0MqXG9C4np4QzMza0JnZnZDPQpnQutRdENSuF5Cr0dzQ12PWkIzM3FD" +
                    "XY9aQrgeb0Ndj1pCPQptQ2dmVkLD9WpDcD1SQkjhaEN7FE5CzcxmQ4XrSUJSuGRD" +
                    "mplBQtejYkOvRzlCmplhQ69HOUIfhV9Dr0c5QqNwXUOvRzlCH4VfQ6RwPUKjcF1D" +
                    "mplBQh+FX0OamUFCmplhQ4XrSULXo2JDcD1SQl2PYEOF60lC4npeQ5qZQUJnZlxD" +
                    "pHA9QutRWkOamUFCcD1YQ4XrSULrUVpDhetJQmdmXEN7FE5C4npeQ3A9UkJnZlxD" +
                    "cD1SQuJ6XkNnZlZCXY9gQ1K4XkIfhV9DUrheQqNwXUNSuF5CKlxbQ12PWkKvR1lD" +
                    "Z2ZWQjMzV0NwPVJCuB5VQ4XrSUI+ClNDhetJQsP1UEOPwkVCSOFOQ6RwPULNzExD" +
                    "pHA9QlK4SkOkcD1C16NIQ5qZQUJcj0ZDj8JFQuJ6REOF60lCZ2ZCQ3sUTkLrUUBD" +
                    "exROQnE9PkN7FE5C9ig8Q4XrSUJ7FDpDhetJQgAAOEN7FE5Ches1Q3A9UkIL1zND" +
                    "Z2ZWQo/CMUNSuF5CUrgwQ12PWkLNzDJDXY9aQkjhNENdj1pCw/U2Q0jhYkIAADhD" +
                    "PQpnQoXrNUMzM2tCC9czQypcb0KPwjFDFa53QlK4MEMAAIBCFa4vQ/YohELXoy5D" +
                    "61GIQpqZLUPieoxCH4UrQ9ejkELieipDzcyUQmdmKEPD9ZhCKVwnQ7genULrUSZD" +
                    "r0ehQq9HJUOkcKVCcT0kQ5qZqUKvRyVDj8KtQq9HJUOF67FCr0clQ3sUtkKvRyVD" +
                    "cT26QnE9JENnZr5CMzMjQ1yPwkL2KCJDUrjGQrgeIUNI4cpCuB4hQz0Kz0K4HiFD" +
                    "MzPTQj4KH0MqXNdCAAAeQx+F20IAAB5DFa7fQj4KH0MK1+NCAAAeQwAA6ELD9RxD" +
                    "9yjsQsP1HEPrUfBChesbQ+J69EKF6xtD16P4QkjhGkPNzPxCC9cZQ+J6AEOPwhdD" +
                    "XI8CQxWuFUOamQNDmpkTQ9ejBEMfhRFDUrgGQ6RwD0PNzAhDKVwNQ0jhCkOvRwtD" +
                    "w/UMQ3E9CkNI4QpD9igIQ0jhCkN7FAZDC9cJQwAABENI4QpDhesBQ4XrC0MVrv9C" +
                    "AAAOQ5qZ/UI+Cg9Do3D5QrgeEUOvR/VCMzMTQ7ge8UIzMxNDr0f1QnE9FEOjcPlC" +
                    "r0cVQ5qZ/ULrURZDSOEAQ2dmGEPD9QJDpHAZQ0jhAEOkcBlDmpn9Qh+FG0MVrv9C" +
                    "XI8cQx+F+0LiehpDo3D5QqRwGUOvR/VCZ2YYQ7ge8ULiehpDuB7xQqRwGUPD9exC" +
                    "H4UbQ8P17EJcjxxDuB7xQtejHkM9Cu9CUrggQ7ge8ULNzCJDuB7xQkjhJEO4HvFC" +
                    "w/UmQ7ge8UJI4SRDPQrvQs3MIkM9Cu9Cj8IhQ0jh6kIL1yNDSOHqQkjhJEPXo+RC" +
                    "zcwiQ1K45kKPwiFDXY/iQo/CIUNnZt5CC9cjQ2dm3kJI4SRDcT3aQsP1JkPrUdxC" +
                    "PgopQ2dm3kI+CilDcT3aQsP1JkNxPdpCw/UmQ3sU1kI+CilDAADUQrgeK0N7FNZC" +
                    "uB4rQ4Xr0UI+CilDj8LNQrgeK0MVrstCMzMtQ5qZyUJxPS5DpHDFQq9HL0OamclC" +
                    "KVwxQxWuy0JnZjJDH4XHQqRwM0MpXMNCZ2YyQzMzv0LiejRDMzO/Qh+FNUMpXMNC" +
                    "XI82QzMzv0LXozhDr0fBQlK4OkMzM79CUrg6Qz4Ku0LNzDxDPgq7QkjhPkO4Hr1C" +
                    "w/VAQ7gevUIAAEJDr0fBQnsUREOkcMVCuB5FQ5qZyUIzM0dDFa7LQq9HSUOPws1C" +
                    "cT1IQ4Xr0UJxPUhDexTWQnE9SENxPdpCMzNHQ2dm3kJxPUhDcT3aQutRSkPrUdxC" +
                    "r0dJQ/Yo2ELrUUpDAADUQilcS0ML189Co3BNQwvXz0IfhU9DC9fPQpqZUUOF69FC" +
                    "Fa5TQwAA1EIVrlND9ijYQhWuU0MAANRCkMJVQ4Xr0UIK11dDexTWQgrXV0OF69FC" +
                    "hetZQwvXz0IK11dDj8LNQs3MVkOamclCkMJVQ6RwxULNzFZDr0fBQkjhWEMzM79C" +
                    "hetZQz4Ku0KF61lDSOG2QgAAXEPNzLRCexReQ1K4skI9Cl1DXI+uQj0KXUNnZqpC" +
                    "uB5fQ+tRqEL3KGBD9iikQnA9YkP2KKRCcD1iQwAAoELrUWRDAACgQmdmZkMAAKBC" +
                    "KlxlQwvXm0KjcGdDj8KZQh+FaUMVrpdCXY9qQwvXm0IVrm1DC9ebQpDCb0OPwplC" +
                    "CtdxQwvXm0KF63NDj8KZQgAAdkMVrpdCexR4QxWul0J7FHhDH4WTQnsUeEMpXI9C" +
                    "AAB2QzMzi0KF63NDMzOLQgrXcUMzM4tCkMJvQ7geiUJSuG5Dw/WEQs3McEPD9YRC" +
                    "CtdxQ7geiUIK13FDw/WEQoXrc0NI4YJCAAB2Qz4Kh0I9CndDMzOLQrgeeUO4HolC" +
                    "MzN7QzMzi0JwPXxDKVyPQutRfkMpXI9CMzOAQ6RwkUJxPYFDpHCRQq9HgkOkcJFC" +
                    "61GDQx+Fk0IpXIRDmpmVQmdmhUMVrpdCpHCGQ5qZlULieodDFa6XQh+FiEOPwplC" +
                    "XI+JQ4/CmUKamYpDFa6XQteji0MfhZNCFa6MQ6RwkULXo4tDmpmVQhWujEOamZVC" +
                    "16OLQ4/CmUIVroxDFa6XQjMzjUML15tCcT2OQwAAoEKPwo5DC9ebQnE9jkMVrpdC" +
                    "r0ePQ5qZlULrUZBDmpmVQgvXkEML15tCSOGRQ4/CmUJI4ZFDheudQgvXkEN7FKJC" +
                    "61GQQ3E9pkLrUZBDZ2aqQs3Mj0Ncj65Cj8KOQ1yPrkJSuI1D4nqsQjMzjUPXo7BC" +
                    "Fa6MQ83MtEIVroxDw/W4QjMzjUO4Hr1CFa6MQ69HwUL2KIxDpHDFQrgei0OamclC" +
                    "mpmKQ4/CzUJ7FIpDhevRQrgei0MAANRC9iiMQ3sU1kIzM41DcT3aQnE9jkPrUdxC" +
                    "j8KOQ+J64ELNzI9DXY/iQgvXkEPXo+RCSOGRQ1K45kKF65JDzczoQsP1k0NI4epC" +
                    "AACVQ8P17EI+CpZDw/XsQnsUl0O4HvFCuB6YQ69H9UL2KJlDKlz3QjMzmkOjcPlC" +
                    "cT2bQ6Nw+UKvR5xDH4X7QutRnUOamf1CC9edQ0jhAEMpXJ5Dw/UCQ2dmn0PD9QJD" +
                    "pHCgQz4KBUPieqFDPgoFQx+FokN7FAZDXI+jQ3sUBkOamaRDPgoFQ9ejpUMAAARD" +
                    "Fa6mQ8P1AkMVrqZDSOEAQxWupkOamf1CUrinQ6Nw+UJxPahDr0f1Qo/CqEO4HvFC" +
                    "r0epQ8P17EKvR6lDzczoQq9HqUPXo+RCr0epQ+J64ELNzKlD61HcQutRqkP2KNhC" +
                    "C9eqQwAA1ELrUapDC9fPQs3MqUMVrstCzcypQx+Fx0LrUapDKVzDQs3MqUMzM79C" +
                    "zcypQz4Ku0LNzKlDSOG2QutRqkNSuLJC61GqQ1yPrkLrUapDZ2aqQilcq0NxPaZC" +
                    "Z2asQ3sUokIpXKtDexSiQutRqkN7FKJC61GqQ4XrnUIL16pDj8KZQilcq0OamZVC" +
                    "Z2asQxWul0JnZqxDH4WTQmdmrEMpXI9CheusQzMzi0KF66xDPgqHQqRwrUNI4YJC" +
                    "heusQ6NwfULD9a1Do3B9QgAAr0O4HnVCAACvQ6NwfUI+CrBDzcyAQlyPsEPD9YRC" +
                    "exSxQ7geiUJcj7BDr0eNQnsUsUOkcJFCuB6yQx+Fk0K4HrJDFa6XQrgeskML15tC" +
                    "9iizQ4XrnUIVrrNDexSiQhWus0NxPaZCMzO0Q2dmqkIzM7RDXI+uQlK4tENSuLJC" +
                    "Uri0Q0jhtkIzM7RDPgq7QlK4tEMzM79CcT21Qylcw0KPwrVDH4XHQs3MtkOamclC" +
                    "C9e3Qx+Fx0JI4bhDpHDFQoXruUOkcMVCheu5Q5qZyULD9bpDFa7LQgAAvEML189C" +
                    "Pgq9Q4Xr0UI+Cr1D9ijYQlyPvUPrUdxCXI+9Q+J64EJ7FL5D16PkQnsUvkPNzOhC" +
                    "exS+Q8P17EJ7FL5DuB7xQpqZvkOvR/VC16O/Qypc90L2KMBDH4X7QjMzwUMfhftC" +
                    "Fa7AQxWu/0IzM8FDhesBQ1K4wUMAAARDUrjBQ3sUBkNSuMFD9igIQzMzwUNxPQpD" +
                    "UrjBQ+tRDENxPcJDZ2YOQ4/CwkPiehBDj8LCQ1yPEkPNzMND16MUQwvXxEMVrhVD" +
                    "SOHFQ1K4FkOF68ZDUrgWQ8P1x0OPwhdDAADJQ4/CF0MfhclDC9cZQz4KykOF6xtD" +
                    "exTLQ4XrG0O4HsxDw/UcQ/cozUMAAB5DFa7NQ3sUIENSuM5DexQgQ5DCz0O4HiFD" +
                    "r0fQQzMzI0NwPc9D9igiQ3A9z0NxPSRDkMLPQ+tRJkPNzNBDKVwnQz4AAACamctD" +
                    "16O6Q9ejzEP2KLtD9yjNQzMzvEP3KM1DcT29Q/cozUOvR75D9yjNQ+tRv0P3KM1D" +
                    "KVzAQ/cozUNnZsFD9yjNQ6RwwkPXo8xDZ2bBQ7gezEOkcMJDmpnLQ+J6w0N7FMtD" +
                    "H4XEQ3sUy0Ncj8VDmpnLQ5qZxkOamctD16PHQ1yPykPXo8dDH4XJQ7gex0Ncj8pD" +
                    "mpnGQx+FyUN7FMZD4nrIQ5qZxkOkcMdD16PHQ2dmxkPXo8dDZ2bGQxWuyENI4cVD" +
                    "UrjJQwvXxENSuMlDzczDQzMzyUOPwsJDMzPJQ1K4wUMzM8lDUrjBQ/YoyEMVrsBD" +
                    "16PHQ9ejv0O4HsdDmpm+Q5qZxkN7FL5DXI/FQ1yPvUMfhcRDPgq9Q+J6w0M+Cr1D" +
                    "pHDCQ3sUvkPiesNDXI+9Q6RwwkM+Cr1DZ2bBQwAAvEMpXMBD4nq7Q+tRv0PD9bpD" +
                    "r0e+Q6RwukNxPb1Dheu5QzMzvEOkcLpD9ii7Q+J6u0P2KLtDH4W8Q/You0Ncj71D" +
                    "9ii7Q5qZvkMVrrtD16O/QxWuu0MVrsBDUri8Q1K4wUNSuLxDj8LCQ3E9vUPNzMND" +
                    "Uri8QwvXxENSuLxDSOHFQ3E9vUNI4cVDMzO8Q4XrxkMzM7xDw/XHQxWuu0MAAMlD" +
                    "Fa67Q1yPykMVrrtD";

            byte[] data = Base64.INSTANCE.decode(austBase64);

            polyStream = new LittleEndianDataInputStream(new ByteArrayInputStream(data));

            final int polyCnt = polyStream.readInt();
            for (int i = 0; i < polyCnt; ++i) {
                final int vertCnt = polyStream.readInt();
                final Path pg = new Path(vertCnt);
                for (int j = 0; j < vertCnt; ++j) {
                    final float x = polyStream.readFloat() * scale;
                    final float y = polyStream.readFloat() * scale;
                    pg.add(new LongPoint((int) x, (int) y));
                }
                subjects.add(pg);
            }
        } catch (final IOException e) {
            statusBar.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (polyStream != null) {
                    polyStream.close();
                }
            } catch (final IOException e) {
                statusBar.setText("Error: " + e.getMessage());
            }
        }
        clips.clear();
        final Random rand = new Random();

        final int ellipse_size = 100, margin = 10;
        for (int i = 0; i < vertexCount; ++i) {
            final int w = getWidth() - ellipse_size - margin * 2;
            final int h = getHeight() - ellipse_size - margin * 2;

            final int x = rand.nextInt(w) + margin;
            final int y = rand.nextInt(h) + margin;
            final int size = rand.nextInt(ellipse_size - 20) + 20;
            final Ellipse2D path = new Ellipse2D.Float(x, y, size, size);
            final PathIterator pit = path.getPathIterator(null, 0.1);
            final double[] coords = new double[6];

            final Path clip = new Path();
            while (!pit.isDone()) {
                final int type = pit.currentSegment(coords);
                switch (type) {
                    case PathIterator.SEG_LINETO:
                        clip.add(new LongPoint((int) (coords[0] * scale), (int) (coords[1] * scale)));
                        break;
                    default:
                        break;
                }

                pit.next();
            }

            clips.add(clip);
            updateSolution();
        }

    }

    private LongPoint GenerateRandomPoint(int l, int t, int r, int b, Random rand) {
        return new LongPoint(rand.nextInt(r) + l, rand.nextInt(b) + t);
    }

    public void generateRandomPolygon() {

        final Random rand = new Random();
        final int l = MARGIN;
        final int t = MARGIN;
        final int r = getWidth() - MARGIN;
        final int b = getHeight() - MARGIN;

        subjects.clear();
        clips.clear();

        final Path subj = new Path(vertexCount);
        for (int i = 0; i < vertexCount; ++i) {
            subj.add(GenerateRandomPoint(l, t, r, b, rand));
        }
        subjects.add(subj);

        final Path clip = new Path(vertexCount);
        for (int i = 0; i < vertexCount; ++i) {
            clip.add(GenerateRandomPoint(l, t, r, b, rand));
        }
        clips.add(clip);

        updateSolution();
    }

    public Paths getClips() {
        return clips;
    }

    public ClipType getClipType() {
        return clipType;
    }

    public PolyFillType getFillType() {
        return fillType;
    }

    public Paths getSolution() {
        return solution;
    }

    public Paths getSubjects() {
        return subjects;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (final Path p : subjects) {
            final Polygon s = createPolygonFromPath(p);
            g.setColor(new Color(0xC3, 0xC9, 0xCF, 196));
            g.drawPolygon(s);
            g.setColor(new Color(0xDD, 0xDD, 0xF0, 127));
            g.fillPolygon(s);
        }

        for (final Path p : clips) {
            final Polygon s = createPolygonFromPath(p);

            g.setColor(new Color(0xF9, 0xBE, 0xA6, 196));
            g.drawPolygon(s);
            g.setColor(new Color(0xFF, 0xE0, 0xE0, 127));
            g.fillPolygon(s);
        }

        for (final Path p : solution) {
            final Polygon s = createPolygonFromPath(p);
            if (p.orientation()) {
                g.setColor(new Color(0, 0x33, 0, 255));
            } else {
                g.setColor(new Color(0x33, 0, 0, 255));
            }
            g.drawPolygon(s);
            if (p.orientation()) {
                g.setColor(new Color(0x66, 0xEF, 0x7F, 127));
            } else {
                g.setColor(new Color(0x66, 0x00, 0x00, 127));
            }

            g.fillPolygon(s);
        }

    }

    public void setClipType(ClipType clipType) {
        final ClipType oldType = this.clipType;
        this.clipType = clipType;
        if (oldType != clipType) {
            updateSolution();

        }
    }

    public void setFillType(PolyFillType fillType) {
        final PolyFillType oldType = this.fillType;
        this.fillType = fillType;
        if (oldType != fillType) {
            updateSolution();

        }
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public void setPolygon(PolyType type, Paths paths) {
        switch (type) {
            case CLIP:
                clips.clear();
                clips.addAll(paths);
                break;
            case SUBJECT:
                subjects.clear();
                subjects.addAll(paths);
                break;
            default:
                throw new IllegalStateException();

        }
        updateSolution();

    }

    public void setVertexCount(int value) {
        vertexCount = value;

    }

    public void updateSolution() {
        solution.clear();
        if (clipType != null) {
            final DefaultClipper clp = new DefaultClipper(Clipper.STRICTLY_SIMPLE);
            clp.addPaths(subjects, PolyType.SUBJECT, true);
            clp.addPaths(clips, PolyType.CLIP, true);
            if (clp.execute(clipType, solution, fillType, fillType)) {
                if (offset > 0f) {
                    final ClipperOffset clo = new ClipperOffset();
                    clo.addPaths(solution, JoinType.ROUND, EndType.CLOSED_POLYGON);
                    clo.execute(clips, offset);
                }
                int sum = 0;
                for (final Path p : solution) {
                    sum += p.size();
                }
                statusBar.setText("Operation successful. Solution has " + sum + " vertices.");
            } else {
                statusBar.setText("Operation failed");
            }
        } else {
            statusBar.setText("Operation successful. Solution cleared");
        }
        repaint();
    }
}
