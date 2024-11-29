package gangcheng1030.texasholdem.fastgto.util;

import java.util.HashMap;
import java.util.Map;

public class FlopCardConvertUtil {
    private static final Map<String, Map<String, String>> converRuleMap = new HashMap<>();
    private static final Map<String, Map<String, String>> formerTwoEqualRuleMap = new HashMap<>();

    public static Map<String, String> getConvertRule(String cardSuits, boolean formerTwoEqual) {
        if (formerTwoEqual && cardSuits.charAt(0) == cardSuits.charAt(2)) {
            return formerTwoEqualRuleMap.get(cardSuits);
        }
        return converRuleMap.get(cardSuits);
    }

    // cdhs
    static {
        converRuleMap.put("ccc", new HashMap<>());
        converRuleMap.put("ccd", new HashMap<>());
        converRuleMap.put("cch", new HashMap<>(){{put("h", "d"); put("d", "h");}});
        converRuleMap.put("ccs", new HashMap<>(){{put("s", "d"); put("d", "s");}});

        converRuleMap.put("cdc", new HashMap<>());
        converRuleMap.put("cdd", new HashMap<>());
        converRuleMap.put("cdh", new HashMap<>());
        converRuleMap.put("cds", new HashMap<>(){{put("s", "h"); put("h", "s");}});

        converRuleMap.put("chc", new HashMap<>(){{put("h", "d"); put("d", "h");}});
        converRuleMap.put("chd", new HashMap<>(){{put("h", "d"); put("d", "h");}});
        converRuleMap.put("chh", new HashMap<>(){{put("h", "d"); put("d", "h");}});
        converRuleMap.put("chs", new HashMap<>(){{put("h", "d"); put("s", "h"); put("d", "s");}});

        converRuleMap.put("csc", new HashMap<>(){{put("s", "d"); put("d", "s");}});
        converRuleMap.put("csd", new HashMap<>(){{put("s", "d"); put("d", "h"); put("h", "s");}});
        converRuleMap.put("csh", new HashMap<>(){{put("s", "d"); put("d", "s");}});
        converRuleMap.put("css", new HashMap<>(){{put("s", "d"); put("d", "s");}});

        converRuleMap.put("dcc", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        converRuleMap.put("dcd", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        converRuleMap.put("dch", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        converRuleMap.put("dcs", new HashMap<>(){{put("c", "d"); put("d", "c"); put("s", "h"); put("h", "s");}});

        converRuleMap.put("ddc", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        converRuleMap.put("ddd", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        converRuleMap.put("ddh", new HashMap<>(){{put("d", "c"); put("h", "d"); put("c", "h");}});
        converRuleMap.put("dds", new HashMap<>(){{put("d", "c"); put("s", "d"); put("c", "s");}});

        converRuleMap.put("dhc", new HashMap<>(){{put("d", "c"); put("h", "d"); put("c", "h");}});
        converRuleMap.put("dhd", new HashMap<>(){{put("d", "c"); put("h", "d"); put("c", "h");}});
        converRuleMap.put("dhh", new HashMap<>(){{put("d", "c"); put("h", "d"); put("c", "h");}});
        converRuleMap.put("dhs", new HashMap<>(){{put("d", "c"); put("h", "d"); put("s", "h"); put("c", "s");}});

        converRuleMap.put("dsc", new HashMap<>(){{put("d", "c"); put("s", "d"); put("c", "h"); put("h", "s");}});
        converRuleMap.put("dsd", new HashMap<>(){{put("d", "c"); put("s", "d"); put("c", "s");}});
        converRuleMap.put("dsh", new HashMap<>(){{put("d", "c"); put("s", "d"); put("c", "s");}});
        converRuleMap.put("dss", new HashMap<>(){{put("d", "c"); put("s", "d"); put("c", "s");}});

        converRuleMap.put("hcc", new HashMap<>(){{put("h", "c"); put("c", "d"); put("d", "h");}});
        converRuleMap.put("hcd", new HashMap<>(){{put("h", "c"); put("c", "d"); put("d", "h");}});
        converRuleMap.put("hch", new HashMap<>(){{put("h", "c"); put("c", "d"); put("d", "h");}});
        converRuleMap.put("hcs", new HashMap<>(){{put("h", "c"); put("c", "d"); put("s", "h"); put("d", "s");}});

        converRuleMap.put("hdc", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        converRuleMap.put("hdd", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        converRuleMap.put("hdh", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        converRuleMap.put("hds", new HashMap<>(){{put("h", "c"); put("s", "h"); put("c", "s");}});

        converRuleMap.put("hhc", new HashMap<>(){{put("h", "c"); put("c", "d"); put("d", "h");}});
        converRuleMap.put("hhd", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        converRuleMap.put("hhh", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        converRuleMap.put("hhs", new HashMap<>(){{put("h", "c"); put("c", "h"); put("s", "d"); put("d", "s");}});

        converRuleMap.put("hsc", new HashMap<>(){{put("h", "c"); put("s", "d"); put("c", "h"); put("d", "s");}});
        converRuleMap.put("hsd", new HashMap<>(){{put("h", "c"); put("s", "d"); put("d", "h"); put("c", "s");}});
        converRuleMap.put("hsh", new HashMap<>(){{put("h", "c"); put("s", "d"); put("c", "h"); put("d", "s");}});
        converRuleMap.put("hss", new HashMap<>(){{put("h", "c"); put("s", "d"); put("c", "h"); put("d", "s");}});

        converRuleMap.put("scc", new HashMap<>(){{put("s", "c"); put("c", "d"); put("d", "s");}});
        converRuleMap.put("scd", new HashMap<>(){{put("s", "c"); put("c", "d"); put("d", "h"); put("h", "s");}});
        converRuleMap.put("sch", new HashMap<>(){{put("s", "c"); put("c", "d"); put("d", "s");}});
        converRuleMap.put("scs", new HashMap<>(){{put("s", "c"); put("c", "d"); put("d", "s");}});

        converRuleMap.put("sdc", new HashMap<>(){{put("s", "c"); put("c", "h"); put("h", "s");}});
        converRuleMap.put("sdd", new HashMap<>(){{put("s", "c"); put("c", "s");}});
        converRuleMap.put("sdh", new HashMap<>(){{put("s", "c"); put("c", "s");}});
        converRuleMap.put("sds", new HashMap<>(){{put("s", "c"); put("c", "s");}});

        converRuleMap.put("shc", new HashMap<>(){{put("s", "c"); put("h", "d"); put("c", "h"); put("d", "s");}});
        converRuleMap.put("shd", new HashMap<>(){{put("s", "c"); put("h", "d"); put("d", "h"); put("c", "s");}});
        converRuleMap.put("shh", new HashMap<>(){{put("s", "c"); put("h", "d"); put("d", "h"); put("c", "s");}});
        converRuleMap.put("shs", new HashMap<>(){{put("s", "c"); put("h", "d"); put("d", "h"); put("c", "s");}});

        converRuleMap.put("ssc", new HashMap<>(){{put("s", "c"); put("c", "d"); put("d", "s");}});
        converRuleMap.put("ssd", new HashMap<>(){{put("s", "c"); put("c", "s");}});
        converRuleMap.put("ssh", new HashMap<>(){{put("s", "c"); put("h", "d"); put("c", "s"); put("d", "h");}});
        converRuleMap.put("sss", new HashMap<>(){{put("s", "c"); put("c", "s");}});

        // -----------------------------------  分割线  -----------------------------------
        formerTwoEqualRuleMap.put("cdc", new HashMap<>(){{put("c", "d"); put("d", "c");}});
        formerTwoEqualRuleMap.put("chc", new HashMap<>(){{put("c", "d"); put("h", "c"); put("d", "h");}});
        formerTwoEqualRuleMap.put("csc", new HashMap<>(){{put("c", "d"); put("s", "c"); put("d", "s");}});
        formerTwoEqualRuleMap.put("dhd", new HashMap<>(){{put("h", "c"); put("c", "h");}});
        formerTwoEqualRuleMap.put("dsd", new HashMap<>(){{put("s", "c"); put("c", "s");}});
        formerTwoEqualRuleMap.put("hsh", new HashMap<>(){{put("s", "c"); put("c", "s"); put("h", "d"); put("d", "h");}});
    }
}
