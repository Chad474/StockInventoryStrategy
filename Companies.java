package package1;

public class Companies {

        private String name;
        private String date;
        private String futureName;
        private String futureDate;
        private double openingPrice;
        private double lowPrice;
        private double highPrice;
        private double closingPrice;
        private double adjustedClosing;
        private double crazyValue;
        private double twoToOneSplit;
        private double threeToOneSplit;
        private double threeToTwoSplit;
        private double futureOpeningPrice;

        /* Default Constructor. */
        public Companies() {
                this.futureOpeningPrice = 0;
        }

        /* Constructor used for storing information about split days. */
        public Companies(String name, String date, double closingPrice, double futureOpeningPrice, double twoToOneSplit,
                        double threeToOneSplit, double threeToTwoSplit) {
                this.name = name;
                this.date = date;
                this.closingPrice = closingPrice;
                this.futureOpeningPrice = futureOpeningPrice;
                this.twoToOneSplit = twoToOneSplit;
                this.threeToOneSplit = threeToOneSplit;
                this.threeToTwoSplit = threeToTwoSplit;
        }

        /* Constructor used for storing information about crazy days. */
        public Companies(String name, String date, double highPrice, double lowPrice, double crazyDay) {
                this.name = name;
                this.date = date;
                this.lowPrice = lowPrice;
                this.highPrice = highPrice;
                this.crazyValue = crazyDay;
        }

        public String getName() {
                return this.name;
        }

        public String getDate() {
                return this.date;
        }
        public String getFutureDate() {
                return this.futureDate;
        }
        public double getFutureOpeningPrice() {
                return this.futureOpeningPrice;
        }
        public void setFutureOpeningPrice(double openingPrice) {
                this.futureOpeningPrice = openingPrice;
        }
        public double getOpeningPrice() {
                return this.openingPrice;
        }

        public double getHighPrice() {
                return this.highPrice;
        }

        public double getLowPrice() {
                return this.lowPrice;
        }

        public double getClosingPrice() {
                return this.closingPrice;
        }

        public double getAdjustedClosing() {
                return this.adjustedClosing;
        }

        public double getCrazyValue() {
                return this.crazyValue;
        }

        public double getTwoToOneSplit() {
                return this.twoToOneSplit;
        }

        public double getThreeToOneSplit() {
                return this.threeToOneSplit;
        }

        public double getThreeToTwoSplit() {
                return this.threeToTwoSplit;
        }

        /* Overrides toString function. Used instead for displaying split days. */
        public String toString() {
                return this.date + getTab() + this.closingPrice + " --> " + this.futureOpeningPrice;
        }
        /* Helper function for toString. */
        public String getTab() {
                if(date.length() < 10)
                        return "\t\t";
                else
                        return "\t";
        }
}
          

