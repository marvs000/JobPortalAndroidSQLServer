package core.marketing.util;

/**
 * Created by ernestepistola on 2/4/17.
 */
public class MarketingObjectDetail {

    public MarketingObjectDetail(String key, String value){
        this.key = key;
        this.value = value;
    }


    private String key;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String value;



}
