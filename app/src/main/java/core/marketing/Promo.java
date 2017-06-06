package core.marketing;

import core.marketing.util.MarketingObjectDetail;
import core.marketing.util.MarketingObjectType;
import core.marketing.util.MarketingObjectTypeClass;

/**
 * Created by ernestepistola on 2/4/17.
 */
@Deprecated
public class Promo extends MarketingObjectTypeClass {


    public static final String DB_COLUMN_PROMO = "PROMO";
    public static final String DB_COLUMN_BUS_NAME = "BUS_NAME";

    public Promo(String busName, String promoDescription){
        super(MarketingObjectType.PROMO);
        /* Add details to this object */
        this.details.add(new MarketingObjectDetail(DB_COLUMN_BUS_NAME, busName));
        this.details.add(new MarketingObjectDetail(DB_COLUMN_PROMO, promoDescription));
    }

    public String getPromoDescription(){
        return this.getKeyValue(DB_COLUMN_PROMO);
    }

    public void setPromoDescription(String promoDescription){
        this.setKeyValue(DB_COLUMN_PROMO, promoDescription);
    }

    public String getBusName(){
        return this.getKeyValue(DB_COLUMN_BUS_NAME);
    }

    public void setBusName(String busName){
        this.setKeyValue(DB_COLUMN_BUS_NAME, busName);
    }

}
