package gangcheng1030.texasholdem.fastgto.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivateCardsConverter {
    public static final String[] allPrivateCardsSummary = {
            "AA", "AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s",
            "AKo", "KK", "KQs", "KJs", "KTs", "K9s", "K8s", "K7s", "K6s", "K5s", "K4s", "K3s", "K2s",
            "AQo", "KQo", "QQ", "QJs", "QTs", "Q9s", "Q8s", "Q7s", "Q6s", "Q5s", "Q4s", "Q3s", "Q2s",
            "AJo", "KJo", "QJo", "JJ", "JTs", "J9s", "J8s", "J7s", "J6s", "J5s", "J4s", "J3s", "J2s",
            "ATo", "KTo", "QTo", "JTo", "TT", "T9s", "T8s", "T7s", "T6s", "T5s", "T4s", "T3s", "T2s",
            "A9o", "K9o", "Q9o", "J9o", "T9o", "99", "98s", "97s", "96s", "95s", "94s", "93s", "92s",
            "A8o", "K8o", "Q8o", "J8o", "T8o", "98o", "88", "87s", "86s", "85s", "84s", "83s", "82s",
            "A7o", "K7o", "Q7o", "J7o", "T7o", "97o", "87o", "77", "76s", "75s", "74s", "73s", "72s",
            "A6o", "K6o", "Q6o", "J6o", "T6o", "96o", "86o", "76o", "66", "65s", "64s", "63s", "62s",
            "A5o", "K5o", "Q5o", "J5o", "T5o", "95o", "85o", "75o", "65o", "55", "54s", "53s", "52s",
            "A4o", "K4o", "Q4o", "J4o", "T4o", "94o", "84o", "74o", "64o", "54o", "44", "43s", "42s",
            "A3o", "K3o", "Q3o", "J3o", "T3o", "93o", "83o", "73o", "63o", "53o", "43o", "33", "32s",
            "A2o", "K2o", "Q2o", "J2o", "T2o", "92o", "82o", "72o", "62o", "52o", "42o", "32o", "22"
    };

    public static Double[] zeroWeights = new Double[allPrivateCardsSummary.length];
    static {
        Arrays.fill(zeroWeights, 0.0);
    }

    public static String[] getAllPrivateCardsSummary() {
        return Arrays.copyOf(allPrivateCardsSummary, allPrivateCardsSummary.length);
    }

    public static Double[] rangeStr2Weights(String rangeStr) {
        List<String> rangeList = Arrays.asList(rangeStr.split(","));
        Map<String, Double> rangeWeightMap = new HashMap<>(allPrivateCardsSummary.length);
        for (String oneRange : rangeList) {
            if (oneRange == null || oneRange.length() == 0) {
                continue;
            }

            List<String> cardstrArr = Arrays.asList(oneRange.split(":"));
            if (cardstrArr.size() > 2 || cardstrArr.size() < 1){
                throw new RuntimeException("':' number exceeded 2");
            }
            double weight = 1;
            if (cardstrArr.size() == 2) {
                weight = Double.parseDouble(cardstrArr.get(1));
            }
            rangeWeightMap.put(cardstrArr.get(0), weight);
        }

        Double[] weights = new Double[allPrivateCardsSummary.length];
        for (int i = 0; i < allPrivateCardsSummary.length; i++) {
            weights[i] = rangeWeightMap.getOrDefault(allPrivateCardsSummary[i], 0.0);
        }

        return weights;
    }
}
