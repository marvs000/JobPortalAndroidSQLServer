package core.marketing.interfaces;

import core.marketing.util.MarketingObjectDetail;
import core.marketing.util.MarketingObjectType;

import java.util.List;

/**
 * Created by ernestepistola on 2/4/17.
 */
@Deprecated
public interface IMarketingObject {


    /* Returns either FARE_FEES or PROMO */
    public MarketingObjectType getMarketingObjectType();

    /* Returns a list of all details for a marketing object */
    public List<MarketingObjectDetail> getAdvertismentDetails();


}
