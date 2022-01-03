package assignemt4.ui;


import assignemt4.models.Info;

/**
 *
 *    This class represent the events to be shown on the screen
 *
 */
public class UIEvents {
    public static class CalculateRange extends UIEvents {

    }


    public static class UpdateUi extends UIEvents {

    }


    public static class Labels extends UIEvents {
        private Info info;
        private int maxMoves;
        private int timeToEnd;

        public Labels(Info info, int maxMoves, int timeToEnd) {
            this.info = info;
            this.maxMoves = maxMoves;
            this.timeToEnd = timeToEnd;
        }

        public Info getInfo() {
            return info;
        }

        public int getMaxMoves() {
            return maxMoves;
        }

        public int getTimeToEnd() {
            return timeToEnd;
        }

        @Override
        public String toString() {
            return "Labels{" +
                    "info=" + info +
                    ", maxMoves=" + maxMoves +
                    ", timeToEnd=" + timeToEnd +
                    '}';
        }
    }

    public static class ShowMessage extends UIEvents {
        private String message;

        public ShowMessage(String message) {
            super();
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
