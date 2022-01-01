package GameClient.utils;




public class Range2Range {
    private Range2D world, frame;

    public Range2Range(Range2D w, Range2D f) {
        world = new Range2D(w);
        frame = new Range2D(f);
    }


    public Point worldToframe(Point p) {
        Point d = world.getPortion(p);
        Point ans = frame.fromPortion(d);
        return ans;
    }


    public Point frameToWorld(Point p) {
        Point d = frame.getPortion(p);

        return world.fromPortion(d);
    }

    public Range2D getFrame() {
        return frame;
    }
}